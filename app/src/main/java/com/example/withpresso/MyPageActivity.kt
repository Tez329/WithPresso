package com.example.withpresso

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.core.net.toFile
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.withpresso.service.ProfileReplaceService
import kotlinx.android.synthetic.main.activity_main.*
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
    private val OPEN_GALLERY = 1
    private val GALLERY_ACCESS_REQUEST_CODE = 1001

    private lateinit var pref: SharedPreferences
    private lateinit var retrofit: Retrofit
    private lateinit var BASE_URL: String


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_page)

        /* init */
        pref = getSharedPreferences("user_info", 0)
        BASE_URL = "http://ec2-3-34-119-217.ap-northeast-2.compute.amazonaws.com"
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
        var profileUrl: String? = null

        if(pref.getString("profile", "")!!.isNotBlank()) {
            val userUniqNum = pref.getInt("uniq_num", 0)
            val profileName = pref.getString("profile", "")
            profileUrl = "${BASE_URL}/profile/${userUniqNum}/${profileName}"
        }

        drawProfile(this@MyPageActivity, profileUrl, my_page_profile_image)
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

    /* 갤러리 열어서 사진 선택 */
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        startActivityForResult(intent, OPEN_GALLERY)
    }

    /*
    * 1. pref: 선택한 사진 파일 이름을 "profile"의 value로 저장
    * 2. glide: profile 이미지 다시 그려주기. 이때 diskCache는 RESOURCE로 설정
    * 3. retrofit: 이미지 서버로 보내기
    * */
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == OPEN_GALLERY && resultCode == Activity.RESULT_OK) {
            val profileUri : Uri? = data?.data
            Log.d("Uri", profileUri.toString())

            try {
                /* 갤러리에서 사진을 선택하지 않았을 때 실행 */
                if(profileUri.toString().isNullOrEmpty()) {
                    drawProfile(
                        this@MyPageActivity,
                        null,
                        my_page_profile_image
                    )
                }
                /* 갤러리에서 사진을 선택했을 때 실행 */
                else {
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
                            val newProfileUpdataResult = response.body()

                            /* 새로운 이미지가 정상적으로 반영됐을 때 실행 */
                            if(newProfileUpdataResult!!.isNotEmpty()) {
                                /* pref에 이미지 이름 저장하기 */
                                val fileName = file.name
                                val edit = pref.edit()
                                edit.putString("profile", fileName)
                                edit.apply()

                                /* Glide를 사용해서 image view 그리기 */
                                val userUniqNum = pref.getInt("uniq_num", 0)
                                val profileName = pref.getString("profile", "")
                                val profileUrl = "${BASE_URL}/profile/${userUniqNum}/${profileName}"
                                drawProfile(
                                    this@MyPageActivity,
                                    profileUrl,
                                    my_page_profile_image
                                )

                                /* 업데이트 성공 토스트 띄우기 */
                                Toast.makeText(
                                    this@MyPageActivity,
                                    "프로필 이미지가 정상적으로 업데이트 됐습니다.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            else {
                                AlertDialog.Builder(this@MyPageActivity)
                                    .setTitle("프로필 이미지 업데이트 실패")
                                    .setMessage("변경된 이미지가 반영되지 않았습니다.")
                                    .show()
                            }
                        }
                        override fun onFailure(call: Call<String>, t: Throwable) {
                            Log.d("profile update fail", t.message!!)
                            AlertDialog.Builder(this@MyPageActivity)
                                .setTitle("프로필 이미지 업데이트 실패")
                                .setMessage("통신 오류")
                                .show()
                        }
                    })
                }
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

    private fun drawProfile(context: Context, profileUri: String?, des: ImageView) {
        if(profileUri.isNullOrBlank()) {
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
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .into(des)
        }
        else {
            Glide.with(context)
                .load(profileUri)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .into(des)
        }
    }
}
