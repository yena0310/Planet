package com.example.planet

// Android 기본
import android.content.Context
import android.os.Bundle
import android.util.Log

// Activity & CameraX
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview as CameraXPreview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView

// Compose 기본
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.text.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.geometry.*
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView

// Compose Foundation
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.*
import androidx.compose.foundation.lazy.LazyColumn

// Compose Material3
import androidx.compose.material3.*
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween

// Icons
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.automirrored.rounded.*

// 기타
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                HomeScreen()
                BottomNavigationBar()
                StudyQuizPage()
                Quiz1QuestionScreen()
                Quiz1AnswerScreen()
                Quiz2QuestionScreen()
                Quiz3QuestionScreen()
                Quiz4QuestionScreen()
                CameraScreenPreview()
                GuideResultScreen()
                LeaderboardScreen()
                LeaderboardList()
            }
        }
    }
}

//@Preview(showBackground = true)
@Composable
fun HomeScreen() {

    val pretendardsemibold = FontFamily(Font(R.font.pretendardsemibold))
    val pretendardbold = FontFamily(Font(R.font.pretendardbold))

    val iconTint = Color(0xFF546A6E)

    Scaffold(
        bottomBar = { BottomNavigationBar(selectedItem = BottomNavItem.Home) }

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
                        // TODO: 최근 퀴즈 문제로 이동 (없으면 레벨1에 문제1)
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
                    Spacer(modifier = Modifier.height(8.dp))
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
                                onClick = { /* TODO: 폐기물 분류 클릭 이벤트 */ },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE4FBFF))
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "폐기물 분류",
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
                                onClick = { /* TODO: 분리배출 표시 클릭 이벤트 */ },
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
}

//@Preview(showBackground = true)
@Composable//-->메인퀴즈페이지
fun StudyQuizPage() {
    val pretendardsemibold = FontFamily(Font(R.font.pretendardsemibold))
    val pretendardbold = FontFamily(Font(R.font.pretendardbold))

    Scaffold(
        bottomBar = { BottomNavigationBar(selectedItem = BottomNavItem.Quiz) }

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
                        // TODO: 최근 퀴즈 문제로 이동 (없으면 레벨1에 문제1)
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
                    Spacer(modifier = Modifier.height(8.dp))
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
                                text = "이어서 문제를 풀어보세요 !", // TODO: 히스토리 확인해서 최근 문제 또는 첫 문제로 멘트 변경
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
                        text = "틀렸던 문제를 다시 풀어볼까요?",
                        color = Color(0xFF546A6E),
                        fontSize = 18.sp,
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
                        Triple("1", "Chapter 1", "20 문제 | 완료!"), // TODO: 완료, 진행중, 첫시도 디자인 나누기
                        Triple("2", "Chapter 2", "20 문제"),
                        Triple("3", "Chapter 3", "20 문제"),
                        Triple("4", "Chapter 4", "20 문제"),
                        Triple("5", "Chapter 5", "20 문제")
                    ).forEachIndexed { index, (number, title, subtitle) ->

                        val isCompleted = index == 0

                        val backgroundColor = if (isCompleted) Color(0xFF4E4E58) else Color.White
                        val borderColor = if (isCompleted) Color.Transparent else Color(0xFFB9DEE4)

                        // ✅ 글씨 색상 - 완료된 챕터만 분기
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
                                        .background(
                                            Color(0xFF53AEBE),
                                            shape = RoundedCornerShape(17.dp)
                                        ),
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
                                    imageVector = Icons.AutoMirrored.Filled.NavigateNext,
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

enum class BottomNavItem // 네비게이션바 아이템
{ Home, Quiz, Rank, Profile, None }

@Composable//-->하단 네비게이션바
fun BottomNavigationBar(
    modifier: Modifier = Modifier,
    selectedItem: BottomNavItem = BottomNavItem.None
) {
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
        // 배경
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

        // 아이콘 버튼들
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 30.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavItem(
                icon = Icons.Default.Home,
                isSelected = selectedItem == BottomNavItem.Home,
                onClick = { /* TODO: 홈으로 이동 */ }
            )
            NavItem(
                icon = Icons.Default.School,
                isSelected = selectedItem == BottomNavItem.Quiz,
                onClick = { /* TODO: 퀴즈로 이동 */ }
            )

            Spacer(modifier = Modifier.width(50.dp))

            NavItem(
                icon = Icons.Default.BarChart,
                isSelected = selectedItem == BottomNavItem.Rank,
                onClick = { /* TODO: 랭킹으로 이동 */ }
            )
            NavItem(
                icon = Icons.Default.Person,
                isSelected = selectedItem == BottomNavItem.Profile,
                onClick = { /* TODO: 마이페이지로 이동 */ }
            )
        }

        // 카메라 버튼
        FloatingActionButton(
            onClick = { /* TODO: 카메라로 이동 */ },
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
                IconButton(onClick = { /* TODO: 뒤로가기 */ }) {
                    Icon(
                        imageVector = Icons.Rounded.ArrowBackIosNew,
                        modifier = Modifier.size(25.dp),
                        tint = Color.Gray,
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

                Text(
                    text = "89 P",
                    fontSize = 13.sp,
                    color = Color.Gray,
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
                IconButton(onClick = { /* TODO: 뒤로가기 */ }) {
                    Icon(
                        imageVector = Icons.Rounded.ArrowBackIosNew,
                        modifier = Modifier.size(25.dp),
                        tint = Color.Gray,
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
                    fontSize = 13.sp,
                    color = Color.Gray,
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
                    imageVector = Icons.AutoMirrored.Rounded.ArrowForwardIos,
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
                    text = "종이팩은 종이류가 아닌, 전용\n수거함에 배출해야 합니다.",
                    fontSize = 20.sp,
                    fontFamily = pretendardsemibold,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

//@Preview(showBackground = true)
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
                IconButton(onClick = { /* TODO: 뒤로가기 */ }) {
                    Icon(
                        imageVector = Icons.Rounded.ArrowBackIosNew,
                        modifier = Modifier.size(25.dp),
                        tint = Color.Gray,
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
                    fontSize = 13.sp,
                    color = Color.Gray,
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

//@Preview(showBackground = true)
@Composable//-->퀴즈3 문제페이지
fun Quiz3QuestionScreen() {
    val pretendardsemibold = FontFamily(Font(R.font.pretendardsemibold))

    val questions = listOf("깨진 유리컵", "종이 영수증", "유리병 뚜껑", "우유 팩")
    val answers = listOf(
        "깨끗이 헹궈 종이팩\n전용 수거함에 배출",
        "신문지 등에 싸서\n일반 쓰레기로 배출",
        "병과 분리해서\n캔류(금속)로 배출",
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
                IconButton(onClick = { /* TODO: 뒤로가기 */ }) {
                    Icon(
                        imageVector = Icons.Rounded.ArrowBackIosNew,
                        modifier = Modifier.size(25.dp),
                        tint = Color.Gray,
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
                    fontSize = 13.sp,
                    color = Color.Gray,
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
                Column(
                    modifier=Modifier
                        .weight(1f)
                        .padding(top = 24.dp)
                ) {
                    questions.forEach { question ->
                        val isSelected = selectedQuestion == question
                        val isWrong = wrongPairs.any { it.first == question }
                        val bgColor by animateColorAsState(
                            if (isWrong) Color.Red else if (isSelected) Color(0xFFB3E5FC) else Color(0xFFE0F7FA),
                            animationSpec = tween(durationMillis = 300)
                        )

                        Box(
                            modifier = Modifier
                                .width(130.dp)
                                .padding(8.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(bgColor)
                                .clickable { selectedQuestion = question }
                                .padding(16.dp),
                            contentAlignment = Alignment.Center // ✅ 가운데 정렬
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

//@Preview(showBackground = true)
@Composable//-->퀴즈4 문제페이지
fun Quiz4QuestionScreen() {
    val pretendardsemibold = FontFamily(Font(R.font.pretendardsemibold))

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
                IconButton(onClick = { /* TODO: 뒤로가기 */ }) {
                    Icon(
                        imageVector = Icons.Rounded.ArrowBackIosNew,
                        modifier = Modifier.size(25.dp),
                        tint = Color.Gray,
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
                    fontSize = 13.sp,
                    color = Color.Gray,
                    fontFamily = pretendardsemibold
                )
            }
            // 문제 텍스트
            Text(
                text = "다음 중 일반 쓰레기로\n버려야 하는 것은?",
                fontSize = 24.sp,
                color = Color.Black,
                fontFamily = pretendardsemibold,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(horizontal = 60.dp, vertical = 120.dp),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(40.dp))
            // 문제 텍스트 아래에 보기와 선택 버튼 추가
            // 하단에 정사각형 버튼 그리드
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
            ){
            Column(
                modifier = Modifier
                    .padding(start = 60.dp, bottom = 200.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val textStyle = TextStyle(fontSize = 20.sp, fontFamily = pretendardsemibold)
                Text("A. 신문지", style = textStyle)
                Text("B. 음식물이 묻은 종이컵", style = textStyle)
                Text("C. 깨끗한 플라스틱 컵", style = textStyle)
                Text("D. 종이 상자", style = textStyle)
            }}

            // 하단에 정사각형 버튼 그리드
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter) // ✅ 하단 정렬
            ) {
                Column(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(0.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .background(Color(0xFFFFE28C)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("A", fontSize = 80.sp, fontWeight = FontWeight.Bold,color = Color.White)
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .background(Color(0xFF9CD7B5)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("B", fontSize = 80.sp, fontWeight = FontWeight.Bold,color = Color.White)
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(0.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .background(Color(0xFF9CCDE9)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("C", fontSize = 80.sp, fontWeight = FontWeight.Bold,color = Color.White)
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .background(Color(0xFFFFBD88)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("D", fontSize = 80.sp, fontWeight = FontWeight.Bold,color = Color.White)
                        }
                    }
                }
            }

        }
    }
}

@Composable
fun CameraScreenContent(
    selectedTab: String,
    onTabChange: (String) -> Unit,
    onCaptureClick: () -> Unit,
    pretendardbold: FontFamily,
) {
    RequestCameraPermission {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 30.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // 🔙 뒤로가기 버튼 (왼쪽)
                IconButton(onClick = { /* TODO: 뒤로가기 */ }) {
                    Icon(
                        imageVector = Icons.Rounded.ArrowBackIosNew,
                        contentDescription = "뒤로 가기",
                        tint = Color.White,
                        modifier = Modifier.size(25.dp)
                    )
                }

                Spacer(modifier = Modifier.width(20.dp))

                // 🔘 탭 스위치 (오른쪽)
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(Color.DarkGray),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    listOf("폐기물 분리", "분리배출 표시").forEach { tab ->
                        Text(
                            text = tab,
                            fontFamily = pretendardbold,
                            fontSize = 16.sp,
                            color = if (tab == selectedTab) Color.White else Color.LightGray,
                            modifier = Modifier
                                .padding(8.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (tab == selectedTab) Color(0xFF00A6C4) else Color.Transparent)
                                .clickable { onTabChange(tab) }
                                .padding(horizontal = 20.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // 🔳 카메라 프리뷰 영역 (가운데)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(550.dp) // ✅ 원하는 높이 지정
                    .padding(horizontal = 16.dp)
            ) {
                CameraPreviewView(
                    context = LocalContext.current,
                    lifecycleOwner = LocalLifecycleOwner.current,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 📸 하단 촬영 버튼
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 15.dp),
                contentAlignment = Alignment.Center
            ) {
                IconButton (
                onClick = onCaptureClick,
                modifier = Modifier.size(90.dp))// 버튼 크기
                    {
                    Icon(
                        imageVector = Icons.Default.Circle,
                        contentDescription = "촬영",
                        modifier = Modifier.size(75.dp),
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable//-->카메라페이지
fun CameraScreenPreview() {
    val pretendardbold = FontFamily(Font(R.font.pretendardbold))
    var selectedTab by remember { mutableStateOf("폐기물 분리") }

    if (!LocalInspectionMode.current) {
        CameraScreenContent(
            selectedTab = selectedTab,
            onTabChange = { selectedTab = it },
            onCaptureClick = { },
            pretendardbold = pretendardbold
        )
    } else {
        // Preview 전용 대체 UI
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.DarkGray),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("📷 카메라 화면은\n미리보기에 표시되지 않아요", color = Color.White, textAlign = TextAlign.Center)
        }
    }
}

@Composable//-->카메라뷰
fun CameraPreviewView(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    modifier: Modifier = Modifier
) {
    val previewView = remember { PreviewView(context) }
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    if (!LocalInspectionMode.current) {
        AndroidView(
            factory = { previewView },
            modifier = modifier
        )

        LaunchedEffect(Unit) {
            val cameraProvider = cameraProviderFuture.get()
            val preview = CameraXPreview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview
                )
            } catch (e: Exception) {
                Log.e("CameraPreview", "카메라 바인딩 실패", e)
            }
        }
    } else {
        // Preview 모드일 땐 단순 Box로 대체
        Box(
            modifier = modifier
                .background(Color.DarkGray)
                .fillMaxWidth()
                .height(550.dp) // ✅ 원하는 높이 지정
                .padding(horizontal = 16.dp), // ✅ 원하는 높이 지정
            contentAlignment = Alignment.Center
        ) {
            Text("카메라 미리보기", color = Color.White)
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable//-->카메라 권한 요청
fun RequestCameraPermission(content: @Composable () -> Unit) {
    val permissionState = rememberPermissionState(android.Manifest.permission.CAMERA)

    when (permissionState.status) {
        is com.google.accompanist.permissions.PermissionStatus.Granted -> {
            content() // 권한 허용됨 → 콘텐츠 보여주기
        }

        is com.google.accompanist.permissions.PermissionStatus.Denied -> {
            LaunchedEffect(Unit) {
                permissionState.launchPermissionRequest()
            }

            // 거부된 경우 → 안내 메시지
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "카메라 권한이 필요합니다",
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
        }
    }
}


//@Preview(showBackground = true)
@Composable
fun GuideResultScreen() {

    val pretendardsemibold = FontFamily(Font(R.font.pretendardsemibold))

    Scaffold(
        bottomBar = { BottomNavigationBar(selectedItem = BottomNavItem.None) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFF7AC5D3))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(
                        top = 40.dp, // ✅ 상단 여백 명시
                        start = 16.dp,
                        end = 16.dp
                    )
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .background(Color.White)
                    .height(800.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(30.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 🔹 상단 바
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { /* TODO 뒤로가기 */ }) {
                            Icon(
                                imageVector = Icons.Rounded.ArrowBackIosNew,
                                modifier = Modifier.size(25.dp),
                                tint = Color.Gray,
                                contentDescription = "뒤로 가기"
                            )
                        }

                        Text(
                            text = "분리배출 도우미",
                            fontSize = 18.sp,
                            color = Color.Black,
                            fontFamily = pretendardsemibold
                        )

                        IconButton(onClick = { /* TODO 닫기 */ }) {
                            Icon(
                                imageVector = Icons.Rounded.Close,
                                modifier = Modifier.size(28.dp),
                                tint = Color.Gray,
                                contentDescription = "닫기"
                            )
                        }
                    }

                    // 🔹 이미지 박스 (중앙 위치)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.Gray)
                            .fillMaxWidth(0.8f)
                            .aspectRatio(1f)
                    ) {
                        // TODO: 실제 이미지로 교체
                        Image(
                            painter = ColorPainter(Color.LightGray),
                            contentDescription = "촬영 이미지",
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    // 🔹 문제 텍스트
                    Text(
                        text = "내용물을 비우고 이물질을 제거하여\n 비닐류에 배출해주세요!",
                        fontSize = 20.sp,
                        color = Color.Black,
                        fontFamily = pretendardsemibold,
                        textAlign = TextAlign.Center
                    )

                    // 🔹 점수 추가 텍스트
                    Text(
                        text = "+ 10 P",
                        fontSize = 16.sp,
                        color = Color.Gray,
                        fontFamily = pretendardsemibold,
                        textAlign = TextAlign.Center
                    )
                    }
                }
            }
        }
    }

@Preview(showBackground = true)
@Composable
fun LeaderboardScreen() {
    val pretendard = FontFamily(Font(R.font.pretendardsemibold))
    var selectedTab by remember { mutableStateOf("학생별") }

    Scaffold(
        bottomBar = { BottomNavigationBar() }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
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
                                fontSize = 25.sp,
                                fontFamily = pretendard
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = "다른 사용자들보다\n60% 앞서고 있어요!",
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = pretendard,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Start,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Column(modifier = Modifier.fillMaxSize()) {

                    // podium 이미지 + 프로필을 포함한 Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp), // 이미지 + Row를 담을 공간
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
                    Spacer(modifier = Modifier.height(24.dp))
                    LeaderboardList()

                }
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
        verticalArrangement = Arrangement.spacedBy(6.dp)
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
                .padding(horizontal = 12.dp, vertical = 7.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$score QP",
                fontSize = 13.sp,
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
            .height(95.dp)
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

@Composable
fun LeaderboardList() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE0F7FA),shape = RoundedCornerShape(24.dp)) // 하늘색 배경 전체로 적용
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
