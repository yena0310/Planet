package com.example.planet.ui

import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.planet.QuizItem
import com.example.planet.R
import com.example.planet.utils.RankingUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun QuizMultipleChoiceQuestionScreen(
    navController: NavHostController,
    quizList: List<QuizItem>,
    index: Int
) {
    val quiz = quizList[index]
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

    // 사용자 정보 및 lastQuestionIndex 업데이트
    LaunchedEffect(Unit) {
        Log.d("QuizMultipleChoice", "4지선다 문제 화면 초기화 - 인덱스: $index")

        currentUser?.let { user ->
            Log.d("QuizMultipleChoice", "사용자 UID: ${user.uid}")

            // 1. 사용자 정보 가져오기
            RankingUtils.getUserQuizInfo(db, user.uid) { score, total ->
                userScore = score
                totalQuestions = total
                isLoading = false
                Log.d("QuizMultipleChoice", "사용자 정보 로드 완료 - 점수: $score")
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
            Log.w("QuizMultipleChoice", "로그인되지 않은 사용자")
            isLoading = false
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
            // 상단
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.TopCenter),
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
                    color = Color.Black,
                    fontFamily = pretendardsemibold
                )

                Text(
                    text = if (isLoading) "로딩..." else "$userScore P",
                    fontSize = 13.sp,
                    color = Color.Gray,
                    fontFamily = pretendardsemibold
                )
            }

            // 문제 텍스트
            Text(
                text = quiz.question,
                fontSize = 24.sp,
                color = Color.Black,
                fontFamily = pretendardsemibold,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(horizontal = 60.dp, vertical = 120.dp),
                textAlign = TextAlign.Center
            )

            // 보기 텍스트
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
            ) {
                Column(
                    modifier = Modifier
                        .padding(start = 60.dp, bottom = 200.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val textStyle = TextStyle(fontSize = 20.sp, fontFamily = pretendardsemibold)
                    quiz.choices?.forEachIndexed { i, choice ->
                        val label = ('A' + i).toString()
                        Text("$label. $choice", style = textStyle)
                    }
                }
            }

            // 선택 버튼 (정사각형 2x2)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
            ) {
                Column(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    val colors = listOf(
                        Color(0xFFFFE28C),
                        Color(0xFF9CD7B5),
                        Color(0xFF9CCDE9),
                        Color(0xFFFFBD88)
                    )

                    quiz.choices?.chunked(2)?.forEachIndexed { rowIndex, pair ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            horizontalArrangement = Arrangement.spacedBy(0.dp)
                        ) {
                            pair.forEachIndexed { colIndex, _ ->
                                val globalIndex = rowIndex * 2 + colIndex
                                val label = ('A' + globalIndex).toString()
                                val color = colors.getOrElse(globalIndex) { Color.Gray }

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .background(color)
                                        .clickable {
                                            Log.d("QuizMultipleChoice", "$label 선택 - 인덱스: $index")
                                            val route = "quiz_answer/$index?userAnswer=$label"
                                            navController.navigate(route)
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        label,
                                        fontSize = 80.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
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