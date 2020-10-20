package com.example.withpresso.service

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface EmailDupConfirmService{
    @FormUrlEncoded
    @POST("/id_dup_check/")
    fun requestIdDupConfirm(
        @Field("input_email") input_email:String
    ):Call<String>
}