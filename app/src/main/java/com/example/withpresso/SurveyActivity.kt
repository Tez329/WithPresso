package com.example.withpresso

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.Toast
import com.example.withpresso.R
import kotlinx.android.synthetic.main.activity_my_page.*
import kotlinx.android.synthetic.main.activity_review.*
import kotlinx.android.synthetic.main.activity_survey.*

class SurveyActivity : AppCompatActivity() {
    private lateinit var pref: SharedPreferences
    private var checked = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_survey)

        /* init */
        pref = getSharedPreferences("user_info", 0)

        /* setOnClickListener */
        survey_back_button.setOnClickListener { onBackPressed() }

        survey_table_check.setOnCheckedChangeListener { buttonView, isChecked ->
            manageCheckBox(buttonView,isChecked)
        }
        survey_atmo_check.setOnCheckedChangeListener { buttonView, isChecked ->
            manageCheckBox(buttonView,isChecked)
        }
        survey_light_check.setOnCheckedChangeListener { buttonView, isChecked ->
            manageCheckBox(buttonView,isChecked)
        }
        survey_plug_check.setOnCheckedChangeListener { buttonView, isChecked ->
            manageCheckBox(buttonView,isChecked)
        }
        survey_restroom_check.setOnCheckedChangeListener { buttonView, isChecked ->
            manageCheckBox(buttonView,isChecked)
        }

        survey_submit_button.setOnClickListener {
            if(checked < 3)
                Toast.makeText(this, "답변의 개수가 모자랍니다.", Toast.LENGTH_SHORT).show()
            else {
                var survey = arrayOf(0, 0, 0)
                var idx = 0

                if(survey_table_check.isChecked)
                    survey[idx++] = 1
                if(survey_atmo_check.isChecked)
                    survey[idx++] = 2
                if(survey_light_check.isChecked)
                    survey[idx++] = 3
                if(survey_plug_check.isChecked)
                    survey[idx++] = 4
                if(survey_restroom_check.isChecked)
                    survey[idx++] = 5

                val edit = pref.edit()
                edit.putInt("survey1", survey[0])
                edit.putInt("survey2", survey[1])
                edit.putInt("survey3", survey[2])
                edit.commit()

                /* 바로 서버로 넘겨서 추천 목록 정렬해서 보여주기 */
                Toast.makeText(this, "조사 결과가 정상적으로 반영되었습니다.", Toast.LENGTH_SHORT).show()

                onBackPressed()
            }

        }
    }

    private fun manageCheckBox(buttonView: View, isChecked: Boolean) {
        buttonView as CheckBox

        if(isChecked) {
            if(checked >= 3) {
                Toast.makeText(this, "3개를 다 골랐습니다.", Toast.LENGTH_SHORT).show()
                buttonView.isChecked = false
            }
            else
                checked++
        }
        else
            checked--
    }
}