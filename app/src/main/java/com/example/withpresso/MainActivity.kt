package com.example.withpresso

import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.ObjectKey
import com.example.withpresso.adapter.CafeRecyclerViewAdapter
import com.example.withpresso.adapter.Data
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var pref: SharedPreferences

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /* init */
        pref = getSharedPreferences("user_info", 0)

        val dataList = arrayListOf(
            Data(R.drawable.melancholic_org, "melancholic-1"),
            Data(R.drawable.melancholic_head, "melancholic-2"),
            Data(R.drawable.melancholic_head, "melancholic-3"),
            Data(R.drawable.melancholic_org, "melancholic-4"),
            Data(R.drawable.melancholic_org, "melancholic-5"),
            Data(R.drawable.melancholic_head, "melancholic-6"),
            Data(R.drawable.melancholic_head, "melancholic-7"),
            Data(R.drawable.melancholic_org, "melancholic-8")
        )

        cafes_recycle.layoutManager = GridLayoutManager(this, 2)
        cafes_recycle.adapter = CafeRecyclerViewAdapter(this, dataList)

        /* setOnClickListener */
        my_page_button.setOnClickListener {
            if(pref.getString("email", "") == "" || pref.getString("password", "") == "") {
                val intent = Intent(this, LogInActivity::class.java)
                startActivity(intent)
            }
            else {
                val intent = Intent(this, MyPageActivity::class.java)
                startActivity(intent)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()

        /* Glide로 image view에 이미지 그리기 */
        val uri = pref.getString("profile_uri", "")
        val sig = pref.getString("profile_sig", "0")
        /* 나중에 image loading 실패하면 서버에 요청에서 데이터 받아오기 */
        if(sig == "0") {
            val defaultUri = Uri.parse(
                "android.resource://${R::class.java.`package`}/${this.resources.getIdentifier(
                    "default_profile",
                    "drawable",
                    this.packageName
                )}"
            )
            Glide.with(this)
                .asBitmap()
                .load(defaultUri)
                .signature(ObjectKey(sig))
                .into(my_page_button)
        }
        else {
            /* try-catch로 caching 실패했을 때 서버에서 이미지 불러오는 거 처리하기 */
            Glide.with(this)
                .asBitmap()
                .load(uri)
                .signature(ObjectKey(sig!!))
                .centerCrop()
                .into(my_page_button)
        }
    }

    /* string -> byte array -> bitmap으로 변환 */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun stringToBitmap(string: String): Bitmap? {
        try {
            val byteArray = Base64.getDecoder().decode(string)
            val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)

            return bitmap
        }
        catch (e: java.lang.Exception) {
            e.printStackTrace()
            return null
        }
    }
}