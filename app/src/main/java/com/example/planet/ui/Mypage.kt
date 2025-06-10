package com.example.planet.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

@Composable
fun Mypage(navController: NavHostController) {
    val pretendardBold = FontFamily(Font(R.font.pretendardbold))
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    val db = FirebaseFirestore.getInstance()

    // 사용자 정보 상태
    var userName by remember { mutableStateOf("로딩중...") }
    var schoolInfo by remember { mutableStateOf("로딩중...") }
    var myRanking by remember { mutableStateOf(0) }
    var schoolRanking by remember { mutableStateOf(0) }
    var solvedQuestions by remember { mutableStateOf(0) }
    var lastQuestionIndex by remember { mutableStateOf(1) }
    var userScore by remember { mutableStateOf(0) } // 🆕 사용자 점수 추가
    var isLoading by remember { mutableStateOf(true) }

    // 사용자 정보 가져오기
    LaunchedEffect(Unit) {
        Log.d("Mypage", "사용자 정보 로드 시작")
        currentUser?.let { user ->
            Log.d("Mypage", "사용자 UID: ${user.uid}")

            db.collection("users").document(user.uid).get()
                .addOnSuccessListener { userDoc ->
                    Log.d("Mypage", "사용자 문서 존재: ${userDoc.exists()}")
                    if (userDoc.exists()) {
                        Log.d("Mypage", "사용자 문서 데이터: ${userDoc.data}")

                        // 사용자 기본 정보
                        val name = userDoc.getString("name") ?: "이름 없음"
                        userName = name
                        myRanking = userDoc.getLong("ranking")?.toInt() ?: 0
                        lastQuestionIndex = userDoc.getLong("lastQuestionIndex")?.toInt() ?: 1

                        // 문제 수 계산 (인덱스 기반)
                        solvedQuestions = if (lastQuestionIndex > 1) lastQuestionIndex - 1 else 0

                        Log.d("Mypage", "사용자 기본 정보 - 이름: $name, 등수: $myRanking, 풀은문제: $solvedQuestions")

                        // 학교 정보 가져오기 (직접 저장된 정보 우선 사용)
                        val schoolName = userDoc.getString("schoolName")
                        val grade = userDoc.getLong("grade")?.toInt()
                        val classNum = userDoc.getLong("classNum")?.toInt()

                        if (schoolName != null && grade != null && classNum != null) {
                            schoolInfo = "$schoolName\n${grade}학년 ${classNum}반"
                            Log.d("Mypage", "학교 정보 (직접): $schoolInfo")

                            // 학교 랭킹 계산
                            calculateMypageSchoolRanking(db, schoolName, userDoc.getLong("score")?.toInt() ?: 0) { ranking ->
                                schoolRanking = ranking
                                isLoading = false
                                Log.d("Mypage", "학교 랭킹: $ranking")
                            }
                        } else {
                            Log.d("Mypage", "직접 저장된 정보 없음, 참조 방식으로 시도")
                            // 참조 방식으로 학교 정보 가져오기
                            val classRef = userDoc.getDocumentReference("classId")
                            classRef?.get()
                                ?.addOnSuccessListener { classDoc ->
                                    Log.d("Mypage", "반 문서 존재: ${classDoc.exists()}")
                                    if (classDoc.exists()) {
                                        Log.d("Mypage", "반 문서 데이터: ${classDoc.data}")

                                        val gradeFromClass = classDoc.getLong("grade")?.toInt()
                                        val classNumFromClass = classDoc.getLong("number")?.toInt()

                                        val schoolRef = classDoc.getDocumentReference("schoolId")
                                        schoolRef?.get()
                                            ?.addOnSuccessListener { schoolDoc ->
                                                Log.d("Mypage", "학교 문서 존재: ${schoolDoc.exists()}")
                                                if (schoolDoc.exists()) {
                                                    val schoolNameFromRef = schoolDoc.getString("name") ?: "학교명 없음"

                                                    if (gradeFromClass != null && classNumFromClass != null) {
                                                        schoolInfo = "$schoolNameFromRef\n${gradeFromClass}학년 ${classNumFromClass}반"
                                                        Log.d("Mypage", "학교 정보 (참조): $schoolInfo")

                                                        // 학교 랭킹 계산
                                                        calculateMypageSchoolRanking(db, schoolNameFromRef, userDoc.getLong("score")?.toInt() ?: 0) { ranking ->
                                                            schoolRanking = ranking
                                                            isLoading = false
                                                        }
                                                    } else {
                                                        schoolInfo = "학교 정보 불완전"
                                                        Log.w("Mypage", "반 정보 불완전")
                                                        isLoading = false
                                                    }
                                                } else {
                                                    schoolInfo = "학교 정보 없음"
                                                    Log.w("Mypage", "학교 문서 없음")
                                                    isLoading = false
                                                }
                                            }
                                            ?.addOnFailureListener { e ->
                                                schoolInfo = "학교 정보 로드 실패"
                                                Log.e("Mypage", "학교 정보 로드 실패", e)
                                                isLoading = false
                                            }
                                    } else {
                                        schoolInfo = "반 정보 없음"
                                        Log.w("Mypage", "반 문서 없음")
                                        isLoading = false
                                    }
                                }
                                ?.addOnFailureListener { e ->
                                    schoolInfo = "반 정보 로드 실패"
                                    Log.e("Mypage", "반 정보 로드 실패", e)
                                    isLoading = false
                                }
                        }
                    } else {
                        userName = "사용자 정보 없음"
                        schoolInfo = "학교 정보 없음"
                        Log.w("Mypage", "사용자 문서 없음")
                        isLoading = false
                    }
                }
                .addOnFailureListener { e ->
                    userName = "정보 로드 실패"
                    schoolInfo = "정보 로드 실패"
                    Log.e("Mypage", "사용자 정보 로드 실패", e)
                    isLoading = false
                }
        } ?: run {
            userName = "로그인 필요"
            schoolInfo = "로그인 필요"
            Log.w("Mypage", "사용자 없음")
            isLoading = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFCAEBF1))
    ) {
        // ✅ 1. 하얀 카드 (배경으로)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 100.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 25.dp)
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                    .background(Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 80.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 학교명 텍스트 (Firebase에서 가져온 정보)
                    Text(
                        text = schoolInfo,
                        fontSize = 12.sp,
                        fontFamily = pretendardBold,
                        lineHeight = 15.sp,
                        color = Color.Gray,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // 랭킹 박스 (실제 데이터 연동)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(70.dp)
                            .customShadow()
                            .background(Color(0xFF7AD1E0), RoundedCornerShape(16.dp))
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isLoading) "로딩중..."
                            else if (myRanking > 0) "내 등수\n#$myRanking"
                            else "내 등수\n순위 없음",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF343434),
                            lineHeight = 15.sp,
                            textAlign = TextAlign.Center
                        )
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(36.dp)
                                .background(Color(0xFF0C092A).copy(alpha = 0.3f))
                        )
                        Text(
                            text = if (isLoading) "로딩중..."
                            else if (schoolRanking > 0) "학교 등수\n#$schoolRanking"
                            else "학교 등수\n순위 없음",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 15.sp,
                            color = Color(0xFF343434),
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // 퀴즈 박스 (실제 데이터 연동)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .background(Color(0xFFBCE4EC), RoundedCornerShape(16.dp))
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            buildAnnotatedString {
                                append("지금까지 총 ")
                                withStyle(style = SpanStyle(color = Color(0xFF259CB2))) {
                                    append(if (isLoading) "로딩중" else "${solvedQuestions}문제")
                                }
                                append("를 풀었어요!")
                            },
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(15.dp))

                        val totalQuestions = 400 // 80 * 5 챕터
                        val progress = if (totalQuestions > 0) solvedQuestions.toFloat() / totalQuestions else 0f

                        Box(
                            modifier = Modifier.size(140.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            // 배경 원
                            CircularProgressIndicator(
                                progress = { 1f },
                                modifier = Modifier.fillMaxSize(),
                                color = Color.White,
                                strokeWidth = 10.dp
                            )
                            // 진행도 원
                            CircularProgressIndicator(
                                progress = { progress },
                                modifier = Modifier.fillMaxSize(),
                                color = Color(0xFF28B6CC),
                                strokeWidth = 10.dp
                            )
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(
                                        text = if (isLoading) "로딩" else "$solvedQuestions",
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0A0A32)
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(
                                        text = "/$totalQuestions",
                                        fontSize = 16.sp,
                                        color = Color(0x8028B6CC)
                                    )
                                }
                                Text("quiz played", fontSize = 12.sp, color = Color.Gray)
                            }
                        }
                    }
                }
            }
        }

        // ✅ 2. 프로필 이미지와 이름 (Firebase에서 가져온 이름)
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = 60.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                contentAlignment = Alignment.BottomEnd,
                modifier = Modifier.size(100.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFD69ACC))
                )

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .offset(x = (-6).dp, y = (-6).dp)
                        .border(2.dp, Color.Gray, CircleShape)
                        .background(Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ){
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Profile",
                        modifier = Modifier.size(18.dp),
                        tint = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = userName,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // ✅ 3. 설정 아이콘 (맨 위 고정)
        Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = "Settings",
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 50.dp, end = 20.dp)
                .size(28.dp)
                .clickable {
                    Log.d("Mypage", "설정 버튼 클릭, setting으로 이동")
                    navController.navigate("setting")
                },
            tint = Color.Gray
        )
    }
}

// 🆕 학교 내 순위 계산 함수 (Mypage용)
fun calculateMypageSchoolRanking(
    db: FirebaseFirestore,
    schoolName: String,
    userScore: Int,
    onResult: (Int) -> Unit
) {
    Log.d("Mypage", "학교 랭킹 계산 시작 - 학교: $schoolName, 점수: $userScore")

    db.collection("users")
        .whereEqualTo("schoolName", schoolName)
        .whereGreaterThan("score", userScore)
        .get()
        .addOnSuccessListener { documents ->
            val higherScoreCount = documents.size()
            val ranking = higherScoreCount + 1
            Log.d("Mypage", "학교 랭킹 계산 완료 - 더 높은 점수 사용자: $higherScoreCount 명, 내 순위: $ranking")
            onResult(ranking)
        }
        .addOnFailureListener { e ->
            Log.e("Mypage", "학교 랭킹 계산 실패", e)
            onResult(0) // 실패 시 0 반환
        }
}