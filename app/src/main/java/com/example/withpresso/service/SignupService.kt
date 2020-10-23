package com.example.withpresso.service

import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

// data class SignUp(val code: String)

public interface SignUpService{
    @FormUrlEncoded
    @POST("/signup/")
    fun requestSignUp(
        @Field("input_email")email: String,
        @Field("input_pw")password: String,
        @Field("input_nick")nickname: String,
        @Field("input_phone")phone: String
    ): Call<String>
}