package com.example.planet.data

import com.example.planet.QuizItem
import com.example.planet.QuizType
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.DocumentSnapshot


object QuizRepository {

    private val db = FirebaseFirestore.getInstance()

    fun fetchOXQuizzes(onResult: (List<QuizItem>) -> Unit) {
        db.collection("ox_quizzes_ch1")
            .get()
            .addOnSuccessListener { result ->
                val quizzes = result.mapNotNull { it.toQuizItem() }
                onResult(quizzes)
            }
    }

    fun fetchSubjectiveQuizzes(onResult: (List<QuizItem>) -> Unit) {
        db.collection("subjective_quizzes_ch1")
            .get()
            .addOnSuccessListener { result ->
                val quizzes = result.mapNotNull { it.toQuizItem() }
                onResult(quizzes)
            }
    }

    fun fetchMatchingQuizzes(onResult: (List<QuizItem>) -> Unit) {
        db.collection("matching_quizzes_ch1")
            .get()
            .addOnSuccessListener { result ->
                val quizzes = result.mapNotNull { it.toQuizItem() }
                onResult(quizzes)
            }
    }

    fun fetchMultipleChoiceQuizzes(onResult: (List<QuizItem>) -> Unit) {
        db.collection("multiple_choice_quizzes_ch1")
            .get()
            .addOnSuccessListener { result ->
                val quizzes = result.mapNotNull { it.toQuizItem() }
                onResult(quizzes)
            }
    }

    // 모든 유형을 하나의 리스트로 모아주는 함수
    fun fetchAllChapter1Quizzes(onResult: (List<QuizItem>) -> Unit) {
        val all = mutableListOf<QuizItem>()
        fetchOXQuizzes { ox ->
            all += ox
            fetchSubjectiveQuizzes { subj ->
                all += subj
                fetchMatchingQuizzes { match ->
                    all += match
                    fetchMultipleChoiceQuizzes { multi ->
                        all += multi
                        onResult(all)
                    }
                }
            }
        }
    }
}

fun DocumentSnapshot.toQuizItem(): QuizItem? {
    return try {
        val data = this.data ?: return null
        QuizItem(
            id = this.id,
            chapter = (data["chapter"] as Long).toInt(),
            question = data["question"] as String,
            type = QuizType.valueOf(data["type"] as String),
            correctAnswer = data["answer"] as String,
            hint = data["hint"] as? String,
            choices = (data["choices"] as? List<*>)?.filterIsInstance<String>(),
            explanation = data["explanation"] as? String
        )
    } catch (e: Exception) {
        null // 변환 실패 시 무시
    }
}
