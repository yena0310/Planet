package com.example.planet.guide

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface RecyclingApiService {
    @Multipart
    @POST("/process")
    fun uploadImage(
        @Part file: MultipartBody.Part
    ): Call<GuideResponse>
}
