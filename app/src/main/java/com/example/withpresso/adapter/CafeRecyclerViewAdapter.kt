package com.example.withpresso.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.withpresso.InfoActivity
import com.example.withpresso.R
import kotlinx.android.synthetic.main.cafe_icon.view.*

data class Data(val profile: Int, var name: String)

class CafeViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    val profile = itemView.melan_image
    var cafe_name = itemView.melan_text
}

class CafeRecyclerViewAdapter(
    val context: Context,
    val dataList: ArrayList<Data>): RecyclerView.Adapter<CafeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CafeViewHolder {
        val cellForRow = LayoutInflater.from(context).inflate(R.layout.cafe_icon, parent, false)
        return CafeViewHolder(cellForRow)
    }

    override fun getItemCount() = dataList.size

    override fun onBindViewHolder(holder: CafeViewHolder, position: Int) {
        val curData = dataList[position]
        holder.profile.setImageResource(curData.profile)
        holder.cafe_name.text = curData.name

        holder.itemView.setOnClickListener {
            Toast.makeText(context, curData.name + "의 정보를 서버로 부터 받아옴.", Toast.LENGTH_SHORT).show()
            val intent = Intent(context, InfoActivity::class.java)
            intent.putExtra("cafe_name", curData.name)
            context.startActivity(intent)
        }
    }

}