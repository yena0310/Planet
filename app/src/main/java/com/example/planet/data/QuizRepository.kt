package com.example.planet.data

import android.util.Log
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

    // 🆕 순환 방식으로 모든 퀴즈를 가져오는 함수
    fun fetchAllChapter1Quizzes(onResult: (List<QuizItem>) -> Unit) {
        val oxQuizzes = mutableListOf<QuizItem>()
        val subjectiveQuizzes = mutableListOf<QuizItem>()
        val multipleChoiceQuizzes = mutableListOf<QuizItem>()
        val matchingQuizzes = mutableListOf<QuizItem>()

        var completedFetches = 0
        val totalFetches = 4

        // 각 유형별로 퀴즈 가져오기
        fetchOXQuizzes { ox ->
            oxQuizzes.addAll(ox)
            completedFetches++
            Log.d("QuizRepository", "OX 퀴즈 로드 완료: ${ox.size}개")
            if (completedFetches == totalFetches) {
                val orderedQuizzes = createOrderedQuizList(oxQuizzes, subjectiveQuizzes, multipleChoiceQuizzes, matchingQuizzes)
                onResult(orderedQuizzes)
            }
        }

        fetchSubjectiveQuizzes { subj ->
            subjectiveQuizzes.addAll(subj)
            completedFetches++
            Log.d("QuizRepository", "주관식 퀴즈 로드 완료: ${subj.size}개")
            if (completedFetches == totalFetches) {
                val orderedQuizzes = createOrderedQuizList(oxQuizzes, subjectiveQuizzes, multipleChoiceQuizzes, matchingQuizzes)
                onResult(orderedQuizzes)
            }
        }

        fetchMultipleChoiceQuizzes { multi ->
            multipleChoiceQuizzes.addAll(multi)
            completedFetches++
            Log.d("QuizRepository", "객관식 퀴즈 로드 완료: ${multi.size}개")
            if (completedFetches == totalFetches) {
                val orderedQuizzes = createOrderedQuizList(oxQuizzes, subjectiveQuizzes, multipleChoiceQuizzes, matchingQuizzes)
                onResult(orderedQuizzes)
            }
        }

        fetchMatchingQuizzes { match ->
            matchingQuizzes.addAll(match)
            completedFetches++
            Log.d("QuizRepository", "매칭 퀴즈 로드 완료: ${match.size}개")
            if (completedFetches == totalFetches) {
                val orderedQuizzes = createOrderedQuizList(oxQuizzes, subjectiveQuizzes, multipleChoiceQuizzes, matchingQuizzes)
                onResult(orderedQuizzes)
            }
        }
    }

    // 🆕 순환 순서로 퀴즈 리스트 생성
    private fun createOrderedQuizList(
        oxQuizzes: List<QuizItem>,
        subjectiveQuizzes: List<QuizItem>,
        multipleChoiceQuizzes: List<QuizItem>,
        matchingQuizzes: List<QuizItem>
    ): List<QuizItem> {
        val orderedList = mutableListOf<QuizItem>()

        // 각 유형별로 최대 몇 개까지 있는지 확인
        val maxCount = maxOf(
            oxQuizzes.size,
            subjectiveQuizzes.size,
            multipleChoiceQuizzes.size,
            matchingQuizzes.size
        )

        // OX → 주관식 → 객관식 → 매칭 순서로 순환하면서 추가
        for (i in 0 until maxCount) {
            // OX 문제 추가
            if (i < oxQuizzes.size) {
                orderedList.add(oxQuizzes[i])
            }

            // 주관식 문제 추가
            if (i < subjectiveQuizzes.size) {
                orderedList.add(subjectiveQuizzes[i])
            }

            // 객관식 문제 추가
            if (i < multipleChoiceQuizzes.size) {
                orderedList.add(multipleChoiceQuizzes[i])
            }

            // 매칭 문제 추가
            if (i < matchingQuizzes.size) {
                orderedList.add(matchingQuizzes[i])
            }
        }

        Log.d("QuizRepository", "순환 퀴즈 리스트 생성 완료: ${orderedList.size}개")
        Log.d("QuizRepository", "순서: ${orderedList.map { "${it.type}" }}")

        return orderedList
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
        Log.e("QuizRepository", "QuizItem 변환 실패: ${this.id}", e)
        null // 변환 실패 시 무시
    }
}