package com.example.planet.ui

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.material.icons.filled.KeyboardDoubleArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.planet.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@SuppressLint("SuspiciousIndentation")
@Composable
fun StudyQuizPage(navController: NavHostController) {
    val pretendardsemibold = FontFamily(Font(R.font.pretendardsemibold))
    val pretendardbold = FontFamily(Font(R.font.pretendardbold))
    val context = LocalContext.current

    // Firebase
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    val db = FirebaseFirestore.getInstance()

    // 사용자 정보 상태
    var userName by remember { mutableStateOf("로딩중...") }
    var userScore by remember { mutableStateOf(0) }
    var lastQuestionIndex by remember { mutableStateOf(1) }
    var isLoading by remember { mutableStateOf(true) }

    // 사용자 정보 가져오기
    LaunchedEffect(Unit) {
        Log.d("StudyQuizPage", "사용자 정보 로드 시작")
        currentUser?.let { user ->
            Log.d("StudyQuizPage", "사용자 UID: ${user.uid}")

            db.collection("users").document(user.uid).get()
                .addOnSuccessListener { userDoc ->
                    Log.d("StudyQuizPage", "사용자 문서 존재: ${userDoc.exists()}")
                    if (userDoc.exists()) {
                        Log.d("StudyQuizPage", "사용자 문서 데이터: ${userDoc.data}")

                        userName = userDoc.getString("name") ?: "이름 없음"
                        userScore = userDoc.getLong("score")?.toInt() ?: 0
                        lastQuestionIndex = userDoc.getLong("lastQuestionIndex")?.toInt() ?: 1

                        Log.d("StudyQuizPage", "사용자 정보 - 이름: $userName, 점수: $userScore, 마지막문제: $lastQuestionIndex")
                        isLoading = false
                    } else {
                        Log.w("StudyQuizPage", "사용자 문서 없음")
                        userName = "정보 없음"
                        isLoading = false
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("StudyQuizPage", "사용자 정보 로드 실패", e)
                    userName = "로드 실패"
                    isLoading = false
                }
        } ?: run {
            Log.w("StudyQuizPage", "로그인되지 않은 사용자")
            userName = "로그인 필요"
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFCAEBF1))
            .padding(
                start = 20.dp,
                end = 20.dp,
                top = 70.dp
            )
    ) {

        // ======= 출석 헤더 =======
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isLoading) "로딩중..." else "🌞 안녕하세요, ${userName}님!",
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 14.sp,
                fontFamily = pretendardsemibold
            )
            Text(
                text = "${userScore} P",
                fontSize = 14.sp,
                color = Color.Black,
                fontFamily = pretendardsemibold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ======= 최근 퀴즈 박스 =======
        Surface(
            tonalElevation = 8.dp,
            shadowElevation = 8.dp,
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .customShadow()
                .clickable {
                    // 다음 문제 인덱스 계산 (1부터 시작하므로 -1)
                    val nextIndex = if (lastQuestionIndex <= 1) 0 else lastQuestionIndex - 1
                    navController.navigate("quiz_question/$nextIndex")
                }
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "RECENT QUIZ",
                    color = Color.Gray,
                    fontSize = 12.06.sp,
                    fontFamily = pretendardbold,
                    modifier = Modifier.padding(start = 15.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 15.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isLoading) "로딩중..."
                            else if (lastQuestionIndex <= 1) "첫 문제를 풀어보세요 !"
                            else "${lastQuestionIndex}번 문제부터 계속하기",
                            color = Color(0xFF546A6E),
                            fontSize = 16.64.sp,
                            style = MaterialTheme.typography.titleMedium,
                            fontFamily = pretendardbold
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        Icon(
                            imageVector = Icons.Default.KeyboardDoubleArrowRight,
                            contentDescription = "Next",
                            tint = Color(0xFF546A6E)
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // ======= 틀린문제 복습 박스 =======
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .customShadow()
                .clickable { /*TODO*/ }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 30.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "틀렸던 문제를 다시 풀어볼까요?",
                    color = Color(0xFF546A6E),
                    fontSize = 18.sp,
                    style = MaterialTheme.typography.titleMedium,
                    fontFamily = pretendardbold
                )
                Spacer(modifier = Modifier.width(6.dp))

                Icon(
                    imageVector = Icons.Default.KeyboardDoubleArrowRight,
                    contentDescription = "Next",
                    tint = Color(0xFF546A6E)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ===== 흰색 박스 =====
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
            ) {
                Text(
                    text = "Study Quizzes",
                    fontSize = 20.sp,
                    fontFamily = pretendardbold,
                    color = Color(0xFF546A6E)
                )

                Spacer(modifier = Modifier.height(20.dp))

                val selectedChapterIndex = remember { mutableStateOf(0) }

                // 🆕 챕터 정보 계산
                val chapters = listOf(
                    Triple("1", "Chapter 1", 80),
                    Triple("2", "Chapter 2", 80),
                    Triple("3", "Chapter 3", 80),
                    Triple("4", "Chapter 4", 80),
                    Triple("5", "Chapter 5", 80)
                )

                chapters.forEachIndexed { index, (number, title, totalQuestions) ->
                    // 챕터별 시작 인덱스 계산 (0부터 시작)
                    val chapterStartIndex = index * totalQuestions
                    val chapterEndIndex = chapterStartIndex + totalQuestions - 1

                    // 완료 여부 확인 (마지막 문제 인덱스가 챕터 끝을 넘었는지)
                    val isCompleted = !isLoading && lastQuestionIndex > chapterEndIndex + 1

                    // 현재 진행 중인 챕터인지 확인
                    val isCurrentChapter = !isLoading &&
                            lastQuestionIndex > chapterStartIndex &&
                            lastQuestionIndex <= chapterEndIndex + 1

                    // 해당 챕터에서 몇 문제 완료했는지 계산
                    val completedInChapter = when {
                        isCompleted -> totalQuestions
                        isCurrentChapter -> (lastQuestionIndex - 1) - chapterStartIndex
                        else -> 0
                    }

                    val subtitle = when {
                        isLoading -> "로딩중..."
                        isCompleted -> "$totalQuestions 문제 | 완료!"
                        isCurrentChapter -> "$totalQuestions 문제 | ${completedInChapter}/$totalQuestions"
                        else -> "$totalQuestions 문제"
                    }

                    val backgroundColor = if (isCompleted) Color(0xFF4E4E58) else Color.White
                    val borderColor = if (isCompleted) Color.Transparent else Color(0xFFB9DEE4)
                    val titleColor = if (isCompleted) Color(0xFFC2EFF7) else Color(0xFF546A6E)
                    val subtitleColor = if (isCompleted) Color(0xFF95D0DB) else Color(0xFF858494)

                    Button(
                        onClick = {
                            val startIndex = when {
                                // 완료된 챕터면 해당 챕터 첫 문제로
                                isCompleted -> chapterStartIndex
                                // 현재 진행 중인 챕터면 마지막 문제 인덱스로
                                isCurrentChapter -> lastQuestionIndex - 1
                                // 아직 시작하지 않은 챕터면 해당 챕터 첫 문제로
                                else -> chapterStartIndex
                            }
                            Log.d("StudyQuizPage", "챕터 $number 클릭 - startIndex: $startIndex")
                            navController.navigate("quiz_question/$startIndex")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .border(
                                width = 2.dp,
                                color = borderColor,
                                shape = RoundedCornerShape(20.dp)
                            )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 11.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .background(
                                        Color(0xFF53AEBE),
                                        shape = RoundedCornerShape(17.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = number,
                                    fontSize = 27.sp,
                                    fontFamily = pretendardbold,
                                    color = Color.White
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column {
                                Text(
                                    text = title,
                                    fontSize = 16.sp,
                                    fontFamily = pretendardbold,
                                    color = titleColor
                                )
                                Text(
                                    text = subtitle,
                                    fontSize = 14.sp,
                                    fontFamily = pretendardsemibold,
                                    color = subtitleColor
                                )
                            }

                            Spacer(modifier = Modifier.weight(1f))

                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.NavigateNext,
                                contentDescription = "Next",
                                tint = Color(0xFF53AEBE)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}