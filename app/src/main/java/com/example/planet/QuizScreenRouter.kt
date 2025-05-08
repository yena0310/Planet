package com.example.planet

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward

@Composable
fun QuizQuestionScreen(navController: NavHostController, index: Int) {
    val quiz = chapter1FullQuizzes.getOrNull(index) ?: return

    when (quiz.type) {
        QuizType.OX -> QuizOXQuestionScreen(navController, quiz, index)
        QuizType.SUBJECTIVE -> QuizSubjectiveQuestionScreen(navController, quiz, index)
        QuizType.MATCHING -> QuizMatchingQuestionScreen(navController, quiz, index)
        QuizType.MULTIPLE_CHOICE -> QuizMultipleChoiceQuestionScreen(navController, quiz, index)
    }
}

@Composable
fun QuizAnswerScreen(navController: NavHostController, index: Int) {
    val quiz = chapter1FullQuizzes.getOrNull(index) ?: return

    QuizAnswerContent(quiz = quiz, onNext = {
        val nextIndex = index + 1
        if (nextIndex < chapter1FullQuizzes.size) {
            navController.navigate("quiz_question/$nextIndex") {
                popUpTo("quiz_question/$index") { inclusive = true }
            }
        } else {
            navController.navigate("quiz") {
                popUpTo("quiz_question/$index") { inclusive = true }
            }
        }
    })
}

@Composable
fun QuizAnswerContent(quiz: QuizItem, onNext: () -> Unit) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "해설",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = quiz.explanation ?: "정답 해설이 없습니다.",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Row(modifier = Modifier
            .clickable { onNext() }
            .padding(top = 16.dp)) {
            Text("다음 문제", style = MaterialTheme.typography.bodyLarge)
            Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "Next")
        }
    }
}

