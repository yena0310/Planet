package com.example.planet.utils

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

/**
 * 랭킹 관련 유틸리티 함수들
 */
object RankingUtils {

    /**
     * 학교 내 순위 계산 (점수 기준)
     * @param db FirebaseFirestore 인스턴스
     * @param schoolName 학교명
     * @param userScore 사용자 점수
     * @param onResult 결과 콜백 (순위)
     */
    fun calculateSchoolRanking(
        db: FirebaseFirestore,
        schoolName: String,
        userScore: Int,
        onResult: (Int) -> Unit
    ) {
        Log.d("RankingUtils", "학교 랭킹 계산 시작 - 학교: $schoolName, 점수: $userScore")

        db.collection("users")
            .whereEqualTo("schoolName", schoolName)
            .whereGreaterThan("score", userScore)
            .get()
            .addOnSuccessListener { documents ->
                val higherScoreCount = documents.size()
                val ranking = higherScoreCount + 1
                Log.d("RankingUtils", "학교 랭킹 계산 완료 - 더 높은 점수 사용자: $higherScoreCount 명, 내 순위: $ranking")
                onResult(ranking)
            }
            .addOnFailureListener { e ->
                Log.e("RankingUtils", "학교 랭킹 계산 실패", e)
                onResult(0) // 실패 시 0 반환
            }
    }

    /**
     * 학급 내 전체 랭킹 조회 (점수 순 정렬)
     * @param db FirebaseFirestore 인스턴스
     * @param currentUserId 현재 사용자 ID
     * @param onResult 결과 콜백 (랭킹 리스트, 내 등수, 내 점수, 퍼센타일)
     */
    fun loadClassRankings(
        db: FirebaseFirestore,
        currentUserId: String,
        onResult: (List<StudentRanking>, Int, Int, Int) -> Unit
    ) {
        Log.d("RankingUtils", "학급 랭킹 로드 시작 - 사용자ID: $currentUserId")

        // 1. 현재 사용자 정보 가져오기
        db.collection("users").document(currentUserId).get()
            .addOnSuccessListener { userDoc ->
                if (userDoc.exists()) {
                    val userClassId = userDoc.getDocumentReference("classId")
                    val userScore = userDoc.getLong("score")?.toInt() ?: 0
                    val userName = userDoc.getString("name") ?: "나"

                    Log.d("RankingUtils", "사용자 점수: $userScore, 학급ID: ${userClassId?.path}")

                    if (userClassId != null) {
                        // 2. 같은 학급의 모든 사용자 가져오기 (점수 순 정렬)
                        db.collection("users")
                            .whereEqualTo("classId", userClassId)
                            .orderBy("score", Query.Direction.DESCENDING)
                            .get()
                            .addOnSuccessListener { documents ->
                                Log.d("RankingUtils", "학급 사용자 수: ${documents.size()}")

                                val rankings = mutableListOf<StudentRanking>()
                                var currentUserRank = 1
                                var currentUserScore = userScore

                                // 학급에 혼자만 있는 경우 처리
                                if (documents.size() <= 1) {
                                    rankings.add(
                                        StudentRanking(
                                            userId = currentUserId,
                                            name = userName,
                                            score = userScore,
                                            rank = 1,
                                            isCurrentUser = true
                                        )
                                    )

                                    onResult(rankings, 1, userScore, 100)
                                    return@addOnSuccessListener
                                }

                                documents.forEachIndexed { index, document ->
                                    val userId = document.getString("userId") ?: ""
                                    val name = document.getString("name") ?: "이름 없음"
                                    val score = document.getLong("score")?.toInt() ?: 0
                                    val rank = index + 1
                                    val isCurrentUser = userId == currentUserId

                                    if (isCurrentUser) {
                                        currentUserRank = rank
                                        currentUserScore = score
                                    }

                                    rankings.add(
                                        StudentRanking(
                                            userId = userId,
                                            name = name,
                                            score = score,
                                            rank = rank,
                                            isCurrentUser = isCurrentUser
                                        )
                                    )
                                }

                                // 3. 퍼센타일 계산
                                val totalStudents = documents.size()
                                val percentileAhead = if (totalStudents > 1) {
                                    ((totalStudents - currentUserRank).toFloat() / (totalStudents - 1) * 100).toInt()
                                } else {
                                    100
                                }

                                Log.d("RankingUtils", "퍼센타일 계산 - 전체: $totalStudents, 내 등수: $currentUserRank, 퍼센타일: $percentileAhead%")

                                onResult(rankings, currentUserRank, currentUserScore, percentileAhead)
                            }
                            .addOnFailureListener { e ->
                                Log.e("RankingUtils", "학급 랭킹 로드 실패", e)
                                onResult(emptyList(), 0, 0, 0)
                            }
                    } else {
                        Log.w("RankingUtils", "사용자의 학급 정보가 없음")
                        onResult(emptyList(), 0, 0, 0)
                    }
                } else {
                    Log.w("RankingUtils", "사용자 문서가 존재하지 않음")
                    onResult(emptyList(), 0, 0, 0)
                }
            }
            .addOnFailureListener { e ->
                Log.e("RankingUtils", "사용자 정보 로드 실패", e)
                onResult(emptyList(), 0, 0, 0)
            }
    }

    /**
     * 전교 랭킹 계산 (점수 기준)
     * @param db FirebaseFirestore 인스턴스
     * @param userScore 사용자 점수
     * @param onResult 결과 콜백 (순위)
     */
    fun calculateGlobalRanking(
        db: FirebaseFirestore,
        userScore: Int,
        onResult: (Int) -> Unit
    ) {
        Log.d("RankingUtils", "전교 랭킹 계산 시작 - 점수: $userScore")

        db.collection("users")
            .whereGreaterThan("score", userScore)
            .get()
            .addOnSuccessListener { documents ->
                val higherScoreCount = documents.size()
                val ranking = higherScoreCount + 1
                Log.d("RankingUtils", "전교 랭킹 계산 완료 - 더 높은 점수 사용자: $higherScoreCount 명, 내 순위: $ranking")
                onResult(ranking)
            }
            .addOnFailureListener { e ->
                Log.e("RankingUtils", "전교 랭킹 계산 실패", e)
                onResult(0)
            }
    }

    /**
     * 사용자 점수 업데이트 및 랭킹 재계산
     * @param db FirebaseFirestore 인스턴스
     * @param userId 사용자 ID
     * @param newScore 새로운 점수
     * @param onSuccess 성공 콜백
     * @param onFailure 실패 콜백
     */
    fun updateUserScoreAndRanking(
        db: FirebaseFirestore,
        userId: String,
        newScore: Int,
        onSuccess: () -> Unit = {},
        onFailure: (String) -> Unit = {}
    ) {
        Log.d("RankingUtils", "사용자 점수 업데이트 - ID: $userId, 새 점수: $newScore")

        // 1. 점수 업데이트
        db.collection("users").document(userId)
            .update("score", newScore)
            .addOnSuccessListener {
                Log.d("RankingUtils", "점수 업데이트 성공")

                // 2. 사용자 정보 다시 가져와서 랭킹 계산
                db.collection("users").document(userId).get()
                    .addOnSuccessListener { userDoc ->
                        if (userDoc.exists()) {
                            val classId = userDoc.getDocumentReference("classId")

                            if (classId != null) {
                                // 3. 같은 학급 내에서 랭킹 계산
                                db.collection("users")
                                    .whereEqualTo("classId", classId)
                                    .whereGreaterThan("score", newScore)
                                    .get()
                                    .addOnSuccessListener { documents ->
                                        val newRanking = documents.size() + 1

                                        // 4. 랭킹 업데이트
                                        db.collection("users").document(userId)
                                            .update("ranking", newRanking)
                                            .addOnSuccessListener {
                                                Log.d("RankingUtils", "랭킹 업데이트 완료 - 새 랭킹: $newRanking")
                                                onSuccess()
                                            }
                                            .addOnFailureListener { e ->
                                                Log.e("RankingUtils", "랭킹 업데이트 실패", e)
                                                onFailure("랭킹 업데이트 실패: ${e.message}")
                                            }
                                    }
                                    .addOnFailureListener { e ->
                                        Log.e("RankingUtils", "랭킹 계산 실패", e)
                                        onFailure("랭킹 계산 실패: ${e.message}")
                                    }
                            } else {
                                onFailure("학급 정보가 없습니다")
                            }
                        } else {
                            onFailure("사용자 정보가 없습니다")
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e("RankingUtils", "사용자 정보 로드 실패", e)
                        onFailure("사용자 정보 로드 실패: ${e.message}")
                    }
            }
            .addOnFailureListener { e ->
                Log.e("RankingUtils", "점수 업데이트 실패", e)
                onFailure("점수 업데이트 실패: ${e.message}")
            }
    }
}

/**
 * 학생 랭킹 데이터 클래스
 */
data class StudentRanking(
    val userId: String,
    val name: String,
    val score: Int,
    val rank: Int,
    val isCurrentUser: Boolean = false
)