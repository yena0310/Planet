package com.example.planet.ui

import android.content.Context
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import com.example.planet.QuizItem
import com.example.planet.R
import com.example.planet.utils.RankingUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay

@Composable
fun QuizMatchingQuestionScreen(
    navController: NavHostController,
    quizList: List<QuizItem>,
    index: Int
) {
    val pretendardsemibold = FontFamily(Font(R.font.pretendardsemibold))
    val quiz = quizList[index]
    val context = LocalContext.current

    // Firebase
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    val db = FirebaseFirestore.getInstance()

    // 사용자 정보 상태
    var userScore by remember { mutableStateOf(0) }
    var totalQuestions by remember { mutableStateOf(400) }
    var isLoading by remember { mutableStateOf(true) }

    // 매칭 관련 상태
    val questions = quizList.map { it.question }
    val answers = quizList.map { it.correctAnswer }
    var selectedQuestion by remember { mutableStateOf<String?>(null) }
    val matchedPairs = remember { mutableStateListOf<Pair<String, String>>() }
    val questionDotCoords = remember { mutableMapOf<String, Offset>() }
    val answerDotCoords = remember { mutableMapOf<String, Offset>() }
    val matchedLines = remember { mutableStateListOf<Pair<Offset, Offset>>() }
    val rootCoords = remember { mutableStateOf<LayoutCoordinates?>(null) }

    // 사용자 정보 및 lastQuestionIndex 업데이트
    LaunchedEffect(Unit) {
        Log.d("QuizMatching", "매칭 문제 화면 초기화 - 인덱스: $index")

        currentUser?.let { user ->
            Log.d("QuizMatching", "사용자 UID: ${user.uid}")

            // 1. 사용자 정보 가져오기
            RankingUtils.getUserQuizInfo(db, user.uid) { score, total ->
                userScore = score
                totalQuestions = total
                isLoading = false
                Log.d("QuizMatching", "사용자 정보 로드 완료 - 점수: $score")
            }

            // 2. lastQuestionIndex 업데이트 (현재 문제 + 1)
            val nextQuestionIndex = index + 1
            RankingUtils.updateLastQuestionIndex(db, user.uid, nextQuestionIndex)

            // 3. SharedPreferences 업데이트 (기존 방식 유지)
            context.getSharedPreferences("quiz_prefs", Context.MODE_PRIVATE)
                .edit()
                .putInt("last_index", nextQuestionIndex)
                .apply()
        } ?: run {
            Log.w("QuizMatching", "로그인되지 않은 사용자")
            isLoading = false
        }
    }

    // 매칭 완료 체크
    LaunchedEffect(matchedPairs.size) {
        if (matchedPairs.size == questions.size) {
            Log.d("QuizMatching", "모든 매칭 완료")
            delay(1000)
            navController.navigate("quiz_result")
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF7AC5D3))
            .onGloballyPositioned { rootCoords.value = it }
    ) {
        val lineOffsetX = 20f

        // 선을 제일 위에 그리기 위한 Canvas
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(1f)
        ) {
            matchedLines.forEach { (start, end) ->
                drawLine(
                    color = Color.Black,
                    start = start.copy(x = start.x + lineOffsetX),
                    end = end.copy(x = end.x - lineOffsetX),
                    strokeWidth = 4f
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .align(Alignment.BottomCenter)
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .background(Color.White)
                .height(800.dp)
        ) {
            Column {
                // 상단 바
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = {
                        navController.navigate("quiz")
                    }) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBackIosNew,
                            modifier = Modifier.size(25.dp),
                            tint = Color.Gray,
                            contentDescription = "뒤로 가기"
                        )
                    }

                    Text(
                        text = "${index + 1} / $totalQuestions",
                        fontSize = 18.sp,
                        fontFamily = pretendardsemibold
                    )

                    Text(
                        text = if (isLoading) "로딩..." else "$userScore P",
                        fontSize = 13.sp,
                        color = Color.Gray,
                        fontFamily = pretendardsemibold
                    )
                }

                // 문제 제목
                Text(
                    text = "쓰레기와 배출방법을\n올바르게 연결하세요",
                    fontSize = 24.sp,
                    fontFamily = pretendardsemibold,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 20.dp, bottom = 10.dp),
                    textAlign = TextAlign.Center
                )

                // 매칭 영역
                Box(modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp)
                            .height(700.dp)
                    ) {
                        // 질문 컬럼
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            Spacer(modifier = Modifier.height(60.dp))
                            questions.forEach { question ->
                                Box(
                                    modifier = Modifier
                                        .width(140.dp)
                                        .height(50.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFFE0F7FA))
                                        .clickable {
                                            selectedQuestion = question
                                            Log.d("QuizMatching", "질문 선택: $question")
                                        }
                                        .onGloballyPositioned { coords ->
                                            rootCoords.value?.let { root ->
                                                val rightCenter = coords.positionInWindow() + Offset(coords.size.width.toFloat(), coords.size.height / 2f)
                                                val relative = rightCenter - root.positionInWindow()
                                                questionDotCoords[question] = relative
                                                Log.d("QuizMatching", "QuestionDot[$question] = $relative")
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = question,
                                        fontSize = 16.sp,
                                        fontFamily = pretendardsemibold,
                                        textAlign = TextAlign.Center
                                    )
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.CenterEnd)
                                            .offset(x = (-4).dp)
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(Color.DarkGray)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // 답안 컬럼
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(20.dp),
                            horizontalAlignment = Alignment.End
                        ) {
                            Spacer(modifier = Modifier.height(40.dp))
                            answers.forEach { answer ->
                                Box(
                                    modifier = Modifier
                                        .width(150.dp)
                                        .height(50.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFFF1F8E9))
                                        .clickable {
                                            val selected = selectedQuestion
                                            if (selected != null && !matchedPairs.any { it.first == selected && it.second == answer }) {
                                                val startRaw = questionDotCoords[selected]
                                                val endRaw = answerDotCoords[answer]
                                                val start = startRaw?.copy(x = startRaw.x - 45.5f)
                                                val end = endRaw?.copy(x = endRaw.x + 14.5f)
                                                if (start != null && end != null) {
                                                    matchedPairs.add(Pair(selected, answer))
                                                    matchedLines.add(Pair(start, end))
                                                    selectedQuestion = null
                                                    Log.d("QuizMatching", "매칭 완료: $selected -> $answer")
                                                }
                                            }
                                        }
                                        .onGloballyPositioned { coords ->
                                            rootCoords.value?.let { root ->
                                                val leftCenter = coords.positionInWindow() + Offset(0f, coords.size.height / 2f)
                                                val relative = leftCenter - root.positionInWindow()
                                                answerDotCoords[answer] = relative
                                                Log.d("QuizMatching", "AnswerDot[$answer] = $relative")
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = answer,
                                        fontSize = 16.sp,
                                        fontFamily = pretendardsemibold,
                                        textAlign = TextAlign.Center
                                    )
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.CenterStart)
                                            .offset(x = (4).dp)
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(Color.DarkGray)
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