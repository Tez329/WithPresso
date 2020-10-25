package com.example.withpresso

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.withpresso.adapter.CafeRecyclerViewAdapter
import com.example.withpresso.adapter.ExpandableListAdapter
import com.example.withpresso.service.CafeInfo
import kotlinx.android.synthetic.main.activity_info.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class InfoActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        /* setOnClickListener */
        info_back_button.setOnClickListener { onBackPressed() }

        info_review_button.setOnClickListener {
            val intent = Intent(this, ReviewActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()

//        val intent = Intent(this, CafeRecyclerViewAdapter::class.java)
        val cafeInfo = intent.getSerializableExtra("cafe_info") as CafeInfo

        info_cafe_name_text.text = cafeInfo.cafe_name

        val parentList = arrayListOf("카페 기본 정보", "카페 분위기 정보")
        val childrenList = arrayListOf(
            arrayListOf(
                "운영시간: ${cafeInfo.cafe_hour}",
                "매장 위치: ${cafeInfo.addr_city} ${cafeInfo.addr_district} ${cafeInfo.addr_street} ${cafeInfo.addr_detail}",
                "매장 전화번호: ${cafeInfo.cafe_tel}",
                "메뉴: " + "아직 없음."
            ),
            arrayListOf(
                "책상\n" /*+
                        "1인석/2인석/4인석/다인석: ${cafeInfo.table_struct}\n" +
                        "넓이(2인석 기준): " + "A4 ${cafeInfo.table_size}장"*/,
                "의자\n" /*+
                        "쿠션감: ${cafeInfo.chair_cushion}\n" +
                        "등받이: ${textConverter("chair_back", cafeInfo.chair_back)}"*/,
                "음악\n" /*+
                        "장르: ${cafeInfo.music_genre}"*/,
                "화장실\n" /*+
                        "위치: ${textConverter("rest_in", cafeInfo.rest_in)}\n" +
                        "성별 분리: ${textConverter("rest_gen_sep", cafeInfo.rest_gen_sep)}"*/,
                "방역여부\n" /*+
                        "최근 방역 날짜: ${cafeInfo.anco_data}"*/,
                "방문객 평가" /*+
                        "매장 청결: ${cafeInfo.cafe_clean}점\n" +
                        "화장실 청결: ${cafeInfo.rest_clean}점\n" +
                        "점원 친절도: ${cafeInfo.kind}점\n" +
                        "주변 소리(1점 = 조용 ~ 5점 = 시끄러움): ${cafeInfo.noise}점\n" +
                        "공부 잘 됨 지수: ${cafeInfo.study_well}점"*/
            )
        )

        val expandableListAdapter =
            ExpandableListAdapter(this, parentList, childrenList)
        cafe_basic_info.setAdapter(expandableListAdapter)
    }

    private fun textConverter(category: String, value: Boolean): String? {
        return when(category) {
            "chair_back" -> {
                if (value) "있어요."
                else "없어요."
            }
            "rest_in" -> {
                if(value) "매장 안에 있어요."
                else "매장 밖에 있어요."
            }
            "rest_gen_sep" -> {
                if(value) "성별이 구분되어 있어요."
                else "성별이 구분되어 있지 않아요."
            }
            else -> null
        }
    }
}