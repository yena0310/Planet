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



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                HomeScreen()
                QuizQuestionScreen()
                BottomNavigationBar()
                StudyQuizPage()
            }
        }
    }
}

//@Preview(showBackground = true)
@Composable
fun HomeScreen() {

    val pretendardsemibold = FontFamily(
        Font(R.font.pretendardsemibold)
    )
    val pretendardbold = FontFamily(
        Font(R.font.pretendardbold)
    )

    Scaffold(
        bottomBar = {
            BottomNavigationBar()
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFC2E38E))
                .padding(
                    start = 20.dp,
                    end = 20.dp,
                    top = 70.dp,
                    bottom = innerPadding.calculateBottomPadding() // <- 하단 패딩 보정!
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

            // ======= 최근 퀴즈 박스 =======
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFCCD5)),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
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
                            .padding(start = 15.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "첫 문제를 풀어보세요! >>",
                            color = Color(0xFF660012),
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

            // ======= 순위 박스 =======
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFCCEAFF)),
                shape = RoundedCornerShape(20.dp),
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
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "# 6",
                            fontSize = 15.sp,
                            style = MaterialTheme.typography.titleLarge,
                            fontFamily = pretendardbold
                        )
                    }

                    HorizontalDivider(
                        color = Color.LightGray,
                        modifier = Modifier
                            .height(40.dp)
                            .width(1.dp)
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "학교 점수",
                            fontSize = 13.sp,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "# 14",
                            fontSize = 15.sp,
                            style = MaterialTheme.typography.titleLarge,
                            fontFamily = pretendardbold
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
    val pretendardsemibold = FontFamily(
        Font(R.font.pretendardsemibold)
    )
    val pretendardbold = FontFamily(
        Font(R.font.pretendardbold)
    )

    Scaffold(
        bottomBar = {
            BottomNavigationBar()
        }
    ) { _ ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFC2E38E))
                .padding(0.dp)
                .verticalScroll(rememberScrollState()) // 스크롤 추가
        ) {
            // ====== 상단 아이템 ======
            Spacer(modifier = Modifier.height(70.dp))

            // ===== 출석 헤더 =====
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "89 P",
                    style = MaterialTheme.typography.titleLarge,
                    fontSize = 17.sp,
                    fontFamily = pretendardsemibold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ===== 최근 퀴즈 박스 =====
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFCCD5)),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .height(80.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "RECENT QUIZ",
                        color = Color.Gray,
                        fontSize = 12.06.sp,
                        fontFamily = pretendardsemibold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "이어서 풀기 >>",
                            color = Color(0xFF660012),
                            fontSize = 16.64.sp,
                            style = MaterialTheme.typography.titleMedium,
                            fontFamily = pretendardbold
                        )
                        Text(
                            text = "65%",
                            style = MaterialTheme.typography.titleMedium,
                            fontFamily = pretendardsemibold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ===== 틀린 문제 복습 박스 =====
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFCCEAFF)),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .height(60.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                )
                {
                    Text(
                        text = "틀렸던 문제를 다시 풀어볼까요 ? >>",
                        fontSize = 19.sp,
                        style = MaterialTheme.typography.titleLarge,
                        fontFamily = pretendardbold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ===== Study Quizzes 흰색 박스 =====
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(horizontal = 20.dp)

            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                ) {

                    // ===== 타이틀 =====
                    Text(
                        text = "Study Quizzes",
                        fontSize = 20.sp,
                        style = MaterialTheme.typography.titleLarge,
                        fontFamily = pretendardbold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // ===== Chapter 1 =====
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF3C3A4A)),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "1",
                                fontSize = 20.sp,
                                fontFamily = pretendardbold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "Chapter 1",
                                    fontSize = 16.sp,
                                    fontFamily = pretendardbold,
                                    color = Color.White
                                )
                                Text(
                                    text = "100 문제 | 완료!",
                                    fontSize = 14.sp,
                                    fontFamily = pretendardsemibold,
                                    color = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.weight(1f))
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = "Next",
                                tint = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // ===== Chapter 2 =====
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF4F2FF)),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "2",
                                fontSize = 20.sp,
                                fontFamily = pretendardbold,
                                color = Color(0xFF5C50D2)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "Chapter 2",
                                    fontSize = 16.sp,
                                    fontFamily = pretendardbold,
                                    color = Color.Black
                                )
                                Text(
                                    text = "100 문제",
                                    fontSize = 14.sp,
                                    fontFamily = pretendardsemibold,
                                    color = Color.Gray
                                )
                            }
                            Spacer(modifier = Modifier.weight(1f))
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = "Next",
                                tint = Color.Gray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // ===== Chapter 3 =====
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF4F2FF)),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "3",
                                fontSize = 20.sp,
                                fontFamily = pretendardbold,
                                color = Color(0xFF5C50D2)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "Chapter 3",
                                    fontSize = 16.sp,
                                    fontFamily = pretendardbold,
                                    color = Color.Black
                                )
                                Text(
                                    text = "100 문제",
                                    fontSize = 14.sp,
                                    fontFamily = pretendardsemibold,
                                    color = Color.Gray
                                )
                            }
                            Spacer(modifier = Modifier.weight(1f))
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = "Next",
                                tint = Color.Gray
                            )
                        }
                    }

                } // Column 끝
            } // Card 끝

        } // Column 끝
    } // Scaffold 끝
}




@Composable
fun BottomNavigationBar(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp) // 네비게이션 바 높이
            .background(Color.Transparent) // 배경 투명
    ) {
        // ✅ 네비게이션 바의 배경 (라운드 처리 + 파인 부분)
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
        ) {
            val width = size.width
            val height = size.height
            val radius = 40.dp.toPx() // 곡선 반경
            val cutoutRadius = 60.dp.toPx()      // 가운데 파인 부분 넓이 조절
            val curveDepth = 40.dp.toPx()        // 아래로 파인 깊이

            val cornerRadius = radius // 바깥 모서리 둥글기
            val innerRadius = radius / 2 // 가운데 파인 부분의 모서리 둥글기

            drawPath(
                path = Path().apply {
                    // 왼쪽 상단 둥근 모서리 시작
                    moveTo(0f, cornerRadius)
                    quadraticBezierTo(0f, 0f, cornerRadius, 0f)

                    // ➡️ 직선 부분 (유지!)
                    lineTo(width / 2 - radius - innerRadius, 0f)

                    // 🔵 직선과 곡선이 만나는 지점 부드럽게 (곡선으로 약간의 둥글기 추가)
                    quadraticBezierTo(
                        width / 2 - radius,
                        0f,
                        width / 2 - radius,
                        innerRadius
                    )

                    // 가운데 아래로 파인 부분
                    quadraticBezierTo(
                        width / 2,
                        radius * 2,
                        width / 2 + radius,
                        innerRadius
                    )

                    // 오른쪽 부드럽게 다시 직선으로 (곡선 처리)
                    quadraticBezierTo(
                        width / 2 + radius,
                        0f,
                        width / 2 + radius + innerRadius,
                        0f
                    )

                    // ➡️ 직선 부분 (유지!)
                    lineTo(width - cornerRadius, 0f)

                    // 오른쪽 상단 둥근 모서리
                    quadraticBezierTo(width, 0f, width, cornerRadius)

                    // 하단 경로 닫기
                    lineTo(width, height)
                    lineTo(0f, height)

                    close()
                },
                color = Color.White
            )
        }

        // ✅ 네비게이션 아이콘들
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 40.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavItem(icon = Icons.Default.Home, isSelected = true)
            NavItem(icon = Icons.Default.School, isSelected = false)
            Spacer(modifier = Modifier.width(50.dp)) // 카메라 버튼 공간 확보
            NavItem(icon = Icons.Default.BarChart, isSelected = false)
            NavItem(icon = Icons.Default.Person, isSelected = false)
        }

        // ✅ 카메라 버튼 (플로팅 느낌)
        FloatingActionButton(
            onClick = { /* TODO: 카메라 기능 연결 */ },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-28).dp), // 위로 띄우기
            containerColor = Color(0xFFC2E38E) // 연두색 배경
        ) {
            Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = "카메라",
                tint = Color.White
            )
        }
    }
}

@Composable
fun NavItem(icon: ImageVector, isSelected: Boolean) {
    Icon(
        imageVector = icon,
        contentDescription = null,
        tint = if (isSelected) Color(0xFF000000) else Color(0xFFCCCCCC),
        modifier = Modifier.size(24.dp)
    )
}

//@Preview(showBackground = true)
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

