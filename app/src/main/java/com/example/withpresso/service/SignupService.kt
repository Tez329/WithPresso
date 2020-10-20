package com.example.withpresso.service

import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

data class SignUp(val code: String)

public interface SignUpService{
    @Multipart
    @POST("/signup/")
    fun requestSignUp(
        @PartMap user_info: HashMap<String, RequestBody>
    ): Call<SignUp>
}