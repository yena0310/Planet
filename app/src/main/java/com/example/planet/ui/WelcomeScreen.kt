package com.example.planet.ui

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
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
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.planet.R
import com.example.planet.utils.UserStateManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun WelcomeScreen(navController: NavHostController, userId: String? = null) {
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    val actualUserId = userId ?: currentUser?.uid

    val pretendardsemibold = FontFamily(Font(R.font.pretendardsemibold))
    val pretendardextrabold = FontFamily(Font(R.font.pretendardextrabold))

    var userDisplayInfo by remember { mutableStateOf("정보를 불러오는 중...") }

    LaunchedEffect(actualUserId) {
        Log.d("WelcomeScreen", "화면 시작")
        Log.d("WelcomeScreen", "사용할 UserId: $actualUserId")

        if (actualUserId != null) {
            // UserStateManager에 사용자 설정 (아직 설정되지 않은 경우에만)
            if (UserStateManager.currentUserId == null) {
                UserStateManager.setUser(actualUserId)
            }

            val db = FirebaseFirestore.getInstance()
            val userRef = db.collection("users").document(actualUserId)

            userRef.get()
                .addOnSuccessListener { userDoc ->
                    Log.d("WelcomeScreen", "사용자 문서 존재: ${userDoc.exists()}")
                    if (userDoc.exists()) {
                        Log.d("WelcomeScreen", "사용자 문서 데이터: ${userDoc.data}")

                        val name = userDoc.getString("name") ?: ""
                        Log.d("WelcomeScreen", "사용자 이름: $name")

                        // UserStateManager에도 사용자 정보 반영
                        UserStateManager.setUser(actualUserId)

                        // 직접 저장한 정보 사용 (더 간단하고 안정적)
                        val schoolName = userDoc.getString("schoolName")
                        val grade = userDoc.getLong("grade")?.toInt()
                        val classNum = userDoc.getLong("classNum")?.toInt()

                        if (schoolName != null && grade != null && classNum != null) {
                            userDisplayInfo = "$schoolName ${grade}학년 ${classNum}반\n$name"
                            Log.d("WelcomeScreen", "사용자 정보 설정 완료: $userDisplayInfo")
                        } else {
                            Log.d("WelcomeScreen", "직접 저장된 정보 없음, 참조 방식으로 시도")
                            // 기존 참조 방식 fallback
                            val classRef = userDoc.getDocumentReference("classId")
                            classRef?.get()
                                ?.addOnSuccessListener { classDoc ->
                                    Log.d("WelcomeScreen", "반 문서 존재: ${classDoc.exists()}")
                                    if (classDoc.exists()) {
                                        Log.d("WelcomeScreen", "반 문서 데이터: ${classDoc.data}")

                                        // 반 문서에서 직접 grade, classNum 가져오기
                                        val gradeFromClass = classDoc.getLong("grade")?.toInt()
                                        val classNumFromClass =
                                            classDoc.getLong("number")?.toInt()

                                        Log.d(
                                            "WelcomeScreen",
                                            "반에서 가져온 정보 - grade: $gradeFromClass, classNum: $classNumFromClass"
                                        )

                                        val schoolRef =
                                            classDoc.getDocumentReference("schoolId")
                                        schoolRef?.get()
                                            ?.addOnSuccessListener { schoolDoc ->
                                                Log.d(
                                                    "WelcomeScreen",
                                                    "학교 문서 존재: ${schoolDoc.exists()}"
                                                )
                                                if (schoolDoc.exists()) {
                                                    Log.d(
                                                        "WelcomeScreen",
                                                        "학교 문서 데이터: ${schoolDoc.data}"
                                                    )
                                                    val schoolNameFromRef =
                                                        schoolDoc.getString("name") ?: ""

                                                    if (gradeFromClass != null && classNumFromClass != null) {
                                                        userDisplayInfo =
                                                            "$schoolNameFromRef ${gradeFromClass}학년 ${classNumFromClass}반\n$name"
                                                        Log.d(
                                                            "WelcomeScreen",
                                                            "참조 방식으로 정보 설정 완료: $userDisplayInfo"
                                                        )
                                                    } else {
                                                        userDisplayInfo = "반 정보를 완전히 불러오지 못했습니다"
                                                        Log.w("WelcomeScreen", "반 정보 불완전")
                                                    }
                                                } else {
                                                    userDisplayInfo = "학교 정보를 찾을 수 없습니다"
                                                    Log.w("WelcomeScreen", "학교 문서 없음")
                                                }
                                            }
                                            ?.addOnFailureListener { e ->
                                                userDisplayInfo = "학교 정보를 불러오지 못했습니다"
                                                Log.e("WelcomeScreen", "학교 정보 로드 실패", e)
                                            }
                                    } else {
                                        userDisplayInfo = "반 정보를 찾을 수 없습니다"
                                        Log.w("WelcomeScreen", "반 문서 없음")
                                    }
                                }
                                ?.addOnFailureListener { e ->
                                    userDisplayInfo = "반 정보를 불러오지 못했습니다"
                                    Log.e("WelcomeScreen", "반 정보 로드 실패", e)
                                }
                        }
                    } else {
                        userDisplayInfo = "사용자 정보가 없습니다"
                        Log.w("WelcomeScreen", "사용자 문서 없음")
                    }
                }
                .addOnFailureListener { e ->
                    userDisplayInfo = "사용자 정보를 불러오지 못했습니다"
                    Log.e("WelcomeScreen", "사용자 정보 로드 실패", e)
                }
        } else {
            userDisplayInfo = "로그인이 필요합니다"
            Log.w("WelcomeScreen", "사용자 ID 없음")
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFCAEBF1))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .align(Alignment.BottomCenter)
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .background(Color.White)
                .height(720.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 40.dp, vertical = 50.dp)
            ) {
                Text(
                    text = "환영합니다 !",
                    fontFamily = pretendardextrabold,
                    fontSize = 50.sp,
                    modifier = Modifier.padding(top = 10.dp)
                )

                Text(
                    text = "지금 바로 플래닛을 사용해보세요",
                    fontFamily = pretendardsemibold,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(top = 10.dp, bottom = 50.dp)
                )

                // 동적으로 표시되는 정보
                Text(
                    text = userDisplayInfo,
                    fontFamily = pretendardsemibold,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(top = 10.dp, bottom = 50.dp)
                )

                Spacer(modifier = Modifier.height(300.dp))

                Button(
                    onClick = {
                        Log.d("WelcomeScreen", "확인 버튼 클릭, 홈으로 이동")
                        navController.navigate("home") {
                            popUpTo("welcome") { inclusive = true }
                        }
                    },
                    enabled = true,
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(0xFF18BEDD)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .height(48.dp)
                ) {
                    Text("확인",
                        color = Color(0xFFFFFFFF),
                        fontSize = 16.sp,
                        fontFamily = pretendardsemibold)
                }
            }
        }
    }
}