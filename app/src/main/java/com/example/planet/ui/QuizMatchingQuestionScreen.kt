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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
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
import com.example.planet.utils.UserStateManager
import com.google.firebase.firestore.BuildConfig
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// 상수 정의
private object QuizConstants {
    val QUIZ_CONTAINER_HEIGHT = 800.dp
    val QUESTION_BOX_WIDTH = 140.dp
    val ANSWER_BOX_WIDTH = 150.dp
    val BOX_HEIGHT = 50.dp
    val SPACING = 20.dp
    val CORNER_RADIUS = 12.dp
    val DOT_SIZE = 6.dp
    val LINE_STROKE_WIDTH = 4f
    val LINE_OFFSET_X = 20f

    const val QUESTIONS_PER_ROUND = 4
    const val NAVIGATION_DELAY = 500L
    const val URL_SEPARATOR = "|||"
}

// UI 상태 데이터 클래스
data class QuizUiState(
    val userScore: Int = 0,
    val totalQuestions: Int = 100,
    val isLoading: Boolean = true,
    val selectedQuestion: String? = null,
    val matchedPairs: Map<String, String> = emptyMap(),
    val hasError: Boolean = false,
    val errorMessage: String = ""
)

@Composable
fun QuizMatchingQuestionScreen(
    navController: NavHostController,
    quizList: List<QuizItem>,
    index: Int
) {
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()
    val currentUserId = UserStateManager.getUserId()

    // UI 상태 관리
    var uiState by remember { mutableStateOf(QuizUiState()) }

    // 퀴즈 데이터 준비
    val currentQuizSet = remember(index) {
        prepareQuizSet(quizList, index)
    }

    val questions = remember { currentQuizSet.map { it.question } }
    val answers = remember { currentQuizSet.map { it.correctAnswer }.shuffled() }
    val correctPairs = remember { currentQuizSet.associate { it.question to it.correctAnswer } }

    // 매칭 상태
    val matchedPairs = remember { mutableStateMapOf<String, String>() }
    val questionDotCoords = remember { mutableMapOf<String, Offset>() }
    val answerDotCoords = remember { mutableMapOf<String, Offset>() }
    val matchedLines = remember { mutableStateListOf<Pair<Offset, Offset>>() }
    val rootCoords = remember { mutableStateOf<LayoutCoordinates?>(null) }

    // 초기화
    LaunchedEffect(Unit) {
        initializeQuiz(
            context = context,
            db = db,
            currentUserId = currentUserId,
            index = index,
            onStateUpdate = { newState -> uiState = newState }
        )
    }

    // 매칭 완료 체크
    LaunchedEffect(matchedPairs.size) {
        if (matchedPairs.size == questions.size) {
            handleMatchingComplete(
                matchedPairs = matchedPairs.toMap(),
                correctPairs = correctPairs,
                currentQuizSet = currentQuizSet,
                navController = navController,
                index = index
            )
        }
    }

    // 선 업데이트
    LaunchedEffect(questionDotCoords.size, answerDotCoords.size) {
        updateLines(matchedPairs, questionDotCoords, answerDotCoords, matchedLines)
    }

    QuizMatchingContent(
        uiState = uiState,
        questions = questions,
        answers = answers,
        matchedPairs = matchedPairs,
        selectedQuestion = uiState.selectedQuestion,
        matchedLines = matchedLines,
        rootCoords = rootCoords,
        questionDotCoords = questionDotCoords,
        answerDotCoords = answerDotCoords,
        index = index,
        navController = navController,
        onQuestionClick = { question ->
            handleQuestionClick(
                question = question,
                selectedQuestion = uiState.selectedQuestion,
                matchedPairs = matchedPairs,
                onSelectedQuestionChange = { newSelection ->
                    uiState = uiState.copy(selectedQuestion = newSelection)
                },
                onUpdateLines = {
                    updateLines(matchedPairs, questionDotCoords, answerDotCoords, matchedLines)
                }
            )
        },
        onAnswerClick = { answer ->
            handleAnswerClick(
                answer = answer,
                selectedQuestion = uiState.selectedQuestion,
                matchedPairs = matchedPairs,
                onSelectedQuestionChange = { newSelection ->
                    uiState = uiState.copy(selectedQuestion = newSelection)
                },
                onUpdateLines = {
                    updateLines(matchedPairs, questionDotCoords, answerDotCoords, matchedLines)
                }
            )
        }
    )
}

@Composable
private fun QuizMatchingContent(
    uiState: QuizUiState,
    questions: List<String>,
    answers: List<String>,
    matchedPairs: Map<String, String>,
    selectedQuestion: String?,
    matchedLines: List<Pair<Offset, Offset>>,
    rootCoords: MutableState<LayoutCoordinates?>,
    questionDotCoords: MutableMap<String, Offset>,
    answerDotCoords: MutableMap<String, Offset>,
    index: Int,
    navController: NavHostController,
    onQuestionClick: (String) -> Unit,
    onAnswerClick: (String) -> Unit
) {
    val pretendardsemibold = FontFamily(Font(R.font.pretendardsemibold))

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF7AC5D3))
            .onGloballyPositioned { rootCoords.value = it }
    ) {
        // 연결선 그리기
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(1f)
        ) {
            matchedLines.forEach { (start, end) ->
                drawLine(
                    color = Color.Black,
                    start = start.copy(x = start.x + QuizConstants.LINE_OFFSET_X),
                    end = end.copy(x = end.x - QuizConstants.LINE_OFFSET_X),
                    strokeWidth = QuizConstants.LINE_STROKE_WIDTH
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
                .height(QuizConstants.QUIZ_CONTAINER_HEIGHT)
        ) {
            Column {
                QuizHeader(
                    index = index,
                    totalQuestions = uiState.totalQuestions,
                    userScore = uiState.userScore,
                    isLoading = uiState.isLoading,
                    fontFamily = pretendardsemibold,
                    navController = navController
                )

                QuizTitle(fontFamily = pretendardsemibold)

                QuizMatchingArea(
                    questions = questions,
                    answers = answers,
                    matchedPairs = matchedPairs,
                    selectedQuestion = selectedQuestion,
                    questionDotCoords = questionDotCoords,
                    answerDotCoords = answerDotCoords,
                    rootCoords = rootCoords,
                    fontFamily = pretendardsemibold,
                    onQuestionClick = onQuestionClick,
                    onAnswerClick = onAnswerClick,
                    onUpdateLines = {
                        updateLines(matchedPairs, questionDotCoords, answerDotCoords, mutableListOf())
                    }
                )
            }
        }
    }
}

@Composable
private fun QuizHeader(
    index: Int,
    totalQuestions: Int,
    userScore: Int,
    isLoading: Boolean,
    fontFamily: FontFamily,
    navController: NavHostController
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(
            onClick = { navController.navigate("quiz") },
            modifier = Modifier.semantics {
                contentDescription = "퀴즈 목록으로 돌아가기"
                role = Role.Button
            }
        ) {
            Icon(
                imageVector = Icons.Rounded.ArrowBackIosNew,
                modifier = Modifier.size(25.dp),
                tint = Color.Gray,
                contentDescription = null
            )
        }

        Text(
            text = "${index + 1} / $totalQuestions",
            fontSize = 18.sp,
            fontFamily = fontFamily
        )

        Text(
            text = if (isLoading) "로딩..." else "$userScore P",
            fontSize = 13.sp,
            color = Color.Gray,
            fontFamily = fontFamily
        )
    }
}

@Composable
private fun QuizTitle(fontFamily: FontFamily) {
    Text(
        text = "쓰레기와 배출방법을\n올바르게 연결하세요",
        fontSize = 24.sp,
        fontFamily = fontFamily,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp, bottom = 10.dp),
        textAlign = TextAlign.Center
    )
}

@Composable
private fun QuizMatchingArea(
    questions: List<String>,
    answers: List<String>,
    matchedPairs: Map<String, String>,
    selectedQuestion: String?,
    questionDotCoords: MutableMap<String, Offset>,
    answerDotCoords: MutableMap<String, Offset>,
    rootCoords: MutableState<LayoutCoordinates?>,
    fontFamily: FontFamily,
    onQuestionClick: (String) -> Unit,
    onAnswerClick: (String) -> Unit,
    onUpdateLines: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
                .height(700.dp)
        ) {
            QuestionColumn(
                questions = questions,
                matchedPairs = matchedPairs,
                selectedQuestion = selectedQuestion,
                questionDotCoords = questionDotCoords,
                rootCoords = rootCoords,
                fontFamily = fontFamily,
                onQuestionClick = onQuestionClick,
                onUpdateLines = onUpdateLines
            )

            Spacer(modifier = Modifier.width(12.dp))

            AnswerColumn(
                answers = answers,
                matchedPairs = matchedPairs,
                answerDotCoords = answerDotCoords,
                rootCoords = rootCoords,
                fontFamily = fontFamily,
                onAnswerClick = onAnswerClick,
                onUpdateLines = onUpdateLines
            )
        }
    }
}

@Composable
private fun RowScope.QuestionColumn(
    questions: List<String>,
    matchedPairs: Map<String, String>,
    selectedQuestion: String?,
    questionDotCoords: MutableMap<String, Offset>,
    rootCoords: MutableState<LayoutCoordinates?>,
    fontFamily: FontFamily,
    onQuestionClick: (String) -> Unit,
    onUpdateLines: () -> Unit
) {
    Column(
        modifier = Modifier.weight(1f),
        verticalArrangement = Arrangement.spacedBy(QuizConstants.SPACING)
    ) {
        Spacer(modifier = Modifier.height(60.dp))
        questions.forEach { question ->
            QuestionBox(
                question = question,
                isSelected = selectedQuestion == question,
                isMatched = matchedPairs.containsKey(question),
                questionDotCoords = questionDotCoords,
                rootCoords = rootCoords,
                fontFamily = fontFamily,
                onClick = { onQuestionClick(question) },
                onUpdateLines = onUpdateLines
            )
        }
    }
}

@Composable
private fun RowScope.AnswerColumn(
    answers: List<String>,
    matchedPairs: Map<String, String>,
    answerDotCoords: MutableMap<String, Offset>,
    rootCoords: MutableState<LayoutCoordinates?>,
    fontFamily: FontFamily,
    onAnswerClick: (String) -> Unit,
    onUpdateLines: () -> Unit
) {
    Column(
        modifier = Modifier.weight(1f),
        verticalArrangement = Arrangement.spacedBy(QuizConstants.SPACING),
        horizontalAlignment = Alignment.End
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        answers.forEach { answer ->
            AnswerBox(
                answer = answer,
                isMatched = matchedPairs.values.contains(answer),
                answerDotCoords = answerDotCoords,
                rootCoords = rootCoords,
                fontFamily = fontFamily,
                onClick = { onAnswerClick(answer) },
                onUpdateLines = onUpdateLines
            )
        }
    }
}

@Composable
private fun QuestionBox(
    question: String,
    isSelected: Boolean,
    isMatched: Boolean,
    questionDotCoords: MutableMap<String, Offset>,
    rootCoords: MutableState<LayoutCoordinates?>,
    fontFamily: FontFamily,
    onClick: () -> Unit,
    onUpdateLines: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(QuizConstants.QUESTION_BOX_WIDTH)
            .height(QuizConstants.BOX_HEIGHT)
            .clip(RoundedCornerShape(QuizConstants.CORNER_RADIUS))
            .background(
                when {
                    isSelected -> Color(0xFFBBDEFB)
                    isMatched -> Color(0xFFE8F5E8)
                    else -> Color(0xFFE0F7FA)
                }
            )
            .clickable { onClick() }
            .semantics {
                contentDescription = "질문: $question"
                role = Role.Button
            }
            .onGloballyPositioned { coords ->
                rootCoords.value?.let { root ->
                    val rightCenter = coords.positionInWindow() + Offset(
                        coords.size.width.toFloat(),
                        coords.size.height / 2f
                    )
                    val relative = rightCenter - root.positionInWindow()
                    questionDotCoords[question] = relative
                    onUpdateLines()
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = question,
            fontSize = 16.sp,
            fontFamily = fontFamily,
            textAlign = TextAlign.Center
        )
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .offset(x = (-4).dp)
                .size(QuizConstants.DOT_SIZE)
                .clip(CircleShape)
                .background(Color.DarkGray)
        )
    }
}

@Composable
private fun AnswerBox(
    answer: String,
    isMatched: Boolean,
    answerDotCoords: MutableMap<String, Offset>,
    rootCoords: MutableState<LayoutCoordinates?>,
    fontFamily: FontFamily,
    onClick: () -> Unit,
    onUpdateLines: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(QuizConstants.ANSWER_BOX_WIDTH)
            .height(QuizConstants.BOX_HEIGHT)
            .clip(RoundedCornerShape(QuizConstants.CORNER_RADIUS))
            .background(
                if (isMatched) Color(0xFFE8F5E8) else Color(0xFFF1F8E9)
            )
            .clickable { onClick() }
            .semantics {
                contentDescription = "답안: $answer"
                role = Role.Button
            }
            .onGloballyPositioned { coords ->
                rootCoords.value?.let { root ->
                    val leftCenter = coords.positionInWindow() + Offset(
                        0f,
                        coords.size.height / 2f
                    )
                    val relative = leftCenter - root.positionInWindow()
                    answerDotCoords[answer] = relative
                    onUpdateLines()
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = answer,
            fontSize = 16.sp,
            fontFamily = fontFamily,
            textAlign = TextAlign.Center
        )
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(x = 4.dp)
                .size(QuizConstants.DOT_SIZE)
                .clip(CircleShape)
                .background(Color.DarkGray)
        )
    }
}

// 유틸리티 함수들
private fun prepareQuizSet(quizList: List<QuizItem>, index: Int): List<QuizItem> {
    val matchingQuizzes = quizList.filter { it.type == QuizType.MATCHING }
    val matchingRoundCount = quizList.take(index).count { it.type == QuizType.MATCHING }
    val startIndex = matchingRoundCount * QuizConstants.QUESTIONS_PER_ROUND
    val endIndex = minOf(startIndex + QuizConstants.QUESTIONS_PER_ROUND, matchingQuizzes.size)

    return if (startIndex < matchingQuizzes.size) {
        matchingQuizzes.subList(startIndex, endIndex)
    } else {
        emptyList()
    }
}

private suspend fun initializeQuiz(
    context: Context,
    db: FirebaseFirestore,
    currentUserId: String?,
    index: Int,
    onStateUpdate: (QuizUiState) -> Unit
) {
    if (BuildConfig.DEBUG) {
        Log.d("QuizMatching", "매칭 문제 화면 초기화 - 인덱스: $index")
    }

    currentUserId?.let { userId ->
        try {
            RankingUtils.getUserQuizInfo(db, userId) { score, total ->
                onStateUpdate(
                    QuizUiState(
                        userScore = score,
                        totalQuestions = total,
                        isLoading = false
                    )
                )
            }

            val nextQuestionIndex = index + 1
            RankingUtils.updateLastQuestionIndex(db, userId, nextQuestionIndex)

            context.getSharedPreferences("quiz_prefs", Context.MODE_PRIVATE)
                .edit()
                .putInt("last_index", nextQuestionIndex)
                .apply()

        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                Log.e("QuizMatching", "사용자 정보 로드 실패", e)
            }
            onStateUpdate(
                QuizUiState(
                    isLoading = false,
                    hasError = true,
                    errorMessage = "사용자 정보를 불러올 수 없습니다."
                )
            )
        }
    } ?: run {
        if (BuildConfig.DEBUG) {
            Log.w("QuizMatching", "로그인되지 않은 사용자")
        }
        onStateUpdate(QuizUiState(isLoading = false))
    }
}

private fun handleQuestionClick(
    question: String,
    selectedQuestion: String?,
    matchedPairs: MutableMap<String, String>,
    onSelectedQuestionChange: (String?) -> Unit,
    onUpdateLines: () -> Unit
) {
    when {
        matchedPairs.containsKey(question) -> {
            if (BuildConfig.DEBUG) {
                Log.d("QuizMatching", "매칭 취소: $question")
            }
            matchedPairs.remove(question)
            onSelectedQuestionChange(null)
            onUpdateLines()
        }
        selectedQuestion == question -> {
            if (BuildConfig.DEBUG) {
                Log.d("QuizMatching", "질문 선택 해제: $question")
            }
            onSelectedQuestionChange(null)
        }
        else -> {
            if (BuildConfig.DEBUG) {
                Log.d("QuizMatching", "질문 선택: $question")
            }
            onSelectedQuestionChange(question)
        }
    }
}

private fun handleAnswerClick(
    answer: String,
    selectedQuestion: String?,
    matchedPairs: MutableMap<String, String>,
    onSelectedQuestionChange: (String?) -> Unit,
    onUpdateLines: () -> Unit
) {
    val isMatched = matchedPairs.values.contains(answer)

    when {
        isMatched -> {
            val questionToRemove = matchedPairs.entries.find { it.value == answer }?.key
            questionToRemove?.let { question ->
                matchedPairs.remove(question)
                onSelectedQuestionChange(null)
                onUpdateLines()
            }
        }
        selectedQuestion != null -> {
            // 기존 매칭 제거
            val previousQuestionWithSameAnswer = matchedPairs.entries.find { it.value == answer }?.key
            if (previousQuestionWithSameAnswer != null) {
                matchedPairs.remove(previousQuestionWithSameAnswer)
            }

            if (matchedPairs.containsKey(selectedQuestion)) {
                matchedPairs.remove(selectedQuestion)
            }

            // 새로운 매칭 생성
            matchedPairs[selectedQuestion] = answer
            onSelectedQuestionChange(null)
            onUpdateLines()
        }
    }
}

private fun updateLines(
    matchedPairs: Map<String, String>,
    questionDotCoords: Map<String, Offset>,
    answerDotCoords: Map<String, Offset>,
    matchedLines: MutableList<Pair<Offset, Offset>>
) {
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

private suspend fun handleMatchingComplete(
    matchedPairs: Map<String, String>,
    correctPairs: Map<String, String>,
    currentQuizSet: List<QuizItem>,
    navController: NavHostController,
    index: Int
) {
    val correctCount = matchedPairs.count { (question, answer) ->
        correctPairs[question] == answer
    }

    if (BuildConfig.DEBUG) {
        Log.d("QuizMatching", "매칭 완료 - 정답: $correctCount/${matchedPairs.size}")
    }

    delay(QuizConstants.NAVIGATION_DELAY)

    withContext(Dispatchers.Main) {
        val matchingResults = matchedPairs.entries.joinToString(",") { (q, a) ->
            "${q}${QuizConstants.URL_SEPARATOR}${a}"
        }
        val encodedResults = java.net.URLEncoder.encode(matchingResults, "UTF-8")

        val quizIds = currentQuizSet.map { it.id }.joinToString(",")
        val encodedQuizIds = java.net.URLEncoder.encode(quizIds, "UTF-8")

        navController.navigate("quiz_matching_answer/${index}?results=${encodedResults}&quizIds=${encodedQuizIds}")
    }
}