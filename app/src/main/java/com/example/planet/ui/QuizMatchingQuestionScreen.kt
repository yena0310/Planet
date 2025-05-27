package com.example.planet.ui

import android.content.Context
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
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
import com.example.planet.chapter3Quizzes
import kotlinx.coroutines.delay

@Composable//-->매칭형 문제페이지
fun QuizMatchingQuestionScreen(navController: NavHostController, quiz: QuizItem, index: Int) {
    val pretendardsemibold = FontFamily(Font(R.font.pretendardsemibold))
    val quizzes = chapter3Quizzes

    val questions = quizzes.map { it.question }
    val answers = quizzes.map { it.correctAnswer }

    var selectedQuestion by remember { mutableStateOf<String?>(null) }
    val matchedPairs = remember { mutableStateListOf<Pair<String, String>>() }

    val questionDotCoords = remember { mutableMapOf<String, Offset>() }
    val answerDotCoords = remember { mutableMapOf<String, Offset>() }
    val density = LocalDensity.current
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        context.getSharedPreferences("quiz_prefs", Context.MODE_PRIVATE)
            .edit()
            .putInt("last_index", index)
            .apply()
    }
    LaunchedEffect(matchedPairs.size) {
        if (matchedPairs.size == questions.size) {
            delay(1000)
            navController.navigate("quiz_result")
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF7AC5D3))
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
            Column {
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
                        text = "${index + 1} / 20",
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

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 50.dp)
                        .height(700.dp)
                ) {
                    // -------------왼쪽 질문-------------
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Spacer(modifier = Modifier.height(80.dp))
                        questions.forEach { question ->
                            val isSelected = selectedQuestion == question
                            val bgColor by animateColorAsState(
                                if (isSelected) Color(0xFFB3E5FC) else Color(0xFFE0F7FA),
                                animationSpec = tween(300)
                            )

                            Box(
                                modifier = Modifier
                                    .width(110.dp)
                                    .padding(top = 8.dp, bottom = 8.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(bgColor)
                                    .clickable { selectedQuestion = question }
                                    .padding(12.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .onGloballyPositioned { layoutCoordinates ->
                                            val position = layoutCoordinates.positionInRoot()
                                            val center = with(density) {
                                                position + Offset(
                                                    layoutCoordinates.size.width / 2f,
                                                    layoutCoordinates.size.height / 2f
                                                )
                                            }
                                            questionDotCoords[question] = center
                                        })
                                {
                                    Text(
                                        text = question,
                                        fontSize = 16.sp,
                                        fontFamily = pretendardsemibold,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.weight(1f)
                                    )

                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(Color.DarkGray)

                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // ----------오른쪽 답변--------------
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    Spacer(modifier = Modifier.height(10.dp))
                    answers.forEach { answer ->
                        Box(
                            modifier = Modifier
                                .width(140.dp)
                                .width(150.dp)
                                .padding(top = 10.dp, bottom = 4.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFF1F8E9))
                                .clickable {
                                    selectedQuestion?.let { question ->
                                        if (!matchedPairs.any { it.first == question || it.second == answer }) {
                                            matchedPairs.add(question to answer)
                                        }
                                        selectedQuestion = null
                                    }
                                }
                                .padding(8.dp),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(5.dp)
                                        .clip(CircleShape)
                                        .background(Color.DarkGray)
                                        .onGloballyPositioned { layoutCoordinates ->
                                            val position = layoutCoordinates.positionInRoot()
                                            val center = with(density) {
                                                position + Offset(
                                                    layoutCoordinates.size.width / 2f,
                                                    layoutCoordinates.size.height / 2f
                                                )
                                            }
                                            answerDotCoords[answer] = center
                                        })

                                Spacer(modifier = Modifier.width(7.dp))
                                Text(
                                    text = answer,
                                    fontSize = 16.sp,
                                    fontFamily = pretendardsemibold
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(1f)
    ) {
        matchedPairs.forEach { (q, a) ->
            val start = questionDotCoords[q]
            val end = answerDotCoords[a]
            if (start != null && end != null) {
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
