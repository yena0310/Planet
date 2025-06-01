package com.example.planet.ui

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.planet.R
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.ExperimentalMaterialApi
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.rememberBottomSheetScaffoldState
@Composable
fun LeaderboardScreen(navController: NavHostController) {
    val pretendard = FontFamily(Font(R.font.pretendardsemibold))
    var selectedTab by remember { mutableStateOf("학생별") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            //.padding(innerPadding)
            .background(Color(0xFFCAEBF1))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .padding(top = 64.dp)
                    .width(300.dp)
                    .height(47.dp)
                    .align(Alignment.CenterHorizontally)
                    .border(2.dp, Color(0xFF60B6C2), RoundedCornerShape(22.dp))
                    .clip(RoundedCornerShape(20.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(5.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val activeColor = Color(0xFF7AC5D3)
                    val inactiveColor = Color.Gray

                    listOf("학생별", "학급별").forEach { tab ->
                        Box(
                            modifier = Modifier
                                .width(150.dp)
                                .height(36.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (selectedTab == tab) Color.White else Color.Transparent)
                                .clickable { selectedTab = tab },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = tab,
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold,
                                fontFamily = pretendard,
                                fontSize = 15.sp,
                                color = if (selectedTab == tab) activeColor else inactiveColor
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier
                    .width(350.dp)
                    .height(80.dp)
                    .padding(horizontal = 24.dp)
                    .align(Alignment.CenterHorizontally)
                    .border(2.dp, Color(0xFF60B6C2), RoundedCornerShape(22.dp))
                    .clip(RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .background(Color(0xFF60B6C2), shape = RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "#4",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 21.sp,
                            fontFamily = pretendard
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = "다른 사용자들보다\n60% 앞서고 있어요!",
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = pretendard,
                        fontSize = 13.sp,
                        lineHeight = 15.sp,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            LeaderboardScreenWithBottomSheet()
            Column(modifier = Modifier.fillMaxSize()) {

                // podium 이미지 + 프로필을 포함한 Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp), // 이미지 + Row를 담을 공간
                    contentAlignment = Alignment.BottomCenter
                ) {
                    // podium 이미지
                    Image(
                        painter = painterResource(id = R.drawable.podium),
                        contentDescription = "시상대",
                        modifier = Modifier
                            .width(300.dp)
                            .height(200.dp)
                            .offset(y = 60.dp)
                    )

                    // podium 위 Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .offset(y = (-120).dp), // 너무 겹치지 않도록 적절히 조절
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        PodiumItem(
                            name = "이슈니",
                            score = 1469,
                            rank = 2,
                            modifier = Modifier.offset(x = 10.dp)
                        )
                        PodiumItem(name = "김슈니", score = 2569, rank = 1)
                        PodiumItem(
                            name = "박슈니",
                            score = 1053,
                            rank = 3,
                            modifier = Modifier.offset(x = (-10).dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))


            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun LeaderboardScreenWithBottomSheet() {
    val sheetState = rememberBottomSheetScaffoldState()
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    BottomSheetScaffold(
        scaffoldState = sheetState,
        sheetPeekHeight = 100.dp,
        sheetContent = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(screenHeight * 0.9f)
            ) {
                LeaderboardList() // ✅ 이미 완성된 리스트!
            }
        }
    ) { innerPadding ->
        // ✅ 여기 podium UI를 "진짜"로 구성
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFCAEBF1))
        ) {
            // podium 이미지와 Row
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Image(
                    painter = painterResource(id = R.drawable.podium),
                    contentDescription = "시상대",
                    modifier = Modifier
                        .width(300.dp)
                        .height(200.dp)
                        .offset(y = 60.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .offset(y = (-120).dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom
                ) {
                    PodiumItem(name = "이슈니", score = 1469, rank = 2, modifier = Modifier.offset(x = 10.dp))
                    PodiumItem(name = "김슈니", score = 2569, rank = 1)
                    PodiumItem(name = "박슈니", score = 1053, rank = 3, modifier = Modifier.offset(x = (-10).dp))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

        }
    }
}

@Composable
fun LeaderboardList() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE0F7FA), shape = RoundedCornerShape(24.dp)) // 하늘색 배경 전체로 적용
            .padding(horizontal = 16.dp, vertical = 15.dp)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { LeaderboardRow(1, "김슈니", 2569, Color(0xFFCCF1E5), isMe = true) }
            item { LeaderboardRow(2, "이슈니", 1469, Color(0xFFFFD6DC)) }
            item { LeaderboardRow(3, "박슈니", 1053, Color(0xFFD7D7FB)) }
            items(30) { index ->
                LeaderboardRow(
                    rank = index + 4,
                    name = "사용자 $index",
                    score = 1000 - index * 10,
                    color = Color.LightGray
                )
            }
        }
    }
}

@Composable
fun PodiumItem(name: String, score: Int, rank: Int, modifier: Modifier = Modifier) {
    val avatarColor = when (rank) {
        1 -> Color(0xFFDFF5E5)
        2 -> Color(0xFFFFD5DC)
        3 -> Color(0xFFD9D6FF)
        else -> Color.LightGray
    }
    val offsetY = when (rank) {
        1 -> (-30).dp  // 가장 높이
        2 -> (0).dp  // 중간
        3 -> (+30).dp
        else -> 0.dp
    }

    Column(
        modifier = modifier
            .width(80.dp)
            .offset(y = offsetY),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .border(0.5.dp, Color(0xff53AEBE), shape = CircleShape)
                .background(avatarColor, shape = CircleShape),
            contentAlignment = Alignment.Center)
        {}

        Text(
            text = name,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )

        Box(
            modifier = Modifier
                .background(Color.White, shape = RoundedCornerShape(40))
                .padding(horizontal = 12.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$score P",
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )
        }
    }
}

@Composable
fun LeaderboardRow(rank: Int, name: String, score: Int, color: Color, isMe: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(85.dp)
            .clip(RoundedCornerShape(32.dp))
            .background(Color.White)
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 순위 번호 (작고 연한 동그라미)
        Box(
            modifier = Modifier
                .size(23.dp)
                .border(1.dp, Color(0xFFDADADA), shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text("$rank", fontSize = 10.sp, color = Color.Gray)
        }

        Spacer(modifier = Modifier.width(12.dp))

        // 프로필 색상 원
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(color, CircleShape)
        )

        Spacer(modifier = Modifier.width(12.dp))

        // 이름과 점수
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Text("${score} points", fontSize = 12.sp, color = Color.Gray)
        }

        // 왕관 아이콘 (hexagon 느낌 흉내내기용 background + padding)
        Box(
            modifier = Modifier
                .background(
                    color = if (isMe) Color(0xFFFFC107) else Color(0xFFF1F1F1),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(6.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = if (isMe) "내 등수" else null,
                tint = if (isMe) Color.White else Color.LightGray,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

