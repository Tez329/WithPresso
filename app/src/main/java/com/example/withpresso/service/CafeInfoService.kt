package com.example.withpresso.service

import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import java.io.Serializable

data class CafeInfo (
    /* basic */
    @SerializedName("cafe_asin") val cafe_asin: Int,
    @SerializedName("cafe_name") val cafe_name: String,
    @SerializedName("addr_city") val addr_city: String,
    @SerializedName("addr_district") val addr_district: String,
    @SerializedName("addr_street") val addr_street: String,
    @SerializedName("addr_detail") val addr_detail: String,
    @SerializedName("cafe_hour") val cafe_hour: String,
    @SerializedName("cafe_tel") val cafe_tel:String,
    /* table*/
    @SerializedName("table_info") val table_struct: String,
    @SerializedName("table_size_info") val table_size: String,
    /* chair */
    @SerializedName("chair_back_info") val chair_back: Int,
    @SerializedName("chair_cushion_info") val chair_cushion: String,
    /* music*/
//    @SerializedName("cafe_tel") val music: Boolean,
    @SerializedName("bgm_info") val music_genre: String,
    /* restroom */
    @SerializedName("toilet_info") val rest_in: Int,
    @SerializedName("toilet_gender_info") val rest_gen_sep: Int,
    /*anti-corona */
    @SerializedName("sterilization_info") val anco_data: String,
    /* user review */
    @SerializedName("user_clean_info") val cafe_clean: Float,
    @SerializedName("user_toilet_clean_info") val rest_clean: Float,
    @SerializedName("user_kindness_info") val kind: Float,
    @SerializedName("user_noisy_info") val noise: Float,
    @SerializedName("user_good_study_info") val study_well: Float
): Serializable

interface CafeInfoService {
    @GET("/cafe_info/")
    fun requestCafeInfo(
        @Query("cafe_num") cafe_num: Int
    ): Call<CafeInfo>
}