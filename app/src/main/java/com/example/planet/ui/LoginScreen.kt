package com.example.planet.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
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
import androidx.navigation.compose.rememberNavController
import com.example.planet.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


// navController: NavHostController
// onLoginClick: () -> Unit = {}

//@Preview
@Composable
fun LoginScreen(navController: NavHostController) {
    val pretendardsemibold = FontFamily(Font(R.font.pretendardsemibold))
    val pretendardextrabold = FontFamily(Font(R.font.pretendardsemibold))

    var school by remember { mutableStateOf("") }
    var selectedGrade by remember { mutableStateOf("") }
    var gradeExpanded by remember { mutableStateOf(false) }
    val gradeOptions = listOf("1", "2", "3", "4", "5", "6")
    var classNum by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }

    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFCAEBF1))
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 40.dp, vertical = 50.dp)
            ) {
                Text(
                    text = "우리의 지구를 위해",
                    color = Color(0xff636363),
                    fontFamily = pretendardsemibold,
                    fontSize = 14.sp
                )
                Text(
                    text = "Planet",
                    fontFamily = pretendardextrabold,
                    fontSize = 50.sp,
                    modifier = Modifier.padding(top = 10.dp, bottom = 50.dp)
                )


                    // 학교 입력 + 검색 아이콘
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .border(1.dp, Color.Gray, RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            BasicTextField(
                                value = school,
                                onValueChange = { school = it },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                decorationBox = { innerTextField ->
                                    if (school.isEmpty()) {
                                        Text("학교", color = Color.Gray)
                                    }
                                    innerTextField()
                                }
                            )
                            Icon(Icons.Default.Search, contentDescription = "검색")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 학년 드롭다운
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .border(1.dp, Color.Gray, RoundedCornerShape(12.dp))
                            .clickable { gradeExpanded = true }
                            .padding(horizontal = 12.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text(
                                text = selectedGrade.ifEmpty { "학년" },
                                color = if (selectedGrade.isNotEmpty()) Color.Black else Color.Gray
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                        }

                        DropdownMenu(
                            expanded = gradeExpanded,
                            onDismissRequest = { gradeExpanded = false }
                        ) {
                            gradeOptions.forEach {
                                DropdownMenuItem(
                                    onClick = {
                                        selectedGrade = it
                                        gradeExpanded = false
                                    },
                                    content = {
                                        Text(it)
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 반 입력
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .border(1.dp, Color.Gray, RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    BasicTextField( // TODO: 반 숫자로 입력하게
                        value = classNum,
                        onValueChange = { classNum = it },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        decorationBox = { innerTextField ->
                            if (classNum.isEmpty()) {
                                Text("반", color = Color.Gray)
                            }
                            innerTextField()
                        }
                    )
                }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 이름 입력
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .border(1.dp, Color.Gray, RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        BasicTextField(
                            value = name,
                            onValueChange = { name = it },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            decorationBox = { innerTextField ->
                                if (name.isEmpty()) {
                                    Text("이름", color = Color.Gray)
                                }
                                innerTextField()
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(100.dp))

                    // 로그인 버튼
                    Button(
                        onClick = {
                            val auth = FirebaseAuth.getInstance()
                            val currentUser = auth.currentUser

                            if (currentUser == null) {
                                // 익명 로그인 먼저
                                auth.signInAnonymously()
                                    .addOnSuccessListener { result ->
                                        val userId = result.user!!.uid
                                        createUserInFirestore(
                                            userId = userId,
                                            schoolName = school,
                                            grade = selectedGrade.toInt(),
                                            classNum = classNum.toInt(),
                                            name = name
                                        )
                                        FirebaseAuth.getInstance().signInAnonymously()
                                            .addOnCompleteListener { task ->
                                                if (task.isSuccessful) {
                                                    navController.navigate("home") {
                                                        popUpTo("login") { inclusive = true }  // 스택에서 login 제거 (선택사항)
                                                    }
                                                } else {
                                                    Log.e("Firebase", "익명 로그인 실패", task.exception)
                                                }
                                            }
                                    }
                                    .addOnFailureListener { e ->
                                        Log.e("Firebase", "익명 로그인 실패", e)
                                    }
                            } else {
                                // 이미 로그인된 상태라면 바로 등록
                                createUserInFirestore(
                                    userId = currentUser.uid,
                                    schoolName = school,
                                    grade = selectedGrade.toInt(),
                                    classNum = classNum.toInt(),
                                    name = name
                                )
                                navController.navigate("welcome")
                            }
                        },
                        enabled = school.isNotBlank() && selectedGrade.isNotBlank() &&
                                classNum.isNotBlank() && name.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = if (school.isNotBlank() && selectedGrade.isNotBlank() &&
                                classNum.isNotBlank() && name.isNotBlank())
                                Color(0xFF18BEDD) else Color.Gray
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .height(48.dp)
                    ) {
                        Text("로그인", color = Color(0xFFFFFFFF), fontSize = 16.sp, fontFamily = pretendardsemibold)
                    }
                }
            }
        }
}

fun createUserInFirestore(
    userId: String,
    schoolName: String,
    grade: Int,
    classNum: Int,
    name: String
) {
    val db = FirebaseFirestore.getInstance()

    // 1. 학교 문서 찾기
    db.collection("schools")
        .whereEqualTo("name", schoolName)
        .get()
        .addOnSuccessListener { schoolResult ->
            if (!schoolResult.isEmpty) {
                val schoolRef = schoolResult.documents[0].reference
                Log.d("Firestore", "학교 찾음: ${schoolRef.path}")

                // 2. 클래스 문서 찾기
                db.collection("classes")
                    .whereEqualTo("schoolId", schoolRef)
                    .whereEqualTo("grade", grade)
                    .whereEqualTo("classNum", classNum)
                    .get()
                    .addOnSuccessListener { classResult ->
                        if (!classResult.isEmpty) {
                            val classRef = classResult.documents[0].reference
                            Log.d("Firestore", "클래스 찾음: ${classRef.path}")

                            val userData = hashMapOf(
                                "userId" to userId.hashCode(),
                                "classId" to classRef,
                                "name" to name,
                                "score" to 0,
                                "ranking" to 0,
                                "profilePhotoPath" to ""
                            )

                            db.collection("users").document(userId).set(userData)
                                .addOnSuccessListener {
                                    Log.d("Firestore", "유저 저장 완료")
                                }
                                .addOnFailureListener {
                                    Log.e("Firestore", "유저 저장 실패", it)
                                }
                        } else {
                            Log.w("Firestore", "클래스 없음")
                        }
                    }
                    .addOnFailureListener {
                        Log.e("Firestore", "클래스 검색 실패", it)
                    }
            } else {
                Log.w("Firestore", "학교 없음")
            }
        }
        .addOnFailureListener {
            Log.e("Firestore", "학교 검색 실패", it)
        }
}
