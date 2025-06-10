package com.example.planet.ui

import android.util.Log
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.planet.R
import com.example.planet.utils.RankingUtils
import com.example.planet.utils.customShadow
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun HomeScreen(navController: NavHostController) {

    val pretendardsemibold = FontFamily(Font(R.font.pretendardsemibold))
    val pretendardbold = FontFamily(Font(R.font.pretendardbold))
    val iconTint = Color(0xFF546A6E)

    // Firebase
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    val db = FirebaseFirestore.getInstance()

    // 사용자 정보 상태
    var userName by remember { mutableStateOf("로딩중...") }
    var userScore by remember { mutableStateOf(0) }
    var lastQuestionIndex by remember { mutableStateOf(1) }
    var myRanking by remember { mutableStateOf(0) }
    var schoolRanking by remember { mutableStateOf(0) }
    var isLoading by remember { mutableStateOf(true) }

    // 사용자 정보 가져오기
    LaunchedEffect(Unit) {
        Log.d("HomeScreen", "사용자 정보 로드 시작")
        currentUser?.let { user ->
            Log.d("HomeScreen", "사용자 UID: ${user.uid}")

            db.collection("users").document(user.uid).get()
                .addOnSuccessListener { userDoc ->
                    Log.d("HomeScreen", "사용자 문서 존재: ${userDoc.exists()}")
                    if (userDoc.exists()) {
                        Log.d("HomeScreen", "사용자 문서 데이터: ${userDoc.data}")

                        // 사용자 기본 정보
                        userName = userDoc.getString("name") ?: "이름 없음"
                        userScore = userDoc.getLong("score")?.toInt() ?: 0
                        lastQuestionIndex = userDoc.getLong("lastQuestionIndex")?.toInt() ?: 1
                        myRanking = userDoc.getLong("ranking")?.toInt() ?: 0

                        Log.d("HomeScreen", "사용자 정보 - 이름: $userName, 점수: $userScore, 마지막문제: $lastQuestionIndex, 랭킹: $myRanking")

                        // 학교 랭킹 계산 (같은 학교 내에서의 순위)
                        val schoolName = userDoc.getString("schoolName")
                        if (schoolName != null) {
                            RankingUtils.calculateSchoolRanking(db, schoolName, userScore) { ranking ->
                                schoolRanking = ranking
                                Log.d("HomeScreen", "학교 랭킹: $ranking")
                            }
                        }

                        isLoading = false
                    } else {
                        Log.w("HomeScreen", "사용자 문서 없음")
                        userName = "정보 없음"
                        isLoading = false
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("HomeScreen", "사용자 정보 로드 실패", e)
                    userName = "로드 실패"
                    isLoading = false
                }
        } ?: run {
            Log.w("HomeScreen", "로그인되지 않은 사용자")
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
                    // 마지막 문제 인덱스에 따라 다른 페이지로 이동
                    val nextQuestionIndex = if (lastQuestionIndex <= 1) 0 else lastQuestionIndex - 1
                    navController.navigate("quiz_question/$nextQuestionIndex")
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

        // ======= 순위 박스 =======
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
                        text = if (isLoading) "로딩중"
                        else if (myRanking > 0) "# $myRanking"
                        else "순위 없음",
                        fontSize = 15.sp,
                        style = MaterialTheme.typography.titleLarge,
                        fontFamily = pretendardbold,
                        color = Color(0xFF284449)
                    )
                }

                // 중앙 세로 구분선
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(30.dp)
                        .background(Color.LightGray)
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "학교 순위",
                        fontSize = 13.sp,
                        style = MaterialTheme.typography.bodyMedium,
                        fontFamily = pretendardbold,
                        color = Color(0xFF284449)
                    )
                    Text(
                        text = if (isLoading) "로딩중"
                        else if (schoolRanking > 0) "# $schoolRanking"
                        else "순위 없음",
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
                .height(IntrinsicSize.Min),
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
                    horizontalArrangement = Arrangement.Center,
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