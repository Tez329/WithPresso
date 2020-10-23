package com.example.withpresso.service

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

// data class Login (var code: String)
/* 로그인에 성공하면 사진 url 받기 */
interface LoginService {
    @FormUrlEncoded
    @POST("/login/")
    fun requestLogin(
        @Field("user_id")id: String,
        @Field("user_pw")pw: String
    ): Call<String>
}
