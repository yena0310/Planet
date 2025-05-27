package com.example.planet.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.planet.R

@Composable
fun Mypage(navController: NavHostController){
    val pretendardBold = FontFamily(Font(R.font.pretendardbold))

    Box(
        modifier = Modifier
            .fillMaxSize()
            //.padding(innerPadding)
            .background(Color(0xFFCAEBF1))
    ) {// Settings Icon
        Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = "Settings",
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 50.dp, end = 20.dp)
                .size(28.dp),
            tint = Color.Gray
        )



        // 흰색 카드 박스
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 160.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 25.dp, end = 25.dp)
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                    .background(Color.White)
                    .height(600.dp)
            ) {
                Text(
                    text = "한국초등학교\n1학년 1반",
                    fontSize = 12.sp,
                    fontFamily = pretendardBold,
                    color = Color.Gray,
                    modifier = Modifier.padding(start = 24.dp, top=20.dp) // 왼쪽 패딩만 줌
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 100.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Rank box
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .background(Color.White)
                            .customShadow()
                            .background(Color(0xFF7AD1E0), RoundedCornerShape(16.dp))
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "내 등수\n#6",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF343434),
                            textAlign = TextAlign.Center,
                            //modifier = Modifier.fillMaxWidth(0.4f)
                        )
                        // ✅ 수직 선 (Divider)
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(36.dp) // 선의 높이
                                .background(Color(0xFF0C092A).copy(alpha = 0.3f)) // 연한 검정
                        )
                        Text(
                            "학급 등수\n#14",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF343434),
                            textAlign = TextAlign.Center,
                            //modifier = Modifier.fillMaxWidth(0.4f)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Quiz progress box
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(500.dp)
                            .background(Color(0xFFBCE4EC), RoundedCornerShape(16.dp))
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            buildAnnotatedString {
                                append("지금까지 총 ")
                                withStyle(style = SpanStyle(color = Color(0xFF259CB2))) {
                                    append("75문제")
                                }
                                append("를 풀었어요!")
                            },
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(20.dp))

                        // Circular progress indicator
                        Box(
                            modifier = Modifier.size(160.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            // 배경 원: 흰색 전체 100%
                            CircularProgressIndicator(
                                progress = {1f},
                                modifier = Modifier.fillMaxSize(),
                                color = Color.White,
                                strokeWidth = 12.dp
                            )

                            // 실제 진행도: 75%
                            CircularProgressIndicator(
                                progress = {0.75f},
                                modifier = Modifier.fillMaxSize(),
                                color = Color(0xFF28B6CC),
                                strokeWidth = 12.dp
                            )

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text("75", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0A0A32))
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text("/100", fontSize = 16.sp, color = Color(0x8028B6CC))
                                }
                                Text("quiz played", fontSize = 16.sp, color = Color.Gray) // ✅ 원 안에 들어감
                            }

                        }

                    }
                }

            }
        }
    }
    // 프로필 영역 전체
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 100.dp), // 🔽 화면 아래로 내림
        horizontalAlignment = Alignment.CenterHorizontally // 🔽 가운데 정렬
    ){
        Box(
            contentAlignment = Alignment.BottomEnd,
            modifier = Modifier
                .size(100.dp) // 프로필 이미지 전체 크기
        ) {
            // 핑크색 원
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFD69ACC)) // 연한 분홍색
            )

            // ✏️ 편집 아이콘
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .offset(x = (-6).dp, y = (-6).dp)
                    .border(2.dp, Color.Gray, CircleShape)
                    .background(Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Profile",
                    modifier = Modifier.size(18.dp),
                    tint = Color.Gray
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        // Name
        Text("김아무개", fontSize = 20.sp, fontWeight = FontWeight.Bold)
    }
}
