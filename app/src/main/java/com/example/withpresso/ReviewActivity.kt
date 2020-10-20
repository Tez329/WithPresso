package com.example.withpresso

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_review.*

data class Review(var cafe_clean: Int, var rest_clean: Int, var kind: Int, var atmo: Int, var studied_well: Int, var comment: String)

class ReviewActivity : AppCompatActivity() {
    lateinit var review: Review

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review)

        /* init */
        review = Review(0, 0, 0, 0, 0, "")

        /* setOnClickListener */
        review_back_button.setOnClickListener { onBackPressed() }

        review_whole_layout.setOnClickListener {}
        review_cafe_clean_card.setOnClickListener {}
        review_rest_clean_card.setOnClickListener {}
        review_atmo_card.setOnClickListener {}
        review_staff_kind_card.setOnClickListener {}
        review_studied_well_card.setOnClickListener {}

        review_cafe_clean_group.setOnCheckedChangeListener { group, checkedId ->
            when(review_cafe_clean_group.checkedRadioButtonId) {
                R.id.review_cafe_very_dirty_radio -> review.cafe_clean = 1
                R.id.review_cafe_dirty_radio -> review.cafe_clean = 2
                R.id.review_cafe_soso_radio -> review.cafe_clean = 3
                R.id.review_cafe_clean_radio -> review.cafe_clean = 4
                R.id.review_cafe_very_clean_radio -> review.cafe_clean = 5
            }
        }
        review_rest_clean_group.setOnCheckedChangeListener { group, checkedId ->
            when(review_rest_clean_group.checkedRadioButtonId) {
                R.id.review_rest_very_dirty_radio -> review.rest_clean = 1
                R.id.review_rest_dirty_radio -> review.rest_clean = 2
                R.id.review_rest_soso_radio -> review.rest_clean = 3
                R.id.review_rest_clean_radio -> review.rest_clean = 4
                R.id.review_rest_very_clean_radio -> review.rest_clean = 5
            }
        }
        review_staff_kind_group.setOnCheckedChangeListener { group, checkedId ->
            when(review_staff_kind_group.checkedRadioButtonId) {
                R.id.review_staff_very_unkind_radio -> review.kind = 1
                R.id.review_staff_unkind_radio -> review.kind = 2
                R.id.review_staff_soso_radio -> review.kind = 3
                R.id.review_staff_kind_card -> review.kind = 4
                R.id.review_staff_very_kind_radio -> review.kind = 5
            }
        }
        review_atmo_group.setOnCheckedChangeListener { group, checkedId ->
            when(review_atmo_group.checkedRadioButtonId) {
                R.id.review_atmo_very_quiet_radio -> review.atmo = 1
                R.id.review_atmo_quiet_radio -> review.atmo = 2
                R.id.review_atmo_soso_radio -> review.atmo = 3
                R.id.review_atmo_little_noisy_radio -> review.atmo = 4
                R.id.review_atmo_noisy_radio -> review.atmo = 5
            }
        }
        review_studied_well_group.setOnCheckedChangeListener { group, checkedId ->
            when(review_studied_well_group.checkedRadioButtonId) {
                R.id.review_study_very_bad_radio -> review.studied_well = 1
                R.id.review_study_bad_radio -> review.studied_well = 2
                R.id.review_study_soso_radio -> review.studied_well = 3
                R.id.review_study_well_radio -> review.studied_well = 4
                R.id.review_study_very_well_radio -> review.studied_well = 5
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
            if(review.cafe_clean == 0)
                Toast.makeText(this, "매장은 깨끗했나요?", Toast.LENGTH_SHORT).show()
            else if(review.rest_clean == 0)
                Toast.makeText(this, "화장실은 깨끗했나요?", Toast.LENGTH_SHORT).show()
            else if(review.atmo == 0)
                Toast.makeText(this, "매장 분위기는 괜찮았나요?", Toast.LENGTH_SHORT).show()
            else if(review.kind == 0)
                Toast.makeText(this, "직원은 친절했나요?", Toast.LENGTH_SHORT).show()
            else if(review.studied_well == 0)
                Toast.makeText(this, "공부는 잘 됐나요?", Toast.LENGTH_SHORT).show()
            else{
                /* 서버에 쿠폰 발급 요청 */
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

