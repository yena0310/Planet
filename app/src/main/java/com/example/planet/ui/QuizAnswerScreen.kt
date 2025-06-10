package com.example.planet.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

@Composable//-->통합 해설페이지
fun QuizAnswerScreen(
    navController: NavHostController,
    quizList: List<QuizItem>,
    index: Int,
    userAnswer: String?
) {
    val quiz = quizList[index]
    val pretendardsemibold = FontFamily(Font(R.font.pretendardsemibold))
    val isCorrect = userAnswer?.trim()?.equals(quiz.correctAnswer.trim(), ignoreCase = true) == true

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
                    text = "${index + 1} / 100",
                    fontSize = 18.sp,
                    color = Color.Black,
                    fontFamily = pretendardsemibold
                )

                Text(
                    text = "89 P",
                    fontSize = 13.sp,
                    color = Color.Gray,
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
                    .align(Alignment.TopEnd) // 오른쪽 상단에 고정
                    .padding(top = 200.dp, end = 30.dp) // 🔽 위치 조정
                    .clickable {
                        val nextIndex = index + 1
                        if (nextIndex < quizList.size) {
                            navController.navigate("quiz_question/$nextIndex")
                        } else {
                            navController.navigate("quiz")
                        }
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "다음 문제",
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
                    .height(300.dp)
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
