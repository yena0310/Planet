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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.draw.drawBehind





class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                HomeScreen()
                Quiz1QuestionScreen()
                BottomNavigationBar()
                StudyQuizPage()
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


@Preview(showBackground = true)
@Composable
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

@Composable
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

@Composable
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

@Composable
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


@Preview(showBackground = true)
@Composable
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

