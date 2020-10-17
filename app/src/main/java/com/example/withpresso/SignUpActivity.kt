package com.example.withpresso

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.ObjectKey
import com.example.withpresso.R
import kotlinx.android.synthetic.main.activity_my_page.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.io.ByteArrayOutputStream
import java.util.*

class SignUpActivity : AppCompatActivity() {
    private lateinit var pref: SharedPreferences
    private val OPEN_GALLERY = 1

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        /* init */
        pref = getSharedPreferences("user_info", 0)

        /* setLinstener*/
        /* sign_up_whole_layout */
        sign_up_whole_layout.setOnClickListener {}

        /* sign_up_back_button */
        sign_up_back_button.setOnClickListener { onBackPressed() }

        /* sign_up_profile_image */
        sign_up_profile_image.setOnClickListener { openGallery() }

        /* sign_up_email_edit */
        sign_up_email_edit.setOnClickListener { showKeypad(it) }
        sign_up_email_edit.setOnFocusChangeListener { v, hasFocus ->
            if(!hasFocus)
                hideKeypad(v)
        }
        sign_up_email_edit.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.toString().isEmpty())
                    sign_up_email_layout.error = "empty"
                else
                    sign_up_email_layout.error = null
            }

            override fun afterTextChanged(s: Editable?) {
                if(s.toString().isEmpty())
                    sign_up_email_layout.error = "empty"
            }
        })

        /* sign_up_password_edit */
        sign_up_password_edit.setOnClickListener { showKeypad(it) }
        sign_up_password_edit.setOnFocusChangeListener { v, hasFocus ->
            if(!hasFocus)
                hideKeypad(v)
        }
        sign_up_password_edit.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.toString().isEmpty())
                    sign_up_pw_layout.error = "empty"
                else
                    sign_up_pw_layout.error = null
            }

            override fun afterTextChanged(s: Editable?) {
                if(s.toString().isEmpty())
                    sign_up_pw_layout.error = "empty"
            }
        })

        /* sign_up_password_check_edit */
        sign_up_password_check_edit.setOnClickListener { showKeypad(it) }
        sign_up_password_check_edit.setOnFocusChangeListener { v, hasFocus ->
            if(!hasFocus)
                hideKeypad(v)
        }
        sign_up_password_check_edit.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val pw = sign_up_password_edit.text.toString()

                if(pw == s.toString())
                    sign_up_pw_check_layout.error = null
                else
                    sign_up_pw_check_layout.error = "not the same"
            }

            override fun afterTextChanged(s: Editable?) {
                val pw = sign_up_password_edit.text.toString()

                if(pw != s.toString())
                    sign_up_pw_check_layout.error = "not the same"
            }
        })

        /* sign_up_phone_edit */
        sign_up_phone_edit.setOnClickListener {
            showKeypad(it)
        }
        sign_up_phone_edit.setOnFocusChangeListener { v, hasFocus ->
            if(!hasFocus)
                hideKeypad(v)
        }
        sign_up_phone_edit.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.toString().isEmpty())
                    sign_up_nickname_layout .error = "empty"
                else
                    sign_up_nickname_layout.error = null
            }

            override fun afterTextChanged(s: Editable?) {
                if(s.toString().isEmpty())
                    sign_up_nickname_layout.error = "empty"
            }
        })

        /* sign_up_nickname_edit */
        sign_up_nickname_edit.setOnClickListener {
            showKeypad(it)
        }
        sign_up_nickname_edit.setOnFocusChangeListener { v, hasFocus ->
            if(!hasFocus)
                hideKeypad(v)
        }
        sign_up_nickname_edit.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.toString().isEmpty())
                    sign_up_nickname_layout .error = "empty"
                else
                    sign_up_nickname_layout.error = null
            }

            override fun afterTextChanged(s: Editable?) {
                if(s.toString().isEmpty())
                    sign_up_nickname_layout.error = "empty"
            }
        })

        /* sign_up_confirm_button */
        sign_up_confirm_button.setOnClickListener {
            if (sign_up_email_edit.text.toString().isEmpty()) {
                sign_up_email_layout.error = "empty"
                Toast.makeText(this, "enter the email address", Toast.LENGTH_SHORT).show()
            }
            else if (sign_up_password_edit.text.toString().isEmpty()){
                sign_up_pw_layout.error = "empty"
                Toast.makeText(this, "enter the password address", Toast.LENGTH_SHORT).show()
            }
            else if(sign_up_password_edit.text.toString() != sign_up_password_check_edit.text.toString()) {
                sign_up_pw_layout.error = "not the same"
                Toast.makeText(this, "password does not matched", Toast.LENGTH_SHORT).show()
            }
            else if(sign_up_nickname_edit.text.toString().isEmpty()) {
                sign_up_nickname_layout.error = "empty"
                Toast.makeText(this, "enter the nickname address", Toast.LENGTH_SHORT).show()
            }
            else {
                val edit = pref.edit()
                edit.putString("email", sign_up_email_edit.text.toString())
                edit.putString("password", sign_up_password_edit.text.toString())
                edit.putString("phone", sign_up_phone_edit.text.toString())
                edit.putString("nickname", sign_up_nickname_edit.text.toString())
                edit.commit()

                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }
    }

    private fun showKeypad(view: View) {
        view.requestFocus()
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, 0)
    }

    private fun hideKeypad(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
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

            try {
                val signature = System.currentTimeMillis().toString()
                Glide.with(this)
                    .asBitmap()
                    .load(currentImageUri)
                    .signature(ObjectKey(signature))
                    .into(sign_up_profile_image)

                val edit = pref.edit()
                edit.putString("profile_uri", currentImageUri.toString())
                edit.putString("profile_sig", signature)
                edit.commit()
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