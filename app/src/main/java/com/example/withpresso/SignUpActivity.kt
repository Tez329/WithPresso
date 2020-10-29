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
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.ObjectKey
import com.example.withpresso.service.EmailDupConfirmService
import com.example.withpresso.service.SignUpService
import kotlinx.android.synthetic.main.activity_sign_up.*
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import java.io.ByteArrayOutputStream
import java.util.*

class SignUpActivity : AppCompatActivity() {
    private lateinit var pref: SharedPreferences
    private lateinit var retrofit: Retrofit
    private val OPEN_GALLERY = 1
    private var emailDupChecked = false

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        /* init */
        pref = getSharedPreferences("user_info", 0)
        retrofit = Retrofit.Builder()
            .baseUrl("http://ec2-3-34-119-217.ap-northeast-2.compute.amazonaws.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        /* set on event linstener */
        /* sign_up_whole_layout */
        sign_up_whole_layout.setOnClickListener {}

        /* sign_up_back_button */
        sign_up_back_button.setOnClickListener { onBackPressed() }

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
                    sign_up_email_layout.error = "이메일을 입력해주세요"
                else
                    sign_up_email_layout.error = null
            }

            override fun afterTextChanged(s: Editable?) {
                if(s.toString().isEmpty())
                    sign_up_email_layout.error = "이메일을 입력해주세요"
            }
        })

        /* sign_up_email_dup_check_button */
        sign_up_email_dup_check_button.setOnClickListener {
            val email = sign_up_email_edit.text.toString()
            if(email.isEmpty()) {
                Toast.makeText(this, "이메일을 입력해주세요", Toast.LENGTH_SHORT).show()
                sign_up_email_layout.error = "이메일을 입력해주세요"
            }
            else {
                val emailDupConfirmService = retrofit.create(EmailDupConfirmService::class.java)
                emailDupConfirmService.requestIdDupConfirm(email).enqueue(object : Callback<String> {
                    /* 통신이 성공하면 출력. 결과값이 맞는 지는 알아서 판단해줘야 함. */
                    override fun onResponse(call: Call<String>, response: Response<String>) {
                        val idDupCheck = response.body()
                        val dialog = AlertDialog.Builder(this@SignUpActivity)

                        if (idDupCheck == "0") {
                            emailDupChecked = true
                            sign_up_email_layout.error = null
                            Toast.makeText(
                                this@SignUpActivity,
                                "사용 가능한 이메일입니다",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            sign_up_email_layout.error = "이미 사용 중인 이메일입니다"
                            dialog.setTitle("중복 확인 실패")
                            dialog.setMessage("이미 사용 중인 이메일입니다")
                            dialog.show()
                        }
                    }
                    /* 통신이 실패하면 출력 */
                    override fun onFailure(call: Call<String>, t: Throwable) {
                        Log.d("id duplicate check", t.message!!)
                        val dialog = AlertDialog.Builder(this@SignUpActivity)
                        dialog.setTitle("중복 확인 실패")
                        dialog.setMessage("통신 오류")
                        dialog.show()
                    }
                })
            }
        }

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
                    sign_up_pw_layout.error = "비밀번호를 입력해주세요"
                else
                    sign_up_pw_layout.error = null
            }

            override fun afterTextChanged(s: Editable?) {
                if(s.toString().isEmpty())
                    sign_up_pw_layout.error = "비밀번호를 입력해주세요"
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
                    sign_up_pw_check_layout.error = "비밀번호가 다릅니다"
            }

            override fun afterTextChanged(s: Editable?) {
                val pw = sign_up_password_edit.text.toString()

                if(pw != s.toString())
                    sign_up_pw_check_layout.error = "비밀번호가 다릅니다"
            }
        })

        /* sign_up_phone_edit */
        sign_up_phone_edit.setOnClickListener { showKeypad(it) }
        sign_up_phone_edit.setOnFocusChangeListener { v, hasFocus ->
            if(!hasFocus)
                hideKeypad(v)
        }
        sign_up_phone_edit.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.toString().isEmpty())
                    sign_up_nickname_layout .error = "전화번호를 입력해주세요"
                else
                    sign_up_nickname_layout.error = null
            }

            override fun afterTextChanged(s: Editable?) {
                if(s.toString().isEmpty())
                    sign_up_nickname_layout.error = "전화번호를 입력해주세요"
            }
        })

        /* sign_up_nickname_edit */
        sign_up_nickname_edit.setOnClickListener { showKeypad(it) }
        sign_up_nickname_edit.setOnFocusChangeListener { v, hasFocus ->
            if(!hasFocus)
                hideKeypad(v)
        }
        sign_up_nickname_edit.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.toString().isEmpty())
                    sign_up_nickname_layout .error = "닉네임을 입력해주세요"
                else
                    sign_up_nickname_layout.error = null
            }

            override fun afterTextChanged(s: Editable?) {
                if(s.toString().isEmpty())
                    sign_up_nickname_layout.error = "닉네임을 입력해주세요"
            }
        })

        /* sign_up_confirm_button */
        sign_up_confirm_button.setOnClickListener {
            if (sign_up_email_edit.text.toString().isEmpty()) {
                sign_up_email_layout.error = "이메일을 입력해주세요"
                Toast.makeText(this, "이메일을 입력해주세요", Toast.LENGTH_SHORT).show()
            }
            else if (!emailDupChecked) {
                sign_up_email_layout.error = "사용 가능한 아이디인지 확인해주세요"
                Toast.makeText(this, "사용 가능한 아이디인지 확인해주세요.", Toast.LENGTH_SHORT).show()
            }
            else if (sign_up_password_edit.text.toString().isEmpty()){
                sign_up_pw_layout.error = "비밀번호를 입력해주세요"
                Toast.makeText(this, "비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show()
            }
            else if(sign_up_password_edit.text.toString() != sign_up_password_check_edit.text.toString()) {
                sign_up_pw_layout.error = "비밀번호가 다릅니다"
                Toast.makeText(this, "비밀번호가 다릅니다", Toast.LENGTH_SHORT).show()
            }
            else if(sign_up_nickname_edit.text.toString().isEmpty()) {
                sign_up_nickname_layout.error = "닉네임을 입력해주세요"
                Toast.makeText(this, "닉네임을 입력해주세요", Toast.LENGTH_SHORT).show()
            }
            else {
                val signUpService = retrofit.create(SignUpService::class.java)
                val email = sign_up_email_edit.text.toString()
                val password = sign_up_password_edit.text.toString()
                val phone = sign_up_phone_edit.text.toString()
                val nickname = sign_up_nickname_edit.text.toString()

                signUpService.requestSignUp(email, password, nickname, phone).enqueue(object :Callback<String>{
                    /* 통신 성공 시 실행 */
                    override fun onResponse(call: Call<String>, response: Response<String>) {
                        val user_uniq_num = response.body()

                        if(user_uniq_num != "0") {
                            /* 계정 정보를 pref에 저장 */
                            val edit = pref.edit()
                            edit.putInt("uniq_num", user_uniq_num!!.toInt())
                            edit.putString("email", email)
                            edit.putString("password", password)
                            edit.putString("nickname", nickname)
                            edit.apply()

                            /* 성공 토스트 띄우기 */
                            Toast.makeText(this@SignUpActivity, "회원 가입 성공", Toast.LENGTH_SHORT).show()

                            /* MainActivity로 이동 */
                            val intent = Intent(this@SignUpActivity, MainActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }
                        else {
                            val dialog = AlertDialog.Builder(this@SignUpActivity)
                            dialog.setTitle("회원 가입 실패")
                            dialog.setMessage("회원 가입에 실패했습니다.")
                            dialog.show()
                        }
                    }
                    /* 통신 실패 시 실행 */
                    override fun onFailure(call: Call<String>, t: Throwable) {
                        Log.d("Sign up", t.message!!)
                        val dialog = AlertDialog.Builder(this@SignUpActivity)
                        dialog.setTitle("회원 가입 실패")
                        dialog.setMessage("통신 오류")
                        dialog.show()
                    }

                })
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
}