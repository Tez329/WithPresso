package com.example.withpresso.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.withpresso.InfoActivity
import com.example.withpresso.R
import com.example.withpresso.service.Cafe
import com.example.withpresso.service.CafeInfo
import com.example.withpresso.service.CafeInfoService
import kotlinx.android.synthetic.main.cafe_icon.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class CafeViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    val cafe_uniq_num = itemView.cafe_uniq_num_text
    val photo = itemView.cafe_image
    var cafe_name = itemView.cafe_name_text
}

class CafeRecyclerViewAdapter(
    val context: Context,
    val dataList: ArrayList<Cafe>): RecyclerView.Adapter<CafeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CafeViewHolder {
        val cellForRow = LayoutInflater.from(context).inflate(R.layout.cafe_icon, parent, false)
        return CafeViewHolder(cellForRow)
    }

    override fun getItemCount() = dataList.size

    override fun onBindViewHolder(holder: CafeViewHolder, position: Int) {
        val currentCafe = dataList[position]
        holder.cafe_uniq_num.text = currentCafe.cafe_uniq_num.toString()
        drawCafePhoto(currentCafe.photoUrl, holder.photo)
        holder.cafe_name.text = currentCafe.name

        holder.itemView.setOnClickListener {
            /* 서버에 카페 정보 요청 */
            val retrofit = Retrofit.Builder()
                .baseUrl("http://ec2-3-34-119-217.ap-northeast-2.compute.amazonaws.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val cafeInfoService = retrofit.create(CafeInfoService::class.java)

            cafeInfoService.requestCafeInfo(currentCafe.cafe_uniq_num).enqueue(object :Callback<CafeInfo> {
                /* 통신 성공 시 실행 */
                override fun onResponse(call: Call<CafeInfo>, response: Response<CafeInfo>) {
                    val cafeInfo = response.body()

                    if(cafeInfo == null) {
                        AlertDialog.Builder(context)
                            .setTitle("카페 정보 불러오기 실패")
                            .setMessage("카페 정보를 불러오지 못했습니다.")
                            .show()
                    }
                    else {
                        Toast.makeText(
                            context,
                            "${currentCafe.name} 의 정보를 서버로 부터 받아옴.",
                            Toast.LENGTH_SHORT
                        ).show()

                        val intent = Intent(context, InfoActivity::class.java)
                        intent.putExtra("cafe_info", cafeInfo)
                        context.startActivity(intent)
                    }
                }
                /* 통신 실패 시 실행 */
                override fun onFailure(call: Call<CafeInfo>, t: Throwable) {
                    Log.d("Error", t.message.toString())
                    AlertDialog.Builder(context)
                        .setTitle("카페 정보 불러오기 실패")
                        .setMessage("통신 오류")
                        .show()
                }
            })
        }
    }


    private fun drawCafePhoto(photoUrl: String?, imageView: ImageView) {
        if(photoUrl.isNullOrBlank()) {
            Glide.with(context)
                .load(R.drawable.coffee)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(imageView)
        }
        else{
            Glide.with(context)
                .load(photoUrl)
                .centerCrop()
                .error(R.drawable.coffee)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(imageView)
        }
    }
}