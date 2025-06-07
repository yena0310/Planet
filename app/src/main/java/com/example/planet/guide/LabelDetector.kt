package com.example.planet.guide

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream

class LabelDetector(private val context: Context) {

    fun process(
        bitmap: Bitmap,
        onResult: (Bitmap, String) -> Unit,
        onError: (Bitmap, String) -> Unit
    ) {
        // STEP 1: 리사이징 (최대 1024px 정도로 제한, 비율 유지)
        val resizedBitmap = resizeBitmap(bitmap, 1024)

        // STEP 2: 업로드를 위한 ByteArray 변환
        val stream = ByteArrayOutputStream()
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream)
        val requestBody = stream.toByteArray()
            .toRequestBody("image/jpeg".toMediaTypeOrNull())

        val multipartBody = MultipartBody.Part.createFormData(
            "file",
            "image.jpg",
            requestBody
        )

        val call = RetrofitClient.apiService.uploadImage(multipartBody)
        Log.d("DEBUG", "🚀 Retrofit 요청 보냄")

        call.enqueue(object : Callback<GuideResponse> {
            override fun onResponse(
                call: Call<GuideResponse>,
                response: Response<GuideResponse>
            ) {
                Log.d("DEBUG", "✅ 서버 응답 수신: ${response.code()}")
                if (response.isSuccessful && response.body() != null) {
                    Log.d("DEBUG", "🌀성공적으로 수신 완료!")
                    onResult(resizedBitmap, response.body()!!.guide)
                    Log.d("DEBUG", "🍇가이드 : ${response.body()!!.guide}")
                } else {
                    onError(resizedBitmap, "서버 응답 오류: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<GuideResponse>, t: Throwable) {
                Log.e("DEBUG", "❌ 서버 요청 실패: ${t.message}")
                onError(resizedBitmap, "네트워크 오류: ${t.message}")
            }
        })
    }
}

private fun resizeBitmap(bitmap: Bitmap, maxSize: Int): Bitmap {
    val width = bitmap.width
    val height = bitmap.height

    if (width <= maxSize && height <= maxSize) return bitmap

    val aspectRatio: Float = width.toFloat() / height.toFloat()
    val (newWidth, newHeight) = if (aspectRatio > 1) {
        maxSize to (maxSize / aspectRatio).toInt()
    } else {
        (maxSize * aspectRatio).toInt() to maxSize
    }

    return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
}
