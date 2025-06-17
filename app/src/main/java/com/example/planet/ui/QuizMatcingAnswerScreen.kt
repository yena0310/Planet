package com.example.planet.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForwardIos
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material3.IconButton
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.planet.QuizItem
import com.example.planet.QuizType
import com.example.planet.R
import com.example.planet.utils.RankingUtils
import com.example.planet.utils.UserStateManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun QuizMatchingAnswerScreen(
    navController: NavHostController,
    quizList: List<QuizItem>,
    index: Int,
    matchedPairs: Map<String, String>,
    quizIds: List<String>
) {
    val pretendardsemibold = FontFamily(Font(R.font.pretendardsemibold))

    // Firebase
    val currentUserId = UserStateManager.getUserId()
    val db = FirebaseFirestore.getInstance()

    // 사용자 정보 상태
    var userScore by remember { mutableStateOf(0) }
    var totalQuestions by remember { mutableStateOf(100) }
    var isLoading by remember { mutableStateOf(true) }
    var scoreUpdated by remember { mutableStateOf(false) }

    // 전달받은 quizIds로 문제 찾기
    val currentQuizSet = remember(quizIds) {
        quizIds.mapNotNull { id ->
            quizList.find { it.id == id }
        }
    }

    // 정답 매칭 정보
    val correctPairs = remember(currentQuizSet) {
        currentQuizSet.associate { it.question to it.correctAnswer }
    }

    // 정답 체크 (모든 매칭이 맞아야만 정답)
    val isAllCorrect = remember(matchedPairs, correctPairs) {
        Log.d("QuizMatchingAnswer", "=== 정답 체크 시작 ===")
        Log.d("QuizMatchingAnswer", "매칭 개수: ${matchedPairs.size}, 정답 개수: ${correctPairs.size}")

        val result = matchedPairs.size == correctPairs.size &&
                matchedPairs.all { (question, userAnswer) ->
                    val trimmedQuestion = question.trim()
                    val trimmedUserAnswer = userAnswer.trim()
                    val correctAnswer = correctPairs[trimmedQuestion]?.trim()

                    Log.d("QuizMatchingAnswer", "비교: 질문='$trimmedQuestion'")
                    Log.d("QuizMatchingAnswer", "사용자='$trimmedUserAnswer' vs 정답='$correctAnswer'")
                    Log.d("QuizMatchingAnswer", "일치=${correctAnswer == trimmedUserAnswer}")

                    correctAnswer == trimmedUserAnswer
                }

        Log.d("QuizMatchingAnswer", "최종 결과: $result")
        result
    }

    // 틀린 문제들의 해설 수집
    val wrongExplanations = remember(matchedPairs, correctPairs) {
        matchedPairs.mapNotNull { (question, userAnswer) ->
            val correctAnswer = correctPairs[question]
            if (correctAnswer != userAnswer) {
                val quizItem = currentQuizSet.find { it.question == question }
                quizItem?.explanation ?: "해설이 없습니다."
            } else null
        }
    }

    // 점수 업데이트 로직
    LaunchedEffect(Unit) {
        Log.d("QuizMatchingAnswer", "=== 전달받은 매칭 결과 ===")
        matchedPairs.forEach { (question, userAnswer) ->
            val correctAnswer = correctPairs[question.trim()]?.trim()
            val isCorrect = correctAnswer == userAnswer.trim()
            Log.d("QuizMatchingAnswer", "UI 표시: '$question' -> 사용자:'$userAnswer', 정답:'$correctAnswer', 맞음:$isCorrect")
        }

        Log.d("QuizMatchingAnswer", "=== 정답 쌍들 ===")
        correctPairs.forEach { (q, a) ->
            Log.d("QuizMatchingAnswer", "정답: '$q' -> '$a' (답안 길이: ${a.length})")
        }
        Log.d("QuizMatchingAnswer", "매칭 해설 화면 초기화 - 인덱스: $index, 모든 매칭 정답 여부: $isAllCorrect")

        currentUserId?.let { userId ->
            Log.d("QuizMatchingAnswer", "사용자 UID: $userId")

            db.collection("users").document(userId).get()
                .addOnSuccessListener { userDoc ->
                    if (userDoc.exists()) {
                        val currentScore = userDoc.getLong("score")?.toInt() ?: 0
                        userScore = currentScore

                        Log.d("QuizMatchingAnswer", "현재 점수: $currentScore")

                        if (!scoreUpdated) {
                            val pointsToAdd = if (isAllCorrect) 10 else 5
                            val newScore = currentScore + pointsToAdd

                            Log.d("QuizMatchingAnswer", "점수 업데이트 - 추가점수: $pointsToAdd, 새점수: $newScore")

                            RankingUtils.updateUserScoreAndRanking(
                                db = db,
                                userId = userId,
                                newScore = newScore,
                                onSuccess = {
                                    userScore = newScore
                                    scoreUpdated = true
                                    isLoading = false
                                    Log.d("QuizMatchingAnswer", "✅ 점수 및 랭킹 업데이트 성공: $newScore")
                                },
                                onFailure = { error ->
                                    Log.e("QuizMatchingAnswer", "❌ 점수 업데이트 실패: $error")
                                    isLoading = false
                                }
                            )
                        } else {
                            isLoading = false
                        }
                    } else {
                        Log.w("QuizMatchingAnswer", "사용자 문서 없음")
                        isLoading = false
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("QuizMatchingAnswer", "사용자 정보 로드 실패", e)
                    isLoading = false
                }
        } ?: run {
            Log.w("QuizMatchingAnswer", "로그인되지 않은 사용자")
            isLoading = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF7AC5D3))
    ) {
        // 흰색 카드 영역
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp)
                .align(Alignment.BottomCenter)
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .background(Color.White)
                .height(800.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // 상단바 (백버튼, 문제수, 점수)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = {
                        navController.navigate("quiz") {
                            popUpTo("quiz") { inclusive = false }
                            launchSingleTop = true
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBackIosNew,
                            modifier = Modifier.size(25.dp),
                            tint = Color.Gray,
                            contentDescription = "뒤로 가기"
                        )
                    }

                    Text(
                        text = "${index + 1} / $totalQuestions",
                        fontSize = 18.sp,
                        color = Color.Black,
                        fontFamily = pretendardsemibold
                    )

                    Text(
                        text = if (isLoading) "로딩..." else "$userScore P",
                        fontSize = 13.sp,
                        color = if (scoreUpdated && !isLoading) Color(0xFF4CAF50) else Color.Gray,
                        fontFamily = pretendardsemibold
                    )
                }
                // 결과 제목 + 다음 문제 버튼 한 줄로
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Spacer(modifier = Modifier.weight(1f))

                    // 다음 문제 버튼
                    Row(
                        modifier = Modifier
                            .clickable {
                                val nextIndex = index + 1
                                Log.d("QuizAnswer", "다음 문제 클릭 - 다음 인덱스: $nextIndex")
                                if (nextIndex < quizList.size) {
                                    navController.navigate("quiz_question/$nextIndex")
                                } else {
                                    Log.d("QuizAnswer", "마지막 문제 완료, 퀴즈 메인으로 이동")
                                    navController.navigate("quiz")
                                }
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (index + 1 >= quizList.size) "완료" else "다음 문제",
                            fontSize = 12.sp,
                            fontFamily = pretendardsemibold,
                            color = Color(0xFF585858)
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowForwardIos,
                            contentDescription = "다음 문제",
                            modifier = Modifier.padding(start = 4.dp),
                            tint = Color(0xFF585858)
                        )
                    }
                }


                // 결과 제목
                Text(
                    text = if (isAllCorrect) "정답입니다!" else "아쉬워요!",
                    fontSize = 24.sp,
                    fontFamily = pretendardsemibold,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 결과 아이콘
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isAllCorrect) Icons.Outlined.CheckCircle else Icons.Default.Close,
                        contentDescription = "결과 아이콘",
                        tint = if (isAllCorrect) Color(0xFF4CAF50) else Color(0xFFE53935),
                        modifier = Modifier.size(80.dp)
                    )
                }

                // 점수 획득 표시
                Text(
                    text = if (isAllCorrect) "+10점 획득!" else "+5점 획득!",
                    fontSize = 18.sp,
                    fontFamily = pretendardsemibold,
                    color = if (isAllCorrect) Color(0xFF4CAF50) else Color(0xFF2196F3),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 매칭 결과 제목
                Text(
                    text = "매칭 결과",
                    fontSize = 20.sp,
                    fontFamily = pretendardsemibold,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // 매칭 결과 리스트
                matchedPairs.forEach { (question, userAnswer) ->
                    val correctAnswer = correctPairs[question]
                    val isCorrect = correctAnswer == userAnswer

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isCorrect) Color(0xFFE8F5E8) else Color(0xFFFFEBEE)
                            )
                            .border(
                                2.dp,
                                if (isCorrect) Color(0xFF4CAF50) else Color(0xFFE53935),
                                RoundedCornerShape(12.dp)
                            )
                            .padding(16.dp)
                    ) {
                        // 질문
                        Text(
                            text = "문제: $question",
                            fontSize = 14.sp,
                            fontFamily = pretendardsemibold,
                            color = Color.Black,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        // 사용자 답안
                        Text(
                            text = "선택한 답: $userAnswer",
                            fontSize = 14.sp,
                            fontFamily = pretendardsemibold,
                            color = if (isCorrect) Color(0xFF4CAF50) else Color(0xFFE53935)
                        )

                        // 정답 (틀렸을 때만 표시)
                        if (!isCorrect) {
                            Text(
                                text = "정답: $correctAnswer",
                                fontSize = 14.sp,
                                fontFamily = pretendardsemibold,
                                color = Color(0xFF4CAF50),
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }

                        // 결과 표시
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isCorrect) Icons.Outlined.CheckCircle else Icons.Default.Close,
                                contentDescription = if (isCorrect) "정답" else "오답",
                                tint = if (isCorrect) Color(0xFF4CAF50) else Color(0xFFE53935),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isCorrect) "정답" else "오답",
                                fontSize = 12.sp,
                                fontFamily = pretendardsemibold,
                                color = if (isCorrect) Color(0xFF4CAF50) else Color(0xFFE53935)
                            )
                        }
                    }
                }

                // 해설 영역 (틀린 문제가 있을 때만)
                if (wrongExplanations.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "해설",
                        fontSize = 20.sp,
                        fontFamily = pretendardsemibold,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    wrongExplanations.forEach { explanation ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFF5F5F5))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = explanation,
                                fontSize = 14.sp,
                                fontFamily = pretendardsemibold,
                                color = Color.Black,
                                lineHeight = 20.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(80.dp)) // 다음 버튼 공간
            }
        }
    }
}