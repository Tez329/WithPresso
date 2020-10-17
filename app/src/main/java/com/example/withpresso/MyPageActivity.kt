package com.example.withpresso

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.transition.Transition
import android.util.Log
import android.util.LruCache
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.signature.ObjectKey
import kotlinx.android.synthetic.main.activity_my_page.*
import java.io.ByteArrayOutputStream
import java.util.*


class MyPageActivity : AppCompatActivity() {
    private lateinit var pref: SharedPreferences
    private lateinit var memCache: LruCache<String, Bitmap>

    private val OPEN_GALLERY = 1

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_page)


        /* init */
        pref = getSharedPreferences("user_info", 0)

        val bitmapString = pref.getString("profile", "")
        val bitmap = stringToBitmap(bitmapString!!)

        my_page_profile_image.setImageBitmap(bitmap)
        my_page_nickname_text.text = pref.getString("nickname", "")
        my_page_email_text.text = pref.getString("email", "")

        /* setOnClickListener */
        my_page_whole_layout.setOnClickListener {}

        my_page_back_button.setOnClickListener { onBackPressed() }

        my_page_profile_image.setOnClickListener { openGallery() }

        my_page_coupon_layout.setOnClickListener {
            val intent = Intent(this, CouponActivity::class.java)
            startActivity(intent)
        }

        my_page_survey_layout.setOnClickListener {
            val intent = Intent(this, SurveyActivity::class.java)
            startActivity(intent)
        }

        my_page_logout_layout.setOnClickListener {
            val defaultBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.default_profile)
            val bitmapString = bitmapToString(defaultBitmap)
            val edit = pref.edit()
            edit.clear()
            edit.putString("profile", bitmapString)
            edit.commit()

            onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()

        val uri = pref.getString("profile_uri", "")
        val signature = pref.getString("profile_sig", "")

        Glide.with(this)
            .asBitmap()
            .load(uri)
            .signature(ObjectKey(signature!!))
            .centerCrop()
            .into(my_page_profile_image)

        /*
            val email = pref.getString("email", "")
            val password = pref.getString("password", "")
            val profile = pref.getString("profile", "")
            val nickname = pref.getString("nickname", "")

            val edit = pref.edit()
            edit.putString("email", email)
            edit.putString("password", password)
            edit.putString("profile", profile)
            edit.putString("nickname", nickname)
            edit.commit()
        */
    }

    override fun onPause() {
        super.onPause()

        val email = pref.getString("email", "")
        val password = pref.getString("password", "")
        val profile = pref.getString("profile", "")
        val nickname = pref.getString("nickname", "")

        val edit = pref.edit()
        edit.putString("email", email)
        edit.putString("password", password)
        edit.putString("profile", profile)
        edit.putString("nickname", nickname)
        edit.commit()
    }

    /* 갤러리 열어서 사진 선택 */
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        startActivityForResult(intent, OPEN_GALLERY)
    }

    /* 선택한 사진을 bitmap으로 반환해서 profile 그리기. + string으로 변환해서 sharedpreferences에 저장*/
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == OPEN_GALLERY && resultCode == Activity.RESULT_OK) {
            val currentImageUri : Uri? = data?.data
            Log.d("Uri", currentImageUri.toString())

            try {
                // var bitmap = MediaStore.Images.Media.getBitmap(contentResolver, currentImageUri)
                /* uri에서 bitmap추출 후 image view에 그리기 */
                /*
                    val parcelFileDescriptor = contentResolver.openFileDescriptor(currentImageUri!!, "r")
                    val fileDescriptor = parcelFileDescriptor!!.fileDescriptor
                    val bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor)
                    parcelFileDescriptor.close()
                    my_page_profile_image.setImageBitmap(bitmap)
                */

                /* Glide를 사용해서 bitmap 추출 + image view에 그리기 */
                val signature = System.currentTimeMillis().toString()
                Glide.with(this)
                    .asBitmap()
                    .load(currentImageUri)
                    .signature(ObjectKey(signature))
                    .into(my_page_profile_image)

                val edit = pref.edit()
                edit.putString("profile_uri", currentImageUri.toString())
                edit.putString("profile_sig", signature)
                edit.commit()


                /*
                    /* bitmap을 문자열로 encoding */
                    val bitmapString = bitmapToString(bitmap)
                    Log.d("bitmap to string", bitmapString)

                    /* encoding된 string을 SharedPreferences에 저장 */
                    val edit = pref.edit()
                    edit.putString("profile", bitmapString)
                    edit.commit()
                    Log.d("save", "success")
                */
            }
            catch (e: Exception) {
                e.printStackTrace()
            }
        }
        else {
            Log.d("ActivityResult", "something wrong")
        }
    }

    /* bitmap -> byte array -> string으로 변환 */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun bitmapToString(bitmap: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)

        val byteArray = baos.toByteArray()
        val byteString = Base64.getEncoder().encodeToString(byteArray)

        return byteString
    }

    /* string -> byte array -> bitmap으로 변환 */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun stringToBitmap(string: String): Bitmap? {
        try {
            val byteArray = Base64.getDecoder().decode(string)
            val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)

            return bitmap
        }
        catch (e: java.lang.Exception) {
            e.printStackTrace()
            return null
        }
    }
}