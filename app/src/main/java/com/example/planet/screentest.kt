package com.example.planet.ui

import android.content.Context
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import com.example.planet.QuizItem
import com.example.planet.R
import com.example.planet.chapter3Quizzes
import kotlinx.coroutines.delay
import android.util.Log

@Preview
@Composable
fun QuizMatchingQuestionScreen(/*navController: NavHostController, quiz: QuizItem, index: Int*/) {
    val pretendardsemibold = FontFamily(Font(R.font.pretendardsemibold))
    val quizzes = chapter3Quizzes

    val questions = quizzes.map { it.question }
    val answers = quizzes.map { it.correctAnswer }

    var selectedQuestion by remember { mutableStateOf<String?>(null) }
    val matchedPairs = remember { mutableStateListOf<Pair<String, String>>() }
    val questionDotCoords = remember { mutableMapOf<String, Offset>() }
    val answerDotCoords = remember { mutableMapOf<String, Offset>() }
    val matchedLines = remember { mutableStateListOf<Pair<Offset, Offset>>() }
    val context = LocalContext.current
    val rootCoords = remember { mutableStateOf<LayoutCoordinates?>(null) }

    LaunchedEffect(Unit) {
        context.getSharedPreferences("quiz_prefs", Context.MODE_PRIVATE)
            .edit()
            //.putInt("last_index", index)
            .apply()
    }
    LaunchedEffect(matchedPairs.size) {
        if (matchedPairs.size == questions.size) {
            delay(1000)
            //navController.navigate("quiz_result")
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF7AC5D3))
            .onGloballyPositioned { rootCoords.value = it }
    ) {
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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = {
                        //navController.navigate("quiz")
                    }) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBackIosNew,
                            modifier = Modifier.size(25.dp),
                            tint = Color.Gray,
                            contentDescription = "뒤로 가기"
                        )
                    }
                    Text(
                        text = "${/*index + */1} / 20",
                        fontSize = 18.sp,
                        fontFamily = pretendardsemibold
                    )
                    Text(
                        text = "89 P",
                        fontSize = 13.sp,
                        color = Color.Gray,
                        fontFamily = pretendardsemibold
                    )
                }

                Text(
                    text = "쓰레기와 배출방법을\n올바르게 연결하세요",
                    fontSize = 24.sp,
                    fontFamily = pretendardsemibold,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 20.dp, bottom = 10.dp),
                    textAlign = TextAlign.Center
                )

                Box(modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp)
                            .height(700.dp)
                    ) {
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
                                        .clickable { selectedQuestion = question }
                                        .onGloballyPositioned { coords ->
                                            rootCoords.value?.let { root ->
                                                val center = coords.positionInWindow() + Offset(coords.size.width / 2f, coords.size.height / 2f)
                                                val relative = center - root.positionInWindow()
                                                questionDotCoords[question] = relative

                                                Log.d("QuizDebug", "QuestionDot[$question] = $relative")
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
                                                val start = questionDotCoords[selected]
                                                val end = answerDotCoords[answer]
                                                if (start != null && end != null) {
                                                    matchedPairs.add(Pair(selected, answer))
                                                    matchedLines.add(Pair(start, end))
                                                    selectedQuestion = null
                                                }
                                            }
                                        }
                                        .onGloballyPositioned { coords ->
                                            rootCoords.value?.let { root ->
                                                val center = coords.positionInWindow() + Offset(coords.size.width / 2f, coords.size.height / 2f)
                                                val relative = center - root.positionInWindow()
                                                answerDotCoords[answer] = relative

                                                Log.d("QuizDebug", "AnswerDot[$answer] = $relative")
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

                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .zIndex(1f)
                    ) {
                        matchedLines.forEach { (start, end) ->
                            drawLine(
                                color = Color.Black,
                                start = start,
                                end = end,
                                strokeWidth = 4f
                            )
                        }
                    }
                }
            }
        }
    }
}
