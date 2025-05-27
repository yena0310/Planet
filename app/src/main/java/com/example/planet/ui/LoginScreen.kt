package com.example.planet.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.planet.R
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
fun LoginScreen(navController: NavHostController) {
    val pretendardsemibold = FontFamily(Font(R.font.pretendardsemibold))
    val pretendardextrabold = FontFamily(Font(R.font.pretendardsemibold))

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val auth = Firebase.auth
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFCAEBF1))
            .padding(
                start = 40.dp,
                end = 20.dp,
                top = 70.dp
            )
    ) {
        Text(
            text = "우리의 지구를 위해",
            fontSize = 15.sp,
            fontFamily = pretendardsemibold
        )
        Text(
            text = "Planet",
            fontSize = 48.sp,
            fontFamily = pretendardextrabold
        )

        Spacer(modifier = Modifier.height(40.dp))

        // 이메일 입력 필드
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("이메일", fontFamily = pretendardsemibold) },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = Color(0xFF18BEDD),
                unfocusedIndicatorColor = Color.Gray
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 비밀번호 입력 필드
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("비밀번호", fontFamily = pretendardsemibold) },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = Color(0xFF18BEDD),
                unfocusedIndicatorColor = Color.Gray
            )
        )

        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = Color.Red,
                fontSize = 14.sp,
                fontFamily = pretendardsemibold,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 로그인 버튼
        Button(
            onClick = {
                if (email.isBlank() || password.isBlank()) {
                    errorMessage = "이메일과 비밀번호를 모두 입력해주세요"
                    return@Button
                }

                isLoading = true
                errorMessage = ""

                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        isLoading = false
                        if (task.isSuccessful) {
                            navController.navigate("home") {
                                popUpTo("login") { inclusive = true }
                            }
                        } else {
                            errorMessage = "로그인에 실패했습니다. 다시 시도해주세요."
                        }
                    }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF18BEDD)
            ),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text(
                    "로그인",
                    fontSize = 16.sp,
                    fontFamily = pretendardsemibold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 회원가입 버튼
        TextButton(
            onClick = { /* TODO: 회원가입 화면으로 이동 */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                "계정이 없으신가요? 회원가입",
                fontSize = 14.sp,
                fontFamily = pretendardsemibold,
                color = Color(0xFF18BEDD)
            )
        }
    }
}
