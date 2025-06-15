package com.example.planet.ui

import android.util.Log
import androidx.compose.foundation.background
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
import com.example.planet.R
import com.example.planet.utils.RankingUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun QuizAnswerScreen(
    navController: NavHostController,
    quizList: List<QuizItem>,
    index: Int,
    userAnswer: String?
) {
    val quiz = quizList[index]
    val pretendardsemibold = FontFamily(Font(R.font.pretendardsemibold))
    val isCorrect = userAnswer?.trim()?.equals(quiz.correctAnswer.trim(), ignoreCase = true) == true

    // Firebase
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    val db = FirebaseFirestore.getInstance()

    // 사용자 정보 상태
    var userScore by remember { mutableStateOf(0) }
    var totalQuestions by remember { mutableStateOf(80) }
    var isLoading by remember { mutableStateOf(true) }
    var scoreUpdated by remember { mutableStateOf(false) }

    // 사용자 정보 로드 및 점수 업데이트
    LaunchedEffect(Unit) {
        Log.d("QuizAnswer", "해설 화면 초기화 - 인덱스: $index, 정답여부: $isCorrect, 사용자답안: $userAnswer")

        currentUser?.let { user ->
            Log.d("QuizAnswer", "사용자 UID: ${user.uid}")

            // 1. 현재 사용자 정보 가져오기
            db.collection("users").document(user.uid).get()
                .addOnSuccessListener { userDoc ->
                    if (userDoc.exists()) {
                        val currentScore = userDoc.getLong("score")?.toInt() ?: 0
                        userScore = currentScore

                        Log.d("QuizAnswer", "현재 점수: $currentScore")

                        // 2. 점수 계산 및 업데이트
                        if (!scoreUpdated) {
                            val pointsToAdd = if (isCorrect) 10 else 5
                            val newScore = currentScore + pointsToAdd

                            Log.d("QuizAnswer", "점수 업데이트 - 추가점수: $pointsToAdd, 새점수: $newScore")

                            // 3. RankingUtils를 통한 점수 및 랭킹 업데이트
                            RankingUtils.updateUserScoreAndRanking(
                                db = db,
                                userId = user.uid,
                                newScore = newScore,
                                onSuccess = {
                                    userScore = newScore
                                    scoreUpdated = true
                                    isLoading = false
                                    Log.d("QuizAnswer", "✅ 점수 및 랭킹 업데이트 성공: $newScore")
                                },
                                onFailure = { error ->
                                    Log.e("QuizAnswer", "❌ 점수 업데이트 실패: $error")
                                    isLoading = false
                                }
                            )
                        } else {
                            isLoading = false
                        }
                    } else {
                        Log.w("QuizAnswer", "사용자 문서 없음")
                        isLoading = false
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("QuizAnswer", "사용자 정보 로드 실패", e)
                    isLoading = false
                }
        } ?: run {
            Log.w("QuizAnswer", "로그인되지 않은 사용자")
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

            // 상단바
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

            // 문제 텍스트
            Text(
                text = quiz.question,
                fontSize = 16.sp,
                color = Color.Black,
                fontFamily = pretendardsemibold,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(horizontal = 60.dp, vertical = 100.dp),
                textAlign = TextAlign.Center
            )

            // 다음 문제 버튼
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 200.dp, end = 30.dp)
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

            Spacer(modifier = Modifier.height(20.dp))

            // 해설 영역
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
                    text = if (isCorrect) "정답!" else "오답!",
                    fontSize = 22.sp,
                    fontFamily = pretendardsemibold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(14.dp))

                Icon(
                    imageVector = if (isCorrect) Icons.Outlined.CheckCircle else Icons.Default.Close,
                    contentDescription = "결과 아이콘",
                    tint = if (isCorrect) Color(0xFFE56A6A) else Color(0xFF4A75E1),
                    modifier = Modifier.size(70.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // 점수 획득 표시
                Text(
                    text = if (isCorrect) "+10점 획득!" else "+5점 획득!",
                    fontSize = 16.sp,
                    fontFamily = pretendardsemibold,
                    color = if (isCorrect) Color(0xFF4CAF50) else Color(0xFF2196F3)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = quiz.explanation ?: "정답: ${quiz.correctAnswer}",
                    fontSize = 13.sp,
                    fontFamily = pretendardsemibold,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )

                if (!isCorrect && !userAnswer.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "당신의 답: $userAnswer",
                        fontSize = 11.sp,
                        fontFamily = pretendardsemibold,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}