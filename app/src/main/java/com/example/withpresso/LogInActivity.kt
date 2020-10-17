package com.example.withpresso

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_log_in.*

class LogInActivity : AppCompatActivity() {
    private lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        /* init */
        preferences = getSharedPreferences("user_info", 0)

        /* setOnClickListener */
        log_in_layout.setOnClickListener {}

        log_in_back_button.setOnClickListener { onBackPressed() }

        log_in_email_edit.setOnClickListener { showKeypad(it) }
        log_in_email_edit.setOnFocusChangeListener { v, hasFocus ->
            if(!hasFocus)
                hideKeypad(v)
        }

        log_in_password_edit.setOnClickListener { showKeypad(it) }
        log_in_password_edit.setOnFocusChangeListener { v, hasFocus ->
            if(!hasFocus)
                hideKeypad(v)
        }

        log_in_button.setOnClickListener {
            hideKeypad(it)

            val email = log_in_email_edit.text.toString()
            val password = log_in_password_edit.text.toString()

            /* 회원인지 확인, 나중에 서버에 요청을 보내서 확인하는 방식으로 바꾸기 */
            if(email == "junhee329@naver.com"  && password == "0329") {
                val edit = preferences.edit()
                edit.putString("email", email)
                edit.putString("password", password)
                edit.commit()

                Toast.makeText(this, "SUCCESS", Toast.LENGTH_SHORT).show()

                onBackPressed()
            }
            else
                Toast.makeText(this, "SIGN UP AND GET RECOMMENDATION", Toast.LENGTH_SHORT).show()
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