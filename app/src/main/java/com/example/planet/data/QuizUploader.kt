package com.example.planet.data

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import org.json.JSONArray
import org.json.JSONObject

object QuizUploader {

    fun uploadQuizzesFromAsset(context: Context, assetFileName: String, collectionName: String) {
        val db = FirebaseFirestore.getInstance()
        val json = context.assets.open(assetFileName)
            .bufferedReader().use { it.readText() }

        val jsonObject = JSONObject(json)
        val keys = jsonObject.keys()

        while (keys.hasNext()) {
            val key = keys.next()
            val quizObj = jsonObject.getJSONObject(key)

            val data = mutableMapOf<String, Any?>()

            // 공통 필드
            data["chapter"] = quizObj.getInt("chapter")
            data["type"] = quizObj.getString("type")
            data["question"] = quizObj.getString("question")
            data["answer"] = quizObj.getString("answer")
            if (quizObj.has("explanation")) {
                data["explanation"] = quizObj.getString("explanation")
            }

            // 선택 필드
            if (quizObj.has("hint")) {
                data["hint"] = quizObj.getString("hint")
            }
            if (quizObj.has("choices")) {
                val jsonArray: JSONArray = quizObj.getJSONArray("choices")
                val choices = mutableListOf<String>()
                for (i in 0 until jsonArray.length()) {
                    choices.add(jsonArray.getString(i))
                }
                data["choices"] = choices
            }

            db.collection(collectionName).document(key).set(data)
        }
    }
}

