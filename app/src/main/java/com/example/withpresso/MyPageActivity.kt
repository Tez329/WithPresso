package com.example.withpresso

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.withpresso.service.ProfileReplaceService
import kotlinx.android.synthetic.main.activity_my_page.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File


class MyPageActivity : AppCompatActivity() {
    private val RECORD_REQUEST_CODE = 1000
    private val TAG = "PermissionDemo"
    private val OPEN_GALLERY = 1
    private val GALLERY_ACCESS_REQUEST_CODE = 1001

    private lateinit var pref: SharedPreferences
    private lateinit var retrofit: Retrofit


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_page)

        /* init */
        pref = getSharedPreferences("user_info", 0)
        retrofit = Retrofit.Builder()
            .baseUrl("http://ec2-3-34-119-217.ap-northeast-2.compute.amazonaws.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        my_page_nickname_text.text = pref.getString("nickname", "")
        my_page_email_text.text = pref.getString("email", "")

        /* setOnClickListener */
        my_page_whole_layout.setOnClickListener {}

        my_page_back_button.setOnClickListener { onBackPressed() }

        my_page_profile_image.setOnClickListener { permissionCheckAndOpenGallery() }

        my_page_coupon_layout.setOnClickListener {
            val intent = Intent(this, CouponActivity::class.java)
            startActivity(intent)
        }

        my_page_survey_layout.setOnClickListener {
            val intent = Intent(this, SurveyActivity::class.java)
            startActivity(intent)
        }

        my_page_logout_layout.setOnClickListener {
            val edit = pref.edit()
            edit.clear().commit()

            onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()

        /*
        * pref에 url이 있으면 glide를 이용해서 그리기
        *               없으면 기본 이미지 그리기
        * */
        if(pref.contains("profile_url")) {
            val profileUrl = pref.getString("profile_url", "")

            Glide.with(this)
                .load(profileUrl!!)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(my_page_profile_image)
        }
        else {
            val defaultUri = Uri.parse(
                "android.resource://${R::class.java.`package`}/${this.resources.getIdentifier(
                    "default_profile",
                    "drawable",
                    this.packageName
                )}"
            )
            Glide.with(this)
                .load(defaultUri)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(my_page_profile_image)
        }
    }

    /* 갤러리 열어서 사진 선택 */
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        startActivityForResult(intent, OPEN_GALLERY)
    }

    /*
    * 1. glide: profile 선택한 사진으로 다시 그려주기
    * 2. retrofit: 이미지 서버로 보내고 url 받아오기
    * 3. pref: {"profile_url": '받아온 url'} 저장
    * */
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == OPEN_GALLERY && resultCode == Activity.RESULT_OK) {
            val profileUri : Uri? = data?.data
            Log.d("Uri", profileUri.toString())

            try {
                /* Glide를 사용해서 image view 그리기 */
                val signature = System.currentTimeMillis().toString()
                Glide.with(this)
                    .load(profileUri)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .into(my_page_profile_image)

                /* 이렇게 교체하고 리턴값으로 새로 저장한 profile의 url 받아오기. url값을 pref에 저장하기 */
                val profileReplaceService = retrofit.create(ProfileReplaceService::class.java)
                val absPath = absolutelyPath(profileUri!!)
                val file = File(absPath)
                val requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file)
                val multipartBody = MultipartBody.Part.createFormData(
                    "new_profile", file.name, requestBody
                )
                profileReplaceService.requestProfileReplacement(multipartBody).enqueue(object : Callback<String> {
                    override fun onResponse(call: Call<String>, response: Response<String>) {
                        Log.d("profile image update", "success")

                        /* 반환값을 url로 받아서 pref에 저장하기*/
                        val profileReplaceCheck = response.body()
                        val dialog = AlertDialog.Builder(this@MyPageActivity)
                        dialog.setTitle("통신 성공")

                        if(profileReplaceCheck == "1")
                            dialog.setMessage("업데이트 성공")
                        else
                            dialog.setMessage("업데이트 실패")

                        dialog.show()
                    }
                    override fun onFailure(call: Call<String>, t: Throwable) {
                        Log.d("profile update fail", t.message!!)
                        AlertDialog.Builder(this@MyPageActivity)
                            .setTitle("통신 실패")
                            .setMessage("Error occurred.")
                            .show()
                    }
                })
            }
            catch (e: Exception) {
                e.printStackTrace()
            }
        }
        else {
            Log.d("ActivityResult", "1. not select new image 2. failed to load new image")
        }
    }

    /* 전달된 file uri를 절대 경로로 바꿔주는 함수 */
    private fun absolutelyPath(path: Uri): String {
        var proj: Array<String> = arrayOf(MediaStore.Images.Media.DATA)
        var c = contentResolver.query(path, proj, null, null, null)!!
        var index = c.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        c.moveToFirst()

        return c.getString(index)
    }

    /* permission */
    private fun permissionCheckAndOpenGallery() {
        val permission = ContextCompat.checkSelfPermission(
            this, Manifest.permission.READ_EXTERNAL_STORAGE
        )

        /* 이미 갤러리 접근을 허용 했음 */
        if(permission == PermissionChecker.PERMISSION_GRANTED){
            openGallery()
        }
        /* 갤러리 접근이 허용된 적 없음 */
        else {
            val isAccessDenied = ActivityCompat.shouldShowRequestPermissionRationale(
                this, Manifest.permission.READ_EXTERNAL_STORAGE
            )

            /* 최초로 갤러리에 접근 시도 */
            if(!isAccessDenied) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    GALLERY_ACCESS_REQUEST_CODE
                )
            }
            /* 갤러리 접근 권한 제한됨 */
            else {
                val dialog = AlertDialog.Builder(this)
                dialog.setTitle("갤러리 접근 권한 제한됨")
                dialog.setMessage("  갤러리 접근을 허용해 주세요.\n\n" +
                        "  app info -> Permissions -> storage")
                dialog.show()
            }
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode) {
            /* 갤러리에 접근이 허용되면 갤러리 열기 */
            GALLERY_ACCESS_REQUEST_CODE -> {
                if(grantResults.isNotEmpty() && grantResults[0] == PermissionChecker.PERMISSION_GRANTED) {
                    openGallery()
                }
            }
        }

        return
    }

}