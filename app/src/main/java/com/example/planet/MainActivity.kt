package com.example.planet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                QuizQuestionScreen()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun QuizQuestionScreen() {

    val pretendardsemibold = FontFamily(
        Font(R.font.pretendardsemibold)
    )


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFC2E38E)) // 배경 연두색
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp)
                .align(Alignment.BottomCenter)
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .background(Color.White)
                .height(835.dp)
        ) {

            // ✅ Row로 정렬: 백버튼, 문제수, 점수
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.TopCenter),
                verticalAlignment = Alignment.CenterVertically, // 세로 가운데 정렬
                horizontalArrangement = Arrangement.SpaceBetween // 양끝으로 배치하면 필요 없음
            ) {
                // 🔙 이전으로 돌아가기 버튼
                IconButton(onClick = { /* TODO: 뒤로 가기 */ }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "뒤로 가기",
                    )
                }

                // ✅ 문제 수 (중앙)
                Text(
                    text = "1 / 100", // 임시 데이터
                    fontSize = 18.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    fontFamily = pretendardsemibold

                )

                // ✅ 점수 (우측)
                Text(
                    text = "89 P", // 임시 데이터
                    fontSize = 16.sp,
                    color = Color.Black,
                    fontFamily = pretendardsemibold
                )
            }

            // ✅ 문제 텍스트
            Text(
                text = "종이팩은 일반 종이류와 함께 배출한다.",
                fontSize = 20.sp,
                color = Color.Black,
                fontFamily = pretendardsemibold,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 60.dp),
                textAlign = TextAlign.Center
            )

            // ✅ O/X 버튼
            Row(
                modifier = Modifier
                    .fillMaxWidth() // 흰 박스 기준 가로 너비 동일
                    .align(Alignment.BottomCenter)
                    .height(180.dp) // 정사각형 높이 (더 크고 싶으면 조절)
            ) {
                // O 버튼
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f) // ✅ 정사각형 보장!
                        .clip(RoundedCornerShape(topStart = 16.dp)) // 좌상단만 둥글게
                        .background(Color(0xFFE56A6A)) // ✅ 더 선명한 빨강
                        .clickable { /* TODO: O 선택 동작 */ },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "O",
                        color = Color.White,
                        fontSize = 100.sp, // ✅ 크기 키움
                        fontFamily = pretendardsemibold
                    )
                }

                // X 버튼
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f) // ✅ 정사각형 보장!
                        .clip(RoundedCornerShape(topEnd = 16.dp)) // 우상단만 둥글게
                        .background(Color(0xFF6A93E5)) // ✅ 더 선명한 파랑
                        .clickable { /* TODO: X 선택 동작 */ },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "X",
                        color = Color.White,
                        fontSize = 100.sp, // ✅ 크기 키움
                        fontFamily = pretendardsemibold
                    )
                }
        }

    }
}}
