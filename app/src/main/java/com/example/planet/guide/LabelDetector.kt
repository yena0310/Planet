package com.example.planet.guide

import android.content.Context
import android.graphics.Bitmap
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream

class LabelDetector(private val context: Context) {

    fun process(bitmap: Bitmap, onResult: (String) -> Unit, onError: (String) -> Unit) {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        val requestBody = stream.toByteArray()
            .toRequestBody("image/jpeg".toMediaTypeOrNull())

        val multipartBody = MultipartBody.Part.createFormData(
            "file",
            "image.jpg",
            requestBody
        )

        val call = RetrofitClient.apiService.uploadImage(multipartBody)
        call.enqueue(object : Callback<GuideResponse> {
            override fun onResponse(
                call: Call<GuideResponse>,
                response: Response<GuideResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    onResult(response.body()!!.guide)
                } else {
                    onError("서버 응답 오류: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<GuideResponse>, t: Throwable) {
                onError("네트워크 오류: ${t.message}")
            }
        })
    }
}