package com.example.planet.guide

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:8000" // 에뮬레이터라면 이 주소, 실제 기기에는 같은 네트워크에 있는 컴퓨터 IP주소:8000

    val apiService: RecyclingApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(RecyclingApiService::class.java)
    }
}