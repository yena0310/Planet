package com.example.planet.data

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

object UserQuizRepository {
    private val db = FirebaseFirestore.getInstance()

    // ✅ 문제 인덱스를 업데이트하는 함수 (성공/실패 콜백 포함)
    fun updateLastQuestionIndex(
        userId: String,
        questionIndex: Int,
        onSuccess: () -> Unit = {},
        onFailure: (String) -> Unit = {}
    ) {
        Log.d("UpdateQuestion", "사용자 ${userId}의 lastQuestionIndex를 ${questionIndex}로 업데이트")

        db.collection("users").document(userId)
            .update("lastQuestionIndex", questionIndex)
            .addOnSuccessListener {
                Log.d("UpdateQuestion", "✅ lastQuestionIndex 업데이트 성공: $questionIndex")
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.e("UpdateQuestion", "❌ lastQuestionIndex 업데이트 실패", e)
                onFailure("문제 인덱스 업데이트 실패: ${e.message}")
            }
    }

    // ✅ 저장된 인덱스를 불러오는 함수
    fun fetchLastQuestionIndex(
        userId: String,
        onComplete: (Int?) -> Unit
    ) {
        db.collection("users").document(userId)
            .get()
            .addOnSuccessListener { doc ->
                val index = doc.getLong("lastQuestionIndex")?.toInt()
                onComplete(index)
            }
            .addOnFailureListener {
                onComplete(null)
            }
    }
}
