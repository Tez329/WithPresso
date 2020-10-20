package com.example.withpresso.service

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*
import java.io.File

interface ProfileReplaceService {
    @Multipart
    @POST("/profile/")
    fun requestProfileReplacement(
        @Part file: MultipartBody.Part
    ): Call<String>
}