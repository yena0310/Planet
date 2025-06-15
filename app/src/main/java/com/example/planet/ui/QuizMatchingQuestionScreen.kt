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
import com.example.planet.QuizType
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
    val context = LocalContext.current

    // Firebase
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    val db = FirebaseFirestore.getInstance()

    // 사용자 정보 상태
    var userScore by remember { mutableStateOf(0) }
    var totalQuestions by remember { mutableStateOf(80) }
    var isLoading by remember { mutableStateOf(true) }

    // 🆕 현재 문제 세트 (4개씩) - 수정된 부분
    val currentQuizSet = remember {
        val matchingQuizzes = quizList.filter { it.type == QuizType.MATCHING }

        // 매칭 퀴즈 중에서 현재 인덱스에 해당하는 4개 선택
        val matchingIndex = quizList.take(index + 1).count { it.type == QuizType.MATCHING } - 1
        val startIndex = (matchingIndex / 4) * 4
        val endIndex = minOf(startIndex + 4, matchingQuizzes.size)

        Log.d("QuizMatching", "전체 매칭 퀴즈 수: ${matchingQuizzes.size}")
        Log.d("QuizMatching", "현재 매칭 인덱스: $matchingIndex")
        Log.d("QuizMatching", "선택된 범위: $startIndex ~ $endIndex")

        if (endIndex > startIndex) {
            matchingQuizzes.subList(startIndex, endIndex)
        } else {
            emptyList()
        }
    }

    // 🆕 매칭 관련 상태 - 수정된 부분
    val questions = remember {
        currentQuizSet.map { it.question } // 4개 퀴즈의 질문들
    }
    val answers = remember {
        currentQuizSet.map { it.correctAnswer }.shuffled() // 4개 퀴즈의 답안들을 섞음
    }

    // 🆕 정답 매칭 정보 저장
    val correctPairs = remember {
        currentQuizSet.associate { it.question to it.correctAnswer }
    }

    var selectedQuestion by remember { mutableStateOf<String?>(null) }
    val matchedPairs = remember { mutableStateMapOf<String, String>() } // question -> answer
    val questionDotCoords = remember { mutableMapOf<String, Offset>() }
    val answerDotCoords = remember { mutableMapOf<String, Offset>() }
    val matchedLines = remember { mutableStateListOf<Pair<Offset, Offset>>() }
    val rootCoords = remember { mutableStateOf<LayoutCoordinates?>(null) }

    // 선 업데이트 함수
    fun updateLines() {
        matchedLines.clear()
        matchedPairs.forEach { (question, answer) ->
            val startRaw = questionDotCoords[question]
            val endRaw = answerDotCoords[answer]
            val start = startRaw?.copy(x = startRaw.x - 45.5f)
            val end = endRaw?.copy(x = endRaw.x + 14.5f)
            if (start != null && end != null) {
                matchedLines.add(Pair(start, end))
            }
        }
    }

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

    // 🆕 매칭 완료 체크 (4개 모두 매칭되면 해설 창으로) - 상세 로그 추가
    LaunchedEffect(matchedPairs.size) {
        if (matchedPairs.size == questions.size) { // 4개가 됨
            Log.d("QuizMatching", "=====================================")
            Log.d("QuizMatching", "🎯 모든 매칭 완료! 결과 분석 시작")
            Log.d("QuizMatching", "=====================================")

            // 🆕 상세한 매칭 결과 분석
            var correctCount = 0
            var totalQuestions = questions.size

            Log.d("QuizMatching", "📋 전체 정답 정보:")
            correctPairs.forEach { (question, correctAnswer) ->
                Log.d("QuizMatching", "   Q: $question")
                Log.d("QuizMatching", "   A: $correctAnswer")
                Log.d("QuizMatching", "   ---")
            }

            Log.d("QuizMatching", "")
            Log.d("QuizMatching", "👤 사용자 매칭 결과:")

            matchedPairs.forEach { (userQuestion, userAnswer) ->
                val correctAnswer = correctPairs[userQuestion]
                val isCorrect = userAnswer == correctAnswer

                if (isCorrect) {
                    correctCount++
                    Log.d("QuizMatching", "✅ 정답!")
                } else {
                    Log.d("QuizMatching", "❌ 오답!")
                }

                Log.d("QuizMatching", "   질문: $userQuestion")
                Log.d("QuizMatching", "   사용자 선택: $userAnswer")
                Log.d("QuizMatching", "   정답: $correctAnswer")
                Log.d("QuizMatching", "   결과: ${if (isCorrect) "맞음" else "틀림"}")
                Log.d("QuizMatching", "   ---")
            }

            Log.d("QuizMatching", "")
            Log.d("QuizMatching", "📊 최종 결과:")
            Log.d("QuizMatching", "   총 문제 수: $totalQuestions")
            Log.d("QuizMatching", "   정답 개수: $correctCount")
            Log.d("QuizMatching", "   오답 개수: ${totalQuestions - correctCount}")
            Log.d("QuizMatching", "   정답률: ${(correctCount * 100) / totalQuestions}%")

            // 🆕 매칭이 제대로 되었는지 검증
            if (matchedPairs.size == correctPairs.size) {
                Log.d("QuizMatching", "✅ 매칭 개수 검증: 통과 (${matchedPairs.size}/${correctPairs.size})")
            } else {
                Log.e("QuizMatching", "❌ 매칭 개수 오류: ${matchedPairs.size}/${correctPairs.size}")
            }

            // 🆕 중복 매칭 검사
            val uniqueQuestions = matchedPairs.keys.toSet()
            val uniqueAnswers = matchedPairs.values.toSet()

            if (uniqueQuestions.size == matchedPairs.size && uniqueAnswers.size == matchedPairs.size) {
                Log.d("QuizMatching", "✅ 중복 검사: 통과 (질문 ${uniqueQuestions.size}개, 답안 ${uniqueAnswers.size}개)")
            } else {
                Log.e("QuizMatching", "❌ 중복 매칭 발견!")
                Log.e("QuizMatching", "   질문 중복: ${matchedPairs.size - uniqueQuestions.size}개")
                Log.e("QuizMatching", "   답안 중복: ${matchedPairs.size - uniqueAnswers.size}개")
            }

            // 🆕 성과 분석
            when (correctCount) {
                totalQuestions -> Log.d("QuizMatching", "🏆 완벽! 모든 문제를 맞혔습니다!")
                in (totalQuestions * 0.8).toInt()..totalQuestions -> Log.d("QuizMatching", "🎉 우수! 대부분의 문제를 맞혔습니다!")
                in (totalQuestions * 0.5).toInt() until (totalQuestions * 0.8).toInt() -> Log.d("QuizMatching", "👍 보통! 절반 이상 맞혔습니다!")
                else -> Log.d("QuizMatching", "💪 분발! 더 열심히 공부해보세요!")
            }

            Log.d("QuizMatching", "=====================================")
            Log.d("QuizMatching", "🚀 다음 화면으로 이동 준비 중...")
            Log.d("QuizMatching", "=====================================")

            delay(1000)
            // 🆕 매칭 결과를 URL 파라미터로 전달
            val matchingResults = matchedPairs.entries.joinToString(",") { (q, a) ->
                "${q}|||${a}" // |||로 구분 (쉼표나 콜론이 문제 내용에 있을 수 있어서)
            }
            val encodedResults = java.net.URLEncoder.encode(matchingResults, "UTF-8")

            Log.d("QuizMatching", "🚀 매칭 결과 전달: $matchingResults")
            navController.navigate("quiz_matching_answer/${index}?results=${encodedResults}")
        }
    }

    // 좌표 변경 시 선 업데이트
    LaunchedEffect(questionDotCoords.size, answerDotCoords.size) {
        updateLines()
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
                                        .background(
                                            if (selectedQuestion == question) Color(0xFFBBDEFB)
                                            else Color(0xFFE0F7FA)
                                        )
                                        .clickable {
                                            when {
                                                // 🆕 이미 매칭된 질문을 클릭한 경우 → 매칭 취소
                                                matchedPairs.containsKey(question) -> {
                                                    Log.d("QuizMatching", "🔄 매칭 취소: $question")
                                                    matchedPairs.remove(question)
                                                    selectedQuestion = null
                                                    updateLines()
                                                }
                                                // 🆕 현재 선택된 질문을 다시 클릭한 경우 → 선택 해제
                                                selectedQuestion == question -> {
                                                    Log.d("QuizMatching", "❌ 질문 선택 해제: $question")
                                                    selectedQuestion = null
                                                }
                                                // 🆕 새로운 질문 선택
                                                else -> {
                                                    Log.d("QuizMatching", "👆 질문 선택: $question")
                                                    selectedQuestion = question
                                                }
                                            }
                                        }
                                        .onGloballyPositioned { coords ->
                                            rootCoords.value?.let { root ->
                                                val rightCenter = coords.positionInWindow() + Offset(coords.size.width.toFloat(), coords.size.height / 2f)
                                                val relative = rightCenter - root.positionInWindow()
                                                questionDotCoords[question] = relative
                                                updateLines()
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
                                val isMatched = matchedPairs.values.contains(answer)

                                Box(
                                    modifier = Modifier
                                        .width(150.dp)
                                        .height(50.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFFF1F8E9))
                                        .clickable {
                                            val selected = selectedQuestion

                                            when {
                                                // 🆕 이미 매칭된 답안을 클릭한 경우 → 해당 매칭 취소
                                                isMatched -> {
                                                    val questionToRemove = matchedPairs.entries.find { it.value == answer }?.key
                                                    questionToRemove?.let { question ->
                                                        Log.d("QuizMatching", "🔄 답안에서 매칭 취소: $question -> $answer")
                                                        matchedPairs.remove(question)
                                                        selectedQuestion = null
                                                        updateLines()
                                                    }
                                                }
                                                // 🆕 질문이 선택된 상태에서 답안 클릭 → 새로운 매칭 생성
                                                selected != null -> {
                                                    // 🆕 이미 해당 질문이 다른 답안과 매칭되어 있다면 기존 매칭 제거
                                                    if (matchedPairs.containsKey(selected)) {
                                                        val oldAnswer = matchedPairs[selected]
                                                        Log.d("QuizMatching", "🔄 기존 매칭 제거: $selected -> $oldAnswer")
                                                        matchedPairs.remove(selected)
                                                    }

                                                    // 🆕 새로운 매칭 생성
                                                    matchedPairs[selected] = answer
                                                    selectedQuestion = null
                                                    updateLines()
                                                    Log.d("QuizMatching", "✅ 새로운 매칭 생성: $selected -> $answer")

                                                    // 🆕 현재 매칭 상태 로그
                                                    Log.d("QuizMatching", "📊 현재 매칭 상태: ${matchedPairs.size}/${questions.size}")
                                                    matchedPairs.forEach { (q, a) ->
                                                        Log.d("QuizMatching", "   $q -> $a")
                                                    }
                                                }
                                                // 🆕 질문이 선택되지 않은 상태에서 답안 클릭
                                                else -> {
                                                    Log.d("QuizMatching", "⚠️ 먼저 질문을 선택해주세요!")
                                                }
                                            }
                                        }
                                        .onGloballyPositioned { coords ->
                                            rootCoords.value?.let { root ->
                                                val leftCenter = coords.positionInWindow() + Offset(0f, coords.size.height / 2f)
                                                val relative = leftCenter - root.positionInWindow()
                                                answerDotCoords[answer] = relative
                                                updateLines()
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