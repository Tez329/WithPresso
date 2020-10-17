package com.example.withpresso

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.withpresso.adapter.ExpandableListAdapter
import kotlinx.android.synthetic.main.activity_info.*

class InfoActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        if(intent.hasExtra("cafe_name"))
            cafe_name_text.text = intent.getStringExtra("cafe_name")

        val parentList = arrayListOf("카페 기본 정보", "카페 분위기 정보")
        val childrenList = arrayListOf(
            arrayListOf(
                "운영시간: " + "10:00 - 20:00",
                "매장 위치: " + "중앙대학교 308관",
                "매장 전화번호: " + "02-820-0000",
                "메뉴: " + "아메리카노 4,000원"
            ),
            arrayListOf(
                "책상\n" +
                        "2인석/4인석/다인석: " + "5/5/1" + "\n" +
                        "넓이: " + "A4 4장",
                "의자\n" +
                        "개수: " + "자리 당 하나" +"\n" +
                        "형태: " + "등받이가 있는 딱딱한 의자",
                "음악\n" +
                        "장르: " + "가사가 없는 재즈" + "\n" +
                        "볼륨: " + "시끄러워요: 70%, 적당해요: 30%",
                "화장실\n" +
                        "위치: " + "매장 내부" + "\n" +
                        "청결: " + "깨끗해요" + "\n" +
                        "성별 분리: " + "따로 있어요"
            )
        )

        val expandableListAdapter =
            ExpandableListAdapter(
                this,
                parentList,
                childrenList
            )
        cafe_basic_info.setAdapter(expandableListAdapter)


        /* setOnClickListener */
        info_back_button.setOnClickListener { onBackPressed() }

        info_review_button.setOnClickListener {
            val intent = Intent(this, ReviewActivity::class.java)
            startActivity(intent)
        }
    }
}