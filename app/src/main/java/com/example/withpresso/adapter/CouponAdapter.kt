package com.example.withpresso.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.example.withpresso.R
import kotlinx.android.synthetic.main.coupon_icon.view.*


data class Coupon (val profile: Int, val value: String)

class CouponAdapter(
    val context: Context,
    val couponList: ArrayList<Coupon>): BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = LayoutInflater.from(context).inflate(R.layout.coupon_icon, null)

        view.coupon_cafe_image.setImageResource(couponList[position].profile)
        view.coupon_discount_text.text = couponList[position].value

        return view
    }

    override fun getItem(position: Int) = couponList[position]

    override fun getItemId(position: Int) = 0L

    override fun getCount() = couponList.size

}