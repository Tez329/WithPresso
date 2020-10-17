package com.example.withpresso

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_review.*

data class Review(var clean: Int, var atmo: Int, var kind: Boolean, var well: Boolean, var comment: String)

class ReviewActivity : AppCompatActivity() {
    lateinit var review: Review

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review)

        /* init */
        review = Review(0, 0, false, false, "")

        /* setOnClickListener */
        review_back_button.setOnClickListener { onBackPressed() }

        review_whole_layout.setOnClickListener {}
        review_clean_card.setOnClickListener {}
        review_atmo_card.setOnClickListener {}
        review_staff_card.setOnClickListener {}
        review_studied_well_card.setOnClickListener {}

        review_clean_group.setOnCheckedChangeListener { group, checkedId ->
            when(review_clean_group.checkedRadioButtonId) {
                R.id.review_clean_bad_radio -> review.clean = 1
                R.id.review_clean_soso_radio -> review.clean = 2
                R.id.review_clean_good_radio -> review.clean = 3
            }
        }
        review_atmo_group.setOnCheckedChangeListener { group, checkedId ->
            when(review_atmo_group.checkedRadioButtonId) {
                R.id.review_atmo_bad_radio -> review.atmo = 1
                R.id.review_atmo_soso_radio -> review.atmo = 2
                R.id.review_atmo_good_radio -> review.atmo = 3
            }
        }
        review_staff_check.setOnCheckedChangeListener { buttonView, isChecked ->
            if(review_staff_check.isChecked) {
                review.kind = true
                Toast.makeText(this, "착한 사람 :)", Toast.LENGTH_SHORT).show()
            }
            else {
                review.kind = false
                Toast.makeText(this, "나쁜 사람 :(", Toast.LENGTH_SHORT).show()
            }
        }
        review_studied_well_check.setOnCheckedChangeListener { buttonView, isChecked ->
            if(review_studied_well_check.isChecked) {
                review.well = true
                Toast.makeText(this, "공부 잘 되는 카페 :)", Toast.LENGTH_SHORT).show()
            }
            else {
                review.well = false
                Toast.makeText(this, "공부 안 되는 카페 :(", Toast.LENGTH_SHORT).show()
            }
        }

        review_comment_edit.setOnClickListener {
            showKeypad(it)
        }
        review_comment_edit.setOnFocusChangeListener { v, hasFocus ->
            if(!hasFocus)
                hideKeypad(v)
        }

        review_submit_button.setOnClickListener {
            if(review.clean == 0)
                Toast.makeText(this, "매장은 깨끗했나요??", Toast.LENGTH_SHORT).show()
            else if(review.atmo == 0)
                Toast.makeText(this, "매장 분위기는 괜찮았나요??", Toast.LENGTH_SHORT).show()
            else{
                Toast.makeText(this, "쿠폰이 발급됐습니다!", Toast.LENGTH_SHORT).show()
                onBackPressed()
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

