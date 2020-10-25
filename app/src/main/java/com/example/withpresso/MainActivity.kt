package com.example.withpresso

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.GridLayout.HORIZONTAL
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.signature.ObjectKey
import com.example.withpresso.adapter.Cafe
import com.example.withpresso.adapter.CafeRecyclerViewAdapter
import com.example.withpresso.service.Login
import com.example.withpresso.service.LoginService
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var pref: SharedPreferences
    private lateinit var retrofit: Retrofit
    private lateinit var BASE_URL: String

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /* init */
        pref = getSharedPreferences("user_info", 0)
        BASE_URL = "http://ec2-3-34-119-217.ap-northeast-2.compute.amazonaws.com"
        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()


        /*
        * onResume()으로 옮기기
        * */
        val dataList = arrayListOf(
            Cafe(R.drawable.melancholic_org, "melancholic-1"),
            Cafe(R.drawable.melancholic_head, "melancholic-2"),
            Cafe(R.drawable.melancholic_head, "melancholic-3"),
            Cafe(R.drawable.melancholic_org, "melancholic-4"),
            Cafe(R.drawable.melancholic_org, "melancholic-5"),
            Cafe(R.drawable.melancholic_head, "melancholic-6"),
            Cafe(R.drawable.melancholic_head, "melancholic-7"),
            Cafe(R.drawable.melancholic_org, "melancholic-8")
        )

        cafes_recycle.layoutManager = GridLayoutManager(this, 2)
        cafes_recycle.adapter = CafeRecyclerViewAdapter(this, dataList)


        /*
        * 자동 로그인
        * 이전에 사용하던 계정 정보가 있다면 자동 로그인 시도
        *  */
        if(pref.contains("email") && pref.contains("password")) {
            val email = pref.getString("email", "")
            val password = pref.getString("password", "")
            val loginService = retrofit.create(LoginService::class.java)

            loginService.requestLogin(email!!, password!!).enqueue(object : Callback<Login>{
                /* 통신 성공 시 실행 */
                override fun onResponse(call: Call<Login>, response: Response<Login>) {
                    val userInfo = response.body()

                    if(userInfo!!.uniq_num != 0) {
                        val edit = pref.edit()
                        edit.putInt("uniq_num", userInfo.uniq_num)
                        edit.putString("email", email)
                        edit.putString("password", password)
                        edit.putString("nickname", userInfo.nickname)
                        edit.putString("profile", userInfo.profile)
                        edit.commit()

                        Toast.makeText(this@MainActivity, "자동 로그인 됨", Toast.LENGTH_SHORT).show()
                    }
                }
                /* 통신 실패 시 실행 */
                override fun onFailure(call: Call<Login>, t: Throwable) {
                    Log.d("login", t.message!!)
                    val dialog = AlertDialog.Builder(this@MainActivity)
                    dialog.setTitle("자동 로그인 실패")
                    dialog.setMessage("통신 오류")
                    dialog.show()
                }
            })
        }

        /* setOnClickListener */
        my_page_button.setOnClickListener {
            if(pref.contains("email") && pref.contains("password")) {
                val intent = Intent(this, MyPageActivity::class.java)
                startActivity(intent)
            }
            else {
                val intent = Intent(this, LogInActivity::class.java)
                startActivity(intent)
            }
        }


    }

    override fun onResume() {
        super.onResume()

        /*
        * pref에 url이 있으면 glide를 이용해서 그리기
        *               없으면 기본 이미지 그리기
        * */
        if(pref.getString("profile", "")!!.isNotBlank()) {
            val userUniqNum = pref.getInt("uniq_num", 0)
            val profileName = pref.getString("profile", "")
            val profileUrl = "${BASE_URL}/profile/${userUniqNum}/${profileName}"

            Glide.with(this)
                .load(profileUrl!!)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .into(my_page_button)
        }
        else {
            val defaultUri = Uri.parse(
                "android.resource://${R::class.java.`package`}/${this.resources.getIdentifier(
                    "default_profile",
                    "drawable",
                    this.packageName
                )}"
            )
            Glide.with(this)
                .load(defaultUri)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(my_page_button)
        }

//        /* Glide로 image view에 이미지 그리기 */
//        val uri = pref.getString("profile_uri", "")
//        val sig = pref.getString("profile_sig", "0")
//        /* 나중에 image loading 실패하면 서버에 요청에서 데이터 받아오기 */
//        if(sig == "0") {
//            val defaultUri = Uri.parse(
//                "android.resource://${R::class.java.`package`}/${this.resources.getIdentifier(
//                    "default_profile",
//                    "drawable",
//                    this.packageName
//                )}"
//            )
//            Glide.with(this)
//                .asBitmap()
//                .load(defaultUri)
//                .signature(ObjectKey(sig))
//                .into(my_page_button)
//        }
//        else {
//            /* try-catch로 caching 실패했을 때 서버에서 이미지 불러오는 거 처리하기 */
//            Glide.with(this)
//                .asBitmap()
//                .load(uri)
//                .signature(ObjectKey(sig!!))
//                .centerCrop()
//                .into(my_page_button)
//        }
    }
}