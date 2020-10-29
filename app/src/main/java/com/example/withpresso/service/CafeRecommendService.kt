package com.example.withpresso.service

import com.example.withpresso.adapter.Cafe
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.ArrayList

interface CafeRecommendService {
    @GET("/cafe_recommend/")
    fun requestCafeRecommendList(
        @Query("lat") latitude: Double,
        @Query("long") longitude: Double,
        @Query("uniq_num") uniq_num: Int
    ): Call<ArrayList<Cafe>>
}