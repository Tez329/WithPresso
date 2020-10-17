package com.example.withpresso

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.withpresso.adapter.Coupon
import com.example.withpresso.adapter.CouponAdapter
import kotlinx.android.synthetic.main.activity_coupon.*

class CouponActivity : AppCompatActivity() {
    val couponList = arrayListOf(
        Coupon(R.drawable.melancholic_head,
            "melancholic_1\n\$1 discount coupon"),
        Coupon(R.drawable.melancholic_org,
            "melancholic_2\n\$1 discount coupon"),
        Coupon(R.drawable.melancholic_head,
            "melancholic_3\n\$1 discount coupon"),
        Coupon(R.drawable.melancholic_org,
            "melancholic_4\n\$1 discount coupon")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coupon)

        coupon_back_button.setOnClickListener { onBackPressed() }

        coupon_list.adapter = CouponAdapter(this, couponList)
    }
}