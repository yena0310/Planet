package com.example.planet.ui

import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
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
fun QuizSubjectiveQuestionScreen(
    navController: NavHostController,
    quizList: List<QuizItem>,
    index: Int
) {
    val quiz = quizList[index]
    val pretendardsemibold = FontFamily(Font(R.font.pretendardsemibold))
    var answer by remember { mutableStateOf("") }
    val context = LocalContext.current

    // Firebase
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    val db = FirebaseFirestore.getInstance()

    // 사용자 정보 상태
    var userScore by remember { mutableStateOf(0) }
    var totalQuestions by remember { mutableStateOf(400) }
    var isLoading by remember { mutableStateOf(true) }

    // 사용자 정보 및 lastQuestionIndex 업데이트
    LaunchedEffect(Unit) {
        Log.d("QuizSubjective", "주관식 문제 화면 초기화 - 인덱스: $index")

        currentUser?.let { user ->
            Log.d("QuizSubjective", "사용자 UID: ${user.uid}")

            // 1. 사용자 정보 가져오기
            RankingUtils.getUserQuizInfo(db, user.uid) { score, total ->
                userScore = score
                totalQuestions = total
                isLoading = false
                Log.d("QuizSubjective", "사용자 정보 로드 완료 - 점수: $score")
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
            Log.w("QuizSubjective", "로그인되지 않은 사용자")
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
            // 상단 바
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
                    .padding(horizontal = 60.dp, vertical = 180.dp),
                textAlign = TextAlign.Center
            )

            // 힌트 텍스트
            quiz.hint?.let {
                Text(
                    text = "힌트: 초성 $it",
                    fontSize = 17.sp,
                    color = Color.LightGray,
                    fontFamily = pretendardsemibold,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 300.dp)
                )
            }

            // 입력 필드 + 제출 버튼
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(top = 420.dp)
                    .padding(horizontal = 24.dp)
            ) {
                OutlinedTextField(
                    value = answer,
                    onValueChange = { answer = it },
                    placeholder = { Text("정답을 입력하세요", fontFamily = pretendardsemibold) },
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
                    trailingIcon = {
                        Button(
                            onClick = {
                                if (answer.isNotBlank()) {
                                    Log.d("QuizSubjective", "답안 제출 - 인덱스: $index, 답안: ${answer.trim()}")
                                    navController.navigate("quiz_answer/$index?userAnswer=${answer.trim()}")
                                }
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
        }
    }
}