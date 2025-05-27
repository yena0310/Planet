package com.example.planet.ui

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.planet.QuizItem
import com.example.planet.R

@Composable//-->O/X 문제페이지
fun QuizOXQuestionScreen(navController: NavHostController, quiz: QuizItem, index: Int) {

    val pretendardsemibold = FontFamily(
        Font(R.font.pretendardsemibold)
    )
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        context.getSharedPreferences("quiz_prefs", Context.MODE_PRIVATE)
            .edit()
            .putInt("last_index", index)
            .apply()
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF7AC5D3)) // 배경
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

            // 상단 정보: 뒤로가기, 문제 수, 점수
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.TopCenter),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = {
                    navController.navigate("quiz")
                }) {
                    Icon(
                        imageVector = Icons.Rounded.ArrowBackIosNew,
                        modifier = Modifier.size(25.dp),
                        tint = Color.Gray,
                        contentDescription = "뒤로 가기"
                    )
                }

                Text(
                    text = "${index + 1} / 20", // 문제 번호
                    fontSize = 18.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(horizontal = 16.dp),
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
                fontSize = 24.sp,
                color = Color.Black,
                fontFamily = pretendardsemibold,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(horizontal = 60.dp, vertical = 250.dp),
                textAlign = TextAlign.Center
            )

            // O / X 버튼
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .height(180.dp)
            ) {
                // O 버튼
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(topStart = 16.dp))
                        .background(Color(0xFFE56A6A))
                        .clickable {
                            val route = "quiz_answer/$index?userAnswer=O"
                            navController.navigate(route)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "O",
                        color = Color.White,
                        fontSize = 100.sp,
                        fontFamily = pretendardsemibold
                    )
                }

                // X 버튼
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(topEnd = 16.dp))
                        .background(Color(0xFF6A93E5))
                        .clickable {
                            val route = "quiz_answer/$index?userAnswer=X" // 또는 "X"
                            navController.navigate(route)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "X",
                        color = Color.White,
                        fontSize = 100.sp,
                        fontFamily = pretendardsemibold
                    )
                }
            }
        }
    }
}
