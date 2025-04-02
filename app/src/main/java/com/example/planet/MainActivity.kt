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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Path
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.ui.draw.shadow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.border
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.draw.drawBehind
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.*
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import kotlinx.coroutines.delay
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.draw.clip

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                HomeScreen()
                Quiz1QuestionScreen()
                BottomNavigationBar()
                StudyQuizPage()
                Quiz1AnswerScreen()
                Quiz2QuestionScreen()
                Quiz3QuestionScreen()
            }
        }
    }
}

//@Preview(showBackground = true)
@Composable
fun HomeScreen() {

    val pretendardsemibold = FontFamily(Font(R.font.pretendardsemibold))
    val pretendardbold = FontFamily(Font(R.font.pretendardbold))

    Scaffold(
        bottomBar = {
            BottomNavigationBar()
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFCAEBF1))
                .padding(
                    start = 20.dp,
                    end = 20.dp,
                    top = 70.dp,
                    bottom = innerPadding.calculateBottomPadding()
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
                    style = MaterialTheme.typography.titleLarge,
                    fontSize = 17.sp,
                    fontFamily = pretendardsemibold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ======= 최근 퀴즈 박스 (버튼 + 그림자 + TODO 이동) =======
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF)),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .clickable {
                        // TODO: 페이지 이동 처리 (예: navController.navigate("quizPage"))
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
                        fontFamily = pretendardsemibold,
                        modifier = Modifier.padding(start = 15.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 15.dp, end = 15.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "첫 문제를 풀어보세요! >>",
                            color = Color(0xFF546A6E),
                            fontSize = 16.64.sp,
                            style = MaterialTheme.typography.titleMedium,
                            fontFamily = pretendardbold
                        )
                        Text(
                            text = "0%",
                            style = MaterialTheme.typography.titleMedium,
                            fontFamily = pretendardsemibold
                        )
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
        }
    }
}


//@Preview(showBackground = true)
@Composable//-->메인퀴즈페이지
fun StudyQuizPage() {
    val pretendardsemibold = FontFamily(Font(R.font.pretendardsemibold))
    val pretendardbold = FontFamily(Font(R.font.pretendardbold))

    Scaffold(
        bottomBar = {
            BottomNavigationBar()
        }
    ) { innerPadding ->

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
                    style = MaterialTheme.typography.titleLarge,
                    fontSize = 17.sp,
                    fontFamily = pretendardsemibold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ======= 최근 퀴즈 박스 =======
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(20.dp),
                //elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .customShadow()
                    .clickable {
                        // TODO: navController.navigate("quizPage")
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
                        fontFamily = pretendardsemibold,
                        modifier = Modifier.padding(start = 15.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 15.dp, end = 15.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "이어서 풀기 >>",
                            color = Color(0xFF546A6E),
                            fontSize = 18.sp,
                            style = MaterialTheme.typography.titleMedium,
                            fontFamily = pretendardbold
                        )
                        Text(
                            text = "0%",
                            style = MaterialTheme.typography.titleMedium,
                            fontFamily = pretendardsemibold
                        )
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
                        text = "틀렸던 문제를 다시 풀어볼까요?  >>",
                        color = Color(0xFF546A6E),
                        fontSize = 18.sp,
                        style = MaterialTheme.typography.titleMedium,
                        fontFamily = pretendardbold
                    )
                }}

            Spacer(modifier = Modifier.height(16.dp))

            // ===== 흰색 박스 =====
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
                    )
                    .padding(bottom = innerPadding.calculateBottomPadding())
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

                    listOf(
                        Triple("1", "Chapter 1", "20 문제 | 완료!"),
                        Triple("2", "Chapter 2", "20 문제"),
                        Triple("3", "Chapter 3", "20 문제"),
                        Triple("4", "Chapter 4", "20 문제"),
                        Triple("5", "Chapter 5", "20 문제")
                    ).forEachIndexed { index, (number, title, subtitle) ->

                        val isCompleted = index == 0

                        val backgroundColor = if (isCompleted) Color(0xFF4E4E58) else Color.White
                        val borderColor = if (isCompleted) Color.Transparent else Color(0xFFB9DEE4)

// ✅ 글씨 색상 - Chapter 1만 따로 분기
                        val titleColor = if (isCompleted) Color(0xFFC2EFF7) else Color(0xFF546A6E)
                        val subtitleColor = if (isCompleted) Color(0xFF95D0DB) else Color(0xFF858494)

                        val iconTint = Color(0xFF53AEBE)

                        Button(
                            onClick = { /* TODO: Chapter 이동 */ },
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
                                        .background(Color(0xFF53AEBE), shape = RoundedCornerShape(17.dp)),
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
                                    imageVector = Icons.Default.ArrowForward,
                                    contentDescription = "Next",
                                    tint = iconTint
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                    }
                }
            }

        }
    }}

@Composable//-->하단 네비게이션바
fun BottomNavigationBar(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
            .shadow(
                elevation = 25.dp,
                shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
                clip = false
            )
            .background(Color.Transparent)
    ) {
        // ✅ 네비게이션 바 배경 (상단 라운드)
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
        ) {
            val width = size.width
            val height = size.height
            val cornerRadius = 40.dp.toPx()

            drawPath(
                path = Path().apply {
                    moveTo(0f, cornerRadius)
                    quadraticBezierTo(0f, 0f, cornerRadius, 0f)
                    lineTo(width - cornerRadius, 0f)
                    quadraticBezierTo(width, 0f, width, cornerRadius)
                    lineTo(width, height)
                    lineTo(0f, height)
                    close()
                },
                color = Color.White
            )
        }

        // ✅ 아이콘 버튼들
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 40.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavItem(
                icon = Icons.Default.Home,
                isSelected = true,
                iconSize = 28.dp,
                onClick = {
                    // TODO: navController.navigate("homePage")
                }
            )

            NavItem(
                icon = Icons.Default.School,
                isSelected = false,
                iconSize = 28.dp,
                onClick = {
                    // TODO: navController.navigate("schoolPage")
                }
            )

            Spacer(modifier = Modifier.width(50.dp))

            NavItem(
                icon = Icons.Default.BarChart,
                isSelected = false,
                iconSize = 28.dp,
                onClick = {
                    // TODO: navController.navigate("chartPage")
                }
            )

            NavItem(
                icon = Icons.Default.Person,
                isSelected = false,
                iconSize = 28.dp,
                onClick = {
                    // TODO: navController.navigate("profilePage")
                }
            )
        }

        // ✅ 카메라 버튼
        FloatingActionButton(
            onClick = { /* TODO: 카메라 클릭 처리 */ },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-28).dp)
                .size(68.dp),
            containerColor = Color(0xFF53AEBE),
            shape = CircleShape
        ) {
            Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = "카메라",
                tint = Color.White,
                modifier = Modifier.size(40.dp)
            )
        }
    }
}

@Composable//-->네비게이션바 아이콘들
fun NavItem(
    icon: ImageVector,
    isSelected: Boolean,
    iconSize: Dp = 28.dp,
    onClick: () -> Unit
) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isSelected) Color(0xFF0C092A) else Color.Gray,
            modifier = Modifier.size(iconSize)
        )
    }
}

@Composable//-->그림자
fun Modifier.customShadow(
    shadowColors: List<Color> = listOf(
        Color(0x00CCCCCC),
        Color(0x10CCCCCC),
        Color(0x30CCCCCC),
        Color(0x50000000) // 진한 그림자 마지막
    ),
    cornerRadius: Dp = 20.dp
): Modifier = this.then(
    Modifier.drawBehind {
        val radius = cornerRadius.toPx()
        shadowColors.forEachIndexed { index, color ->
            drawRoundRect(
                color = color,
                topLeft = Offset(2f, (index + 1) * 2f),
                size = size,
                cornerRadius = CornerRadius(radius, radius)
            )
        }
    }
)

//@Preview(showBackground = true)
@Composable//-->퀴즈1 문제페이지
fun Quiz1QuestionScreen() {

    val pretendardsemibold = FontFamily(
        Font(R.font.pretendardsemibold)
    )


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
                fontSize = 24.sp,
                color = Color.Black,
                fontFamily = pretendardsemibold,
                modifier = Modifier
                    .align(Alignment.TopCenter) // 중앙보다 위쪽에 위치
                    .padding(horizontal = 60.dp, vertical = 250.dp), // 여유 공간 조절
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

//@Preview(showBackground = true)
@Composable//-->퀴즈1 해설페이지
fun Quiz1AnswerScreen() {

    val pretendardsemibold = FontFamily(
        Font(R.font.pretendardsemibold)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF7AC5D3)) // 배경색
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

            // 상단: 뒤로가기, 문제 수, 점수
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.TopCenter),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { /* TODO: 뒤로 가기 */ }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "뒤로 가기",
                    )
                }

                Text(
                    text = "1 / 100",
                    fontSize = 18.sp,
                    color = Color.Black,
                    fontFamily = pretendardsemibold
                )

                Text(
                    text = "89 P",
                    fontSize = 16.sp,
                    color = Color.Black,
                    fontFamily = pretendardsemibold
                )
            }

            // 문제 텍스트
            Text(
                text = "종이팩은 일반 종이류와 함께 배출한다.",
                fontSize = 20.sp,
                color = Color.Black,
                fontFamily = pretendardsemibold,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(horizontal = 60.dp, vertical = 100.dp),
                textAlign = TextAlign.Center
            )

            // 다음 문제 버튼 + 아이콘
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopEnd)
                    .padding(top = 200.dp, end = 30.dp), // 오른쪽 여백 추가

                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End // 수평 오른쪽 정렬
            ) {
                Text(
                    text = "다음 문제",
                    fontSize = 16.sp,
                    fontFamily = pretendardsemibold,
                    color = Color(0xFF585858)
                )
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "다음 문제",
                    modifier = Modifier.padding(start = 4.dp),
                    tint = Color(0xFF585858)
                )
            }

            // 라운드 박스 (오답)
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = 70.dp)
                    .fillMaxWidth(0.80f) // 전체 화면의 85% 너비
                    .height(400.dp) // 높이 지정
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFFF9F6F2))
                    .padding(29.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "오답!",
                    fontSize = 22.sp,
                    fontFamily = pretendardsemibold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(18.dp))

                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "오답 아이콘",
                    tint = Color(0xFF4A75E1),
                    modifier = Modifier.size(70.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "종이팩은 종이류가 아닌,전용\n수거함에 배출해야 합니다.",
                    fontSize = 20.sp,
                    fontFamily = pretendardsemibold,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable//-->퀴즈2 문제페이지
fun Quiz2QuestionScreen() {

    val pretendardsemibold = FontFamily(Font(R.font.pretendardsemibold))

    val correctAnswer = "음식물"
    var answer by remember { mutableStateOf("") }
    var isSubmitted by remember { mutableStateOf(false) }
    var isCorrect by remember { mutableStateOf<Boolean?>(null) }

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

            // 상단 정보
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.TopCenter),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { /* TODO */ }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "뒤로 가기",
                    )
                }

                Text(
                    text = "1 / 100",
                    fontSize = 18.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    fontFamily = pretendardsemibold
                )

                Text(
                    text = "89 P",
                    fontSize = 16.sp,
                    color = Color.Black,
                    fontFamily = pretendardsemibold
                )
            }

            // ✅ 문제 텍스트
            Text(
                text = "바나나 껍질은\n○○○ 쓰레기이다.",
                fontSize = 24.sp,
                color = Color.Black,
                fontFamily = pretendardsemibold,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(horizontal = 60.dp, vertical = 180.dp),
                textAlign = TextAlign.Center
            )

            // ✅ 힌트 텍스트 (초성)
            Text(
                text = "힌트: 초성 ㅇㅅㅁ",
                fontSize = 17.sp,
                color = Color.LightGray,
                fontFamily = pretendardsemibold,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 300.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter) // 상단 기준으로 위치 조절 가능
                    .padding(top = 420.dp) // 👈 너가 조절할 수 있는 위치 포인트
                    .padding(horizontal = 24.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            ) {
                OutlinedTextField(
                    value = answer,
                    onValueChange = {
                        answer = it
                        isSubmitted = false
                        isCorrect = null
                    },
                    placeholder = {
                        Text("정답을 입력하세요", fontFamily = pretendardsemibold)
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, shape = RoundedCornerShape(8.dp)),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color(0xFFF4F4F4),
                        focusedContainerColor = Color(0xFFF4F4F4),
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent
                    ),
                    textStyle = LocalTextStyle.current.copy(fontFamily = pretendardsemibold),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            isSubmitted = true
                            isCorrect = answer.trim() == correctAnswer
                        }
                    ),
                    trailingIcon = {
                        Button(
                            onClick = {
                                isSubmitted = true
                                isCorrect = answer.trim() == correctAnswer
                            },
                            enabled = answer.isNotBlank(),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier.padding(end = 4.dp)
                        ) {
                            Text("제출", fontFamily = pretendardsemibold, fontSize = 14.sp)
                        }
                    }
                )
            }


            Spacer(modifier = Modifier.height(16.dp))

                if (isSubmitted && isCorrect != null) {
                    if (isCorrect == true) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "정답",
                                tint = Color(0xFF4CAF50),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "정답입니다!",
                                color = Color(0xFF4CAF50),
                                fontFamily = pretendardsemibold
                            )
                        }
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "오답",
                                tint = Color(0xFFE53935),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "오답입니다. 다시 시도해보세요.",
                                color = Color(0xFFE53935),
                                fontFamily = pretendardsemibold
                            )
                        }
                    }
                }
            }
        }
    }

@Preview(showBackground = true)
@Composable//-->퀴즈3 문제페이지
fun Quiz3QuestionScreen() {
    val pretendardsemibold = FontFamily(Font(R.font.pretendardsemibold))

    val questions = listOf("깨진 유리컵", "종이 영수증", "유리병 뚜껑", "우유 팩")
    val answers = listOf(
        "깨끗이 헹궈서 종이팩 전용 수거함에 배출",
        "신문지 등에 싸서 일반 쓰레기로 배출",
        "병과 분리해서 캔류(금속)로 배출",
        "감염지라서 일반 쓰레기에 배출"
    )
    val correctMap = mapOf(
        "깨진 유리컵" to "신문지 등에 싸서 일반 쓰레기로 배출",
        "종이 영수증" to "감염지라서 일반 쓰레기에 배출",
        "유리병 뚜껑" to "병과 분리해서 캔류(금속)로 배출",
        "우유 팩" to "깨끗이 헹궈서 종이팩 전용 수거함에 배출"
    )

    var selectedQuestion by remember { mutableStateOf<String?>(null) }
    val matchedPairs = remember { mutableStateListOf<Pair<String, String>>() }
    val wrongPairs = remember { mutableStateListOf<Pair<String, String>>() }

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
            // 상단 정보
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.TopCenter),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { /* TODO: 뒤로 가기 */ }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "뒤로 가기"
                    )
                }

                Text(
                    text = "1 / 100",
                    fontSize = 18.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    fontFamily = pretendardsemibold
                )

                Text(
                    text = "89 P",
                    fontSize = 16.sp,
                    color = Color.Black,
                    fontFamily = pretendardsemibold
                )
            }

            // 문제 텍스트
            Text(
                text = "쓰레기와 배출방법을\n올바르게 연결하세요",
                fontSize = 24.sp,
                color = Color.Black,
                fontFamily = pretendardsemibold,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(horizontal = 60.dp, vertical = 120.dp),
                textAlign = TextAlign.Center
            )

            // 매칭 문제 본문
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 180.dp, bottom = 40.dp, start = 16.dp, end = 16.dp)
                    .align(Alignment.Center)
            ) {
                // 문제 항목
                Column(Modifier.weight(1f)) {
                    questions.forEach { question ->
                        val isSelected = selectedQuestion == question
                        val isWrong = wrongPairs.any { it.first == question }
                        val bgColor by animateColorAsState(
                            if (isWrong) Color.Red else if (isSelected) Color(0xFFB3E5FC) else Color(0xFFE0F7FA),
                            animationSpec = tween(durationMillis = 300)
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(bgColor)
                                .clickable { selectedQuestion = question }
                                .padding(16.dp)
                        ) {
                            Text(question, fontSize = 16.sp, fontFamily = pretendardsemibold)
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // 답변 항목
                Column(Modifier.weight(1f)) {
                    answers.forEach { answer ->
                        val isWrong = wrongPairs.any { it.second == answer }
                        val bgColor by animateColorAsState(
                            if (isWrong) Color.Red else Color(0xFFF1F8E9),
                            animationSpec = tween(durationMillis = 300)
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(bgColor)
                                .clickable {
                                    selectedQuestion?.let { question ->
                                        if (correctMap[question] == answer) {
                                            matchedPairs.add(question to answer)
                                        } else {
                                            wrongPairs.add(question to answer)
                                            // 깜빡 효과 제거됨 나중에 틀리면 빨강색 됐다 되돌아가는 코드 추가하기
                                        }
                                        selectedQuestion = null
                                    }
                                }
                                .padding(16.dp)
                        ) {
                            Text(answer, fontSize = 14.sp, fontFamily = pretendardsemibold)
                        }
                    }
                }
            }
        }
    }
}





