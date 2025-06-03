package com.example.planet.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForwardIos
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.planet.R

@Composable
fun HomeScreen(navController: NavHostController) {

    val pretendardsemibold = FontFamily(Font(R.font.pretendardsemibold))
    val pretendardbold = FontFamily(Font(R.font.pretendardbold))
    val iconTint = Color(0xFF546A6E)


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
                fontSize = 12.sp,
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
                    navController.navigate("matching_quiz")//("quiz_question/0")
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
                            text = "첫 문제를 풀어보세요 !", // TODO: 히스토리 확인해서 최근 문제 또는 첫 문제로 멘트 변경
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

        // ======= 순위 박스 (그림자 + 텍스트 색상 수정 + 구분선 추가) =======
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF)),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .customShadow()
                .height(60.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "내 등수",
                        fontSize = 13.sp,
                        style = MaterialTheme.typography.bodyMedium,
                        fontFamily = pretendardbold,
                        color = Color(0xFF284449)
                    )
                    Text(
                        text = "# 6",
                        fontSize = 15.sp,
                        style = MaterialTheme.typography.titleLarge,
                        fontFamily = pretendardbold,
                        color = Color(0xFF284449)
                    )
                }

                // 👉 중앙 세로 구분선
                Box(
                    modifier = Modifier
                        .width(1.dp)               // 세로선이므로 width는 얇게
                        .height(30.dp)             // 높이는 원하는 만큼
                        .background(Color.LightGray)
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "학교 점수",
                        fontSize = 13.sp,
                        style = MaterialTheme.typography.bodyMedium,
                        fontFamily = pretendardbold,
                        color = Color(0xFF284449)
                    )
                    Text(
                        text = "# 14",
                        fontSize = 15.sp,
                        style = MaterialTheme.typography.titleLarge,
                        fontFamily = pretendardbold,
                        color = Color(0xFF284449)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .customShadow()
                .height(IntrinsicSize.Min), // 높이 고정보다는 콘텐츠에 맞게
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "분리배출 도우미",
                    fontSize = 19.sp,
                    style = MaterialTheme.typography.titleLarge,
                    fontFamily = pretendardbold,
                    color = Color(0xFF284449)
                )

                Text(
                    text = "헷갈리는 분리배출, AI 가이드를 받아보세요!",
                    fontSize = 14.sp,
                    style = MaterialTheme.typography.bodyMedium,
                    fontFamily = pretendardbold,
                    color = Color(0xff859DA1)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center, // 버튼들 전체 중앙 정렬
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Button(
                            onClick = { navController.navigate("camera") },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE4FBFF))
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "폐기물 분리",
                                    fontSize = 16.sp,
                                    fontFamily = pretendardbold,
                                    color = Color(0xFF284449)
                                )

                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.ArrowForwardIos,
                                    contentDescription = "Next",
                                    tint = iconTint,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }

                        Button(
                            onClick = { navController.navigate("camera") },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE4FBFF))
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "분리배출 표시",
                                    fontSize = 16.sp,
                                    fontFamily = pretendardbold,
                                    color = Color(0xFF284449)
                                )

                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.ArrowForwardIos,
                                    contentDescription = "Next",
                                    tint = iconTint,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
