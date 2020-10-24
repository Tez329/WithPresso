package com.example.withpresso.service

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import java.io.Serializable

data class CafeInfo (
    /* basic */
    val cafe_asin: String,
    val cafe_name: String,
    val addr_city: String,
    val addr_district: String,
    val addr_street: String,
    val addr_detail: String,
    val cafe_hour: String,
    val cafe_tel:String,
    /* table */
    val table_struct: String,
    val table_size: String,
    /* chair */
    val chair_back: Boolean,
    val chair_cushion: String,
    /* music */
    val music: Boolean,
    val music_genre: String,
    /* restroom */
    val rest_in: Boolean,
    val rest_gen_sep: Boolean,
    /* anti-corona */
    val anco_data: String,
    /* user review */
    val cafe_clean: Float,
    val rest_clean: Float,
    val kind: Float,
    val noise: Float,
    val study_well: Float
): Serializable

interface CafeInfoService {
    @GET("/cafe_info/")
    fun requestCafeInfo(
        @Query("cafe_num") cafe_num: Int
    ): Call<CafeInfo>
}