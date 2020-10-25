package com.example.withpresso

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.example.withpresso.service.Login
import com.example.withpresso.service.LoginService
import kotlinx.android.synthetic.main.activity_log_in.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LogInActivity : AppCompatActivity() {
    private lateinit var preferences: SharedPreferences
    private lateinit var retrofit: Retrofit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        /* init */
        preferences = getSharedPreferences("user_info", 0)
        retrofit = Retrofit.Builder()
            .baseUrl("http://ec2-3-34-119-217.ap-northeast-2.compute.amazonaws.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        /* setOnClickListener */
        log_in_layout.setOnClickListener {}

        log_in_back_button.setOnClickListener { onBackPressed() }

        log_in_email_edit.setOnClickListener { showKeypad(it) }
        log_in_email_edit.setOnFocusChangeListener { v, hasFocus ->
            if(!hasFocus)
                hideKeypad(v)
        }
        log_in_email_edit.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.toString().isEmpty())
                    log_in_email_layout.error = "empty"
                else
                    log_in_email_layout.error = null
            }

            override fun afterTextChanged(s: Editable?) {
                if(s.toString().isEmpty())
                    log_in_email_layout.error = "empty"
            }
        })

        log_in_password_edit.setOnClickListener { showKeypad(it) }
        log_in_password_edit.setOnFocusChangeListener { v, hasFocus ->
            if(!hasFocus)
                hideKeypad(v)
        }
        log_in_password_edit.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.toString().isEmpty())
                    log_in_password_layout .error = "empty"
                else
                    log_in_password_layout.error = null
            }

            override fun afterTextChanged(s: Editable?) {
                if(s.toString().isEmpty())
                    log_in_password_layout.error = "empty"
            }
        })

        log_in_button.setOnClickListener {
            hideKeypad(it)

            val email = log_in_email_edit.text.toString()
            val password = log_in_password_edit.text.toString()

            if(email.isEmpty()) {
                log_in_email_layout.error = "empty"
                Toast.makeText(this, "이메일을 입력하세요.", Toast.LENGTH_SHORT).show()
            }
            else if(password.isEmpty()) {
                log_in_password_layout.error = "empty"
                Toast.makeText(this, "비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show()
            }
            else {
                val loginService = retrofit.create(LoginService::class.java)

                loginService.requestLogin(email, password).enqueue(object :Callback<Login> {
                    /* 통신이 성공했을 때 */
                    override fun onResponse(call: Call<Login>, response: Response<Login>) {
                        val userInfo = response.body()

                        if(userInfo!!.uniq_num != 0) {
                            val edit = preferences.edit()
                            edit.putInt("uniq_num", userInfo.uniq_num)
                            edit.putString("email", email)
                            edit.putString("password", password)
                            edit.putString("nickname", userInfo.nickname)
                            edit.putString("profile", userInfo.profile)
                            edit.apply()

                            Toast.makeText(
                                this@LogInActivity,
                                "로그인 성공",
                                Toast.LENGTH_SHORT).show()

                            onBackPressed()
                        }
                        else {
                            AlertDialog.Builder(this@LogInActivity)
                                .setTitle("로그인 실패")
                                .setMessage("존재하지 않는 계정입니다.")
                                .show()
                        }
                    }
                    /* 통신이 실패하면 출력 */
                    override fun onFailure(call: Call<Login>, t: Throwable) {
                        Log.d("login", t.message!!)
                        AlertDialog.Builder(this@LogInActivity)
                            .setTitle("로그인 실패")
                            .setMessage("통신 오류")
                            .show()
                    }
                })
            }
        }

        sign_up_mem_button.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
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