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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.example.planet.utils.RankingUtils
import com.example.planet.utils.UserStateManager
import com.google.firebase.firestore.FirebaseFirestore
import com.example.planet.utils.StudentRanking
import com.example.planet.utils.ClassRanking

@Composable
fun LeaderboardScreen(navController: NavHostController) {
    val pretendard = FontFamily(Font(R.font.pretendardsemibold))
    var selectedTab by remember { mutableStateOf("학생별") }

    // Firebase
    val currentUserId = UserStateManager.getUserId()
    val db = FirebaseFirestore.getInstance()

    // 학생별 상태 변수들
    var myRanking by remember { mutableStateOf(0) }
    var myScore by remember { mutableStateOf(0) }
    var percentileAhead by remember { mutableStateOf(0) }
    var classRankings by remember { mutableStateOf(listOf<StudentRanking>()) }

    // 학급별 상태 변수들
    var myClassRanking by remember { mutableStateOf(0) }
    var myClassScore by remember { mutableStateOf(0) }
    var classPercentileAhead by remember { mutableStateOf(0) }
    var schoolClassRankings by remember { mutableStateOf(listOf<ClassRanking>()) }

    var isLoading by remember { mutableStateOf(true) }

    // 선택된 탭에 따라 데이터 로드
    LaunchedEffect(selectedTab) {
        Log.d("LeaderboardScreen", "랭킹 데이터 로드 시작 - 탭: $selectedTab")
        isLoading = true

        currentUserId?.let { userId ->
            when (selectedTab) {
                "학생별" -> {
                    RankingUtils.loadClassRankings(db, userId) { rankings, userRank, userScore, percentile ->
                        classRankings = rankings
                        myRanking = userRank
                        myScore = userScore
                        percentileAhead = percentile
                        isLoading = false
                        Log.d("LeaderboardScreen", "학생별 랭킹 로드 완료 - 랭킹: $userRank, 점수: $userScore")
                    }
                }
                "학급별" -> {
                    RankingUtils.loadSchoolClassRankings(db, userId) { rankings, classRank, userScore, percentile ->
                        schoolClassRankings = rankings
                        myClassRanking = classRank
                        myClassScore = userScore
                        classPercentileAhead = percentile
                        isLoading = false
                        Log.d("LeaderboardScreen", "학급별 랭킹 로드 완료 - 학급 랭킹: $classRank, 퍼센타일: $percentile")
                    }
                }
            }
        } ?: run {
            isLoading = false
            Log.w("LeaderboardScreen", "로그인되지 않은 사용자")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFCAEBF1))
            .verticalScroll(rememberScrollState()) // 🆕 전체 스크롤 가능
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
                            selectedTab == "학생별" && classRankings.size <= 1 -> "👑"
                            selectedTab == "학급별" && schoolClassRankings.size <= 1 -> "👑"
                            selectedTab == "학생별" && myScore == 0 -> "🎯"
                            selectedTab == "학급별" -> "#$myClassRanking"
                            else -> "#$myRanking"
                        },
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = if ((selectedTab == "학생별" && (classRankings.size <= 1 || myScore == 0)) ||
                            (selectedTab == "학급별" && schoolClassRankings.size <= 1)) 24.sp else 21.sp,
                        fontFamily = pretendard
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = if (isLoading) "로딩중..."
                    else when (selectedTab) {
                        "학생별" -> "다른 학급 친구들보다\n${percentileAhead}% 앞서고 있어요!"
                        "학급별" -> "다른 학급들보다\n${classPercentileAhead}% 앞서고 있어요!"
                        else -> ""
                    },
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = pretendard,
                    fontSize = 13.sp,
                    lineHeight = 15.sp,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 시상대 영역
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp), // 🆕 고정 높이
            contentAlignment = Alignment.BottomCenter
        ) {
            Image(
                painter = painterResource(id = R.drawable.podium),
                contentDescription = "시상대",
                modifier = Modifier
                    .width(300.dp)
                    .height(200.dp)
                    .offset(y = 50.dp)
            )

            // 상위 3명/3개 학급 표시
            if (!isLoading) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .offset(y = (-120).dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom
                ) {
                    if (selectedTab == "학생별" && classRankings.isNotEmpty()) {
                        // 학생별 시상대
                        if (classRankings.size >= 2) {
                            PodiumItem(
                                name = classRankings[1].name,
                                score = classRankings[1].score,
                                rank = 2,
                                modifier = Modifier.offset(x = 10.dp),
                                isClass = false
                            )
                        }

                        if (classRankings.isNotEmpty()) {
                            PodiumItem(
                                name = classRankings[0].name,
                                score = classRankings[0].score,
                                rank = 1,
                                isClass = false
                            )
                        }

                        if (classRankings.size >= 3) {
                            PodiumItem(
                                name = classRankings[2].name,
                                score = classRankings[2].score,
                                rank = 3,
                                modifier = Modifier.offset(x = (-10).dp),
                                isClass = false
                            )
                        }
                    } else if (selectedTab == "학급별" && schoolClassRankings.isNotEmpty()) {
                        // 학급별 시상대
                        if (schoolClassRankings.size >= 2) {
                            ClassPodiumItem(
                                classRanking = schoolClassRankings[1],
                                rank = 2,
                                modifier = Modifier.offset(x = 10.dp)
                            )
                        }

                        if (schoolClassRankings.isNotEmpty()) {
                            ClassPodiumItem(
                                classRanking = schoolClassRankings[0],
                                rank = 1
                            )
                        }

                        if (schoolClassRankings.size >= 3) {
                            ClassPodiumItem(
                                classRanking = schoolClassRankings[2],
                                rank = 3,
                                modifier = Modifier.offset(x = (-10).dp)
                            )
                        }
                    } else {
                        // 로딩 중이거나 데이터가 없을 때
                        PodiumItem(name = "곧 채워질", score = 0, rank = 2, modifier = Modifier.offset(x = 10.dp), isClass = false)
                        PodiumItem(name = if (isLoading) "로딩중" else "당신", score = if (selectedTab == "학생별") myScore else myClassScore, rank = 1, isClass = false)
                        PodiumItem(name = "예정", score = 0, rank = 3, modifier = Modifier.offset(x = (-10).dp), isClass = false)
                    }
                }
            } else {
                // 로딩 중
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .offset(y = (-120).dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom
                ) {
                    PodiumItem(name = "곧 채워질", score = 0, rank = 2, modifier = Modifier.offset(x = 10.dp), isClass = false)
                    PodiumItem(name = "로딩중", score = if (selectedTab == "학생별") myScore else myClassScore, rank = 1, isClass = false)
                    PodiumItem(name = "예정", score = 0, rank = 3, modifier = Modifier.offset(x = (-10).dp), isClass = false)
                }
            }
        }

        // 🆕 포디움 하단 선에 맞춰서 리스트 시작
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFE0F7FA), shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .padding(horizontal = 16.dp, vertical =  20.dp)
        ) {
            when (selectedTab) {
                "학생별" -> {
                    LeaderboardList(classRankings, isLoading)
                }
                "학급별" -> {
                    ClassLeaderboardList(schoolClassRankings, isLoading)
                }
            }
        }
    }
}

@Composable
fun ClassLeaderboardList(rankings: List<ClassRanking>, isLoading: Boolean) {
    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("로딩중...", fontSize = 16.sp, color = Color.Gray)
        }
    } else if (rankings.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("🏫", fontSize = 48.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "학교의 첫 번째 학급이에요!",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF546A6E)
                )
                Text(
                    "학급 친구들과 함께 퀴즈를 풀어보세요",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
    } else {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            rankings.forEachIndexed { index, classRanking ->
                ClassLeaderboardRow(
                    rank = classRanking.rank,
                    grade = classRanking.grade,
                    classNumber = classRanking.classNumber,
                    totalScore = classRanking.totalScore,
                    studentCount = classRanking.studentCount,
                    color = getClassColor(index),
                    isMyClass = classRanking.isCurrentClass
                )
            }
        }
    }
}

@Composable
fun LeaderboardList(rankings: List<StudentRanking>, isLoading: Boolean) {
    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("로딩중...", fontSize = 16.sp, color = Color.Gray)
        }
    } else if (rankings.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
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
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            rankings.forEachIndexed { index, student ->
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

// 학급별 색상 지정
fun getClassColor(index: Int): Color {
    val colors = listOf(
        Color(0xFFFFF2CC), // 1등 - 노란색
        Color(0xFFE8F5E8), // 2등 - 초록색
        Color(0xFFFFE6F3), // 3등 - 분홍색
        Color(0xFFE6F3FF), // 4등 - 파란색
        Color(0xFFF0E6FF), // 5등 - 보라색
        Color(0xFFE8E8E8)  // 기타 - 회색
    )
    return colors.getOrElse(index) { Color.LightGray }
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
fun ClassPodiumItem(classRanking: ClassRanking, rank: Int, modifier: Modifier = Modifier) {
    val avatarColor = when (rank) {
        1 -> Color(0xFFFFF2CC)
        2 -> Color(0xFFE8F5E8)
        3 -> Color(0xFFFFE6F3)
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
        ) {
            Text(
                text = "${classRanking.grade}-${classRanking.classNumber}",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        Text(
            text = "${classRanking.grade}학년 ${classRanking.classNumber}반",
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            textAlign = TextAlign.Center
        )

        Box(
            modifier = Modifier
                .background(Color.White, shape = RoundedCornerShape(40))
                .padding(horizontal = 8.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${classRanking.totalScore} P",
                fontSize = 9.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )
        }
    }
}

@Composable
fun PodiumItem(name: String, score: Int, rank: Int, modifier: Modifier = Modifier, isClass: Boolean = false) {
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
fun ClassLeaderboardRow(
    rank: Int,
    grade: Int,
    classNumber: Int,
    totalScore: Int,
    studentCount: Int,
    color: Color,
    isMyClass: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(75.dp)
            .clip(RoundedCornerShape(32.dp))
            .background(if (isMyClass) Color(0xFFE3F2FD) else Color.White)
            .border(
                width = if (isMyClass) 2.dp else 0.dp,
                color = if (isMyClass) Color(0xFF60B6C2) else Color.Transparent,
                shape = RoundedCornerShape(32.dp)
            )
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

        // 학급 표시 색상 원
        Box(
            modifier = Modifier
                .size(45.dp)
                .background(color, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$grade-$classNumber",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // 학급 정보와 점수
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${grade}학년 ${classNumber}반",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                if (isMyClass) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "내 학급",
                        fontSize = 11.sp,
                        color = Color(0xFF60B6C2),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .background(
                                color = Color(0xFFE3F2FD),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("총점 ${totalScore}점", fontSize = 12.sp, color = Color.Gray)
                Spacer(modifier = Modifier.width(8.dp))
                Text("${studentCount}명", fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
fun LeaderboardRow(rank: Int, name: String, score: Int, color: Color, isMe: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(75.dp)
            .clip(RoundedCornerShape(32.dp))
            .background(if (isMe) Color(0xFFE3F2FD) else Color.White)
            .border(
                width = if (isMe) 2.dp else 0.dp,
                color = if (isMe) Color(0xFF60B6C2) else Color.Transparent,
                shape = RoundedCornerShape(32.dp)
            )
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
                .size(45.dp)
                .background(color, CircleShape)
        )

        Spacer(modifier = Modifier.width(12.dp))

        // 이름과 점수
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                if (isMe) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "나",
                        fontSize = 11.sp,
                        color = Color(0xFF60B6C2),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .background(
                                color = Color(0xFFE3F2FD),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            Text("${score} points", fontSize = 12.sp, color = Color.Gray)
        }
    }
}