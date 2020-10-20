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
                        "1인석/2인석/4인석/다인석: " + "5/5/5/1" + "\n" +
                        "넓이(2인석 기준): " + "A4 4장",
                "의자\n" +
                        "쿠션감: " + "딱딱해요"+"\n" +
                        "등받이: " + "있어요",
                "음악\n" +
                        "장르: " + "재즈",
                "화장실\n" +
                        "위치: " + "매장 내부" + "\n" +
                        "성별 분리: " + "분리 되어 있어요",
                "방역여부\n" +
                        "최근 방역 날짜: " + "YYY/MM/DD",
                "방문객 평가" +
                        "매장 청결: " + "4.7" + "점" + "\n" +
                        "화장실 청결: " + "4.3" + "점" + "\n" +
                        "점원 친절도: " + "4.3" + "점" + "\n" +
                        "주변 소리(1점 = 조용 ~ 5점 = 시끄러움)" + "2" + "점" + "\n" +
                        "공부 잘 됨 지수: " + "8" + "점"
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