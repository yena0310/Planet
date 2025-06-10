package com.example.planet.ui

import android.util.Log
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
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.material.rememberBottomSheetScaffoldState
import com.example.planet.utils.RankingUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.example.planet.utils.StudentRanking


// 랭킹 데이터 클래스
data class StudentRanking(
    val userId: String,
    val name: String,
    val score: Int,
    val rank: Int,
    val isCurrentUser: Boolean = false
)

@Composable
fun LeaderboardScreen(navController: NavHostController) {
    val pretendard = FontFamily(Font(R.font.pretendardsemibold))
    var selectedTab by remember { mutableStateOf("학생별") }

    // Firebase
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    val db = FirebaseFirestore.getInstance()

    // 상태 변수들
    var myRanking by remember { mutableStateOf(0) }
    var myScore by remember { mutableStateOf(0) }
    var percentileAhead by remember { mutableStateOf(0) }
    var classRankings by remember { mutableStateOf(listOf<StudentRanking>()) }
    var isLoading by remember { mutableStateOf(true) }

    // 실제 랭킹 데이터 로드
    LaunchedEffect(Unit) {
        Log.d("LeaderboardScreen", "랭킹 데이터 로드 시작")
        currentUser?.let { user ->
            RankingUtils.loadClassRankings(db, user.uid) { rankings, userRank, userScore, percentile ->
                classRankings = rankings
                myRanking = userRank
                myScore = userScore
                percentileAhead = percentile
                isLoading = false
            }
        } ?: run {
            isLoading = false
            Log.w("LeaderboardScreen", "로그인되지 않은 사용자")
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFCAEBF1))
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 탭 선택기
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

            // 내 등수 카드
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
                            text = when {
                                isLoading -> "..."
                                classRankings.size <= 1 -> "👑"
                                myScore == 0 -> "🎯"
                                else -> "#$myRanking"
                            },
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = if (classRankings.size <= 1 || myScore == 0) 24.sp else 21.sp,
                            fontFamily = pretendard
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = if (isLoading) "로딩중..."
                        else "다른 학급 친구들보다\n${percentileAhead}% 앞서고 있어요!",
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = pretendard,
                        fontSize = 13.sp,
                        lineHeight = 15.sp,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            LeaderboardScreenWithBottomSheet(classRankings, isLoading, myScore)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun LeaderboardScreenWithBottomSheet(rankings: List<StudentRanking>, isLoading: Boolean, myScore: Int) {
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
                LeaderboardList(rankings, isLoading)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFCAEBF1))
        ) {
            // 시상대
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

                // 상위 3명 표시
                if (!isLoading && rankings.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .offset(y = (-120).dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        // 2등 (왼쪽)
                        if (rankings.size >= 2) {
                            PodiumItem(
                                name = rankings[1].name,
                                score = rankings[1].score,
                                rank = 2,
                                modifier = Modifier.offset(x = 10.dp)
                            )
                        }

                        // 1등 (가운데)
                        if (rankings.isNotEmpty()) {
                            PodiumItem(
                                name = rankings[0].name,
                                score = rankings[0].score,
                                rank = 1
                            )
                        }

                        // 3등 (오른쪽)
                        if (rankings.size >= 3) {
                            PodiumItem(
                                name = rankings[2].name,
                                score = rankings[2].score,
                                rank = 3,
                                modifier = Modifier.offset(x = (-10).dp)
                            )
                        }
                    }
                } else {
                    // 로딩 중이거나 데이터가 없을 때
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .offset(y = (-120).dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        PodiumItem(name = "곧 채워질", score = 0, rank = 2, modifier = Modifier.offset(x = 10.dp))
                        PodiumItem(name = if (isLoading) "로딩중" else "당신", score = myScore, rank = 1)
                        PodiumItem(name = "예정", score = 0, rank = 3, modifier = Modifier.offset(x = (-10).dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun LeaderboardList(rankings: List<StudentRanking>, isLoading: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE0F7FA), shape = RoundedCornerShape(24.dp))
            .padding(horizontal = 16.dp, vertical = 15.dp)
    ) {
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("로딩중...", fontSize = 16.sp, color = Color.Gray)
            }
        } else if (rankings.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("🎓", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "학급의 첫 번째 학생이에요!",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF546A6E)
                    )
                    Text(
                        "퀴즈를 풀고 점수를 올려보세요",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                itemsIndexed(rankings) { index, student ->
                    LeaderboardRow(
                        rank = student.rank,
                        name = student.name,
                        score = student.score,
                        color = getStudentColor(index),
                        isMe = student.isCurrentUser
                    )
                }
            }
        }
    }
}

// 학생별 색상 지정
fun getStudentColor(index: Int): Color {
    val colors = listOf(
        Color(0xFFCCF1E5), // 1등
        Color(0xFFFFD6DC), // 2등
        Color(0xFFD7D7FB), // 3등
        Color(0xFFE8F5E8), // 4등
        Color(0xFFFFF2CC), // 5등
        Color(0xFFE8E8E8)  // 기타
    )
    return colors.getOrElse(index) { Color.LightGray }
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
        1 -> (-30).dp
        2 -> (0).dp
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
            contentAlignment = Alignment.Center
        ) {}

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
        // 순위 번호
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

        // 왕관 아이콘
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