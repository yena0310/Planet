package com.example.planet.ui

import android.annotation.SuppressLint
import android.content.Context
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.planet.data.UserQuizRepository
import com.google.firebase.auth.FirebaseAuth

@SuppressLint("SuspiciousIndentation")
@Composable//-->메인퀴즈페이지
fun StudyQuizPage(navController: NavHostController) {
    val pretendardsemibold = FontFamily(Font(R.font.pretendardsemibold))
    val pretendardbold = FontFamily(Font(R.font.pretendardbold))
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFCAEBF1))
            .padding(
                start = 20.dp,
                end = 20.dp,
                top = 70.dp
                //bottom = innerPadding.calculateBottomPadding()
            )
    ) {

        // ======= 출석 헤더 =======
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "🌞 연속 7일 출석하고 있어요!",
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 14.sp,
                fontFamily = pretendardsemibold
            )
            Text(
                text = "89 P",
                fontSize = 14.sp,
                color = Color.Black,
                fontFamily = pretendardsemibold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ======= 최근 퀴즈 박스 (버튼 + 그림자 + TODO 이동) =======
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
                    val userId = FirebaseAuth.getInstance().currentUser?.uid
                    if (userId != null) {
                        UserQuizRepository.fetchLastQuestionIndex(userId) { lastIndex ->
                            val startIndex = lastIndex ?: 0
                    navController.navigate("quiz_question/$lastIndex")
                }}}
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
                            text = "이어서 문제를 풀어보세요 !", // TODO: 히스토리 확인해서 최근 문제 또는 첫 문제로 멘트 변경
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
            //elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
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
                    .verticalScroll(rememberScrollState()) // ← 스크롤 적용
                    .padding(24.dp)
            ) {
                Text(
                    text = "Study Quizzes",
                    fontSize = 20.sp,
                    fontFamily = pretendardbold,
                    color = Color(0xFF546A6E) // 변경된 제목 색상
                )

                Spacer(modifier = Modifier.height(20.dp))

                val selectedChapterIndex = remember { mutableStateOf(0) }

                listOf(
                    Triple("1", "Chapter 1", "20 문제 | 완료!"),
                    Triple("2", "Chapter 2", "20 문제"),
                    Triple("3", "Chapter 3", "20 문제"),
                    Triple("4", "Chapter 4", "20 문제"),
                    Triple("5", "Chapter 5", "20 문제")
                ).forEachIndexed { index, (number, title, subtitle) ->

                    val isSelected = selectedChapterIndex.value == index

                    val backgroundColor = if (isSelected) Color(0xFF4E4E58) else Color.White
                    val borderColor = if (isSelected) Color.Transparent else Color(0xFFB9DEE4)
                    val titleColor = if (isSelected) Color(0xFFC2EFF7) else Color(0xFF546A6E)
                    val subtitleColor = if (isSelected) Color(0xFF95D0DB) else Color(0xFF858494)
                    val context = LocalContext.current
                    val sharedPref = context.getSharedPreferences("quiz_prefs", Context.MODE_PRIVATE)
                    Button(
                        onClick = {
                            val startIndex = if (number == "1") {
                                sharedPref.getInt("last_index", 0)
                            } else {
                                0
                            }
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
                }}
        }
    }
}
