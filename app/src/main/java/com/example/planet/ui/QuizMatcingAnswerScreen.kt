package com.example.planet.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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

    // 🆕 전달받은 quizIds로 문제 찾기 (간단!)
    val currentQuizSet = remember(quizIds) {
        quizIds.mapNotNull { id ->
            quizList.find { it.id == id }
        }
    }

    // 🆕 정답 매칭 정보
    val correctPairs = remember(currentQuizSet) {
        currentQuizSet.associate { it.question to it.correctAnswer }
    }

    // 🆕 정답 체크 (모든 매칭이 맞아야만 정답)
    val isAllCorrect = remember(matchedPairs, correctPairs) {
        matchedPairs.size == correctPairs.size &&
                matchedPairs.all { (question, userAnswer) ->
                    correctPairs[question] == userAnswer
                }
    }

    // 🆕 틀린 문제들의 해설 수집
    val wrongExplanations = remember(matchedPairs, correctPairs) {
        matchedPairs.mapNotNull { (question, userAnswer) ->
            val correctAnswer = correctPairs[question]
            if (correctAnswer != userAnswer) {
                val quizItem = currentQuizSet.find { it.question == question }
                quizItem?.explanation ?: "해설이 없습니다."
            } else null
        }
    }

    // 🆕 기존 해설 페이지와 동일한 점수 로직 (정답 10점, 오답 5점)
    LaunchedEffect(Unit) {
        Log.d("QuizMatchingAnswer", "매칭 해설 화면 초기화 - 인덱스: $index, 모든 매칭 정답 여부: $isAllCorrect")

        currentUserId?.let { userId ->
            Log.d("QuizMatchingAnswer", "사용자 UID: $userId")

            // 1. 현재 사용자 정보 가져오기
            db.collection("users").document(userId).get()
                .addOnSuccessListener { userDoc ->
                    if (userDoc.exists()) {
                        val currentScore = userDoc.getLong("score")?.toInt() ?: 0
                        userScore = currentScore

                        Log.d("QuizMatchingAnswer", "현재 점수: $currentScore")

                        // 2. 점수 계산 및 업데이트 (기존과 동일한 로직)
                        if (!scoreUpdated) {
                            val pointsToAdd = if (isAllCorrect) 10 else 5
                            val newScore = currentScore + pointsToAdd

                            Log.d("QuizMatchingAnswer", "점수 업데이트 - 추가점수: $pointsToAdd, 새점수: $newScore")

                            // 3. RankingUtils를 통한 점수 및 랭킹 업데이트
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
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp)
                .align(Alignment.BottomCenter)
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .background(Color.White)
                .height(800.dp)
        ) {

            // 상단바 (기존과 동일)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.TopCenter),
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

            // 🆕 매칭 결과 영역 (2x2 그리드)
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(horizontal = 20.dp, vertical = 100.dp)
                    .heightIn(max = 250.dp)
            ) {
                val matchingList = matchedPairs.toList()

                // 2x2 그리드로 배치
                for (rowIndex in 0 until 2) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (colIndex in 0 until 2) {
                            val itemIndex = rowIndex * 2 + colIndex
                            if (itemIndex < matchingList.size) {
                                val (question, userAnswer) = matchingList[itemIndex]
                                val correctAnswer = correctPairs[question]
                                val isCorrect = correctAnswer == userAnswer

                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(2.dp)
                                ) {
                                    // 질문 박스
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(30.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(
                                                if (isCorrect) Color(0xFFE8F5E8)
                                                else Color(0xFFFFEBEE)
                                            )
                                            .border(
                                                1.dp,
                                                if (isCorrect) Color(0xFF4CAF50)
                                                else Color(0xFFE57373),
                                                RoundedCornerShape(6.dp)
                                            )
                                            .padding(4.dp)
                                    ) {
                                        Text(
                                            text = question,
                                            fontSize = 8.sp,
                                            fontFamily = pretendardsemibold,
                                            color = Color.Black,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.align(Alignment.Center),
                                            maxLines = 2
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(2.dp))

                                    // 답안 박스
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(40.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color(0xFFF5F5F5))
                                            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(6.dp))
                                            .padding(4.dp)
                                    ) {
                                        Text(
                                            text = userAnswer,
                                            fontSize = 7.sp,
                                            fontFamily = pretendardsemibold,
                                            color = Color(0xFF666666),
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.align(Alignment.Center),
                                            maxLines = 3
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 다음 문제 버튼 (기존과 동일)
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 200.dp, end = 30.dp)
                    .clickable {
                        val nextIndex = index + 1
                        Log.d("QuizMatchingAnswer", "다음 문제 클릭 - 다음 인덱스: $nextIndex")
                        if (nextIndex < quizList.size) {
                            navController.navigate("quiz_question/$nextIndex")
                        } else {
                            Log.d("QuizMatchingAnswer", "마지막 문제 완료, 퀴즈 메인으로 이동")
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

            Spacer(modifier = Modifier.height(20.dp))

            // 해설 영역 (기존과 동일한 스타일)
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = 70.dp)
                    .fillMaxWidth(0.80f)
                    .height(350.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFFF9F6F2))
                    .padding(29.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isAllCorrect) "정답!" else "오답!",
                    fontSize = 22.sp,
                    fontFamily = pretendardsemibold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(14.dp))

                Icon(
                    imageVector = if (isAllCorrect) Icons.Outlined.CheckCircle else Icons.Default.Close,
                    contentDescription = "결과 아이콘",
                    tint = if (isAllCorrect) Color(0xFFE56A6A) else Color(0xFF4A75E1),
                    modifier = Modifier.size(70.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // 🆕 점수 획득 표시 (기존과 동일한 로직)
                Text(
                    text = if (isAllCorrect) "+10점 획득!" else "+5점 획득!",
                    fontSize = 16.sp,
                    fontFamily = pretendardsemibold,
                    color = if (isAllCorrect) Color(0xFF4CAF50) else Color(0xFF2196F3)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // 🆕 결과별 메시지
                if (isAllCorrect) {
                    Text(
                        text = "모두 올바르게 연결했습니다!",
                        fontSize = 13.sp,
                        fontFamily = pretendardsemibold,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                } else {
                    // 🆕 틀린 문제들의 해설 표시
                    Column {
                        wrongExplanations.forEach { explanation ->
                            Text(
                                text = explanation,
                                fontSize = 11.sp,
                                fontFamily = pretendardsemibold,
                                color = Color.Black,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}