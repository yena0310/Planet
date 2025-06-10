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
import androidx.navigation.compose.rememberNavController
import com.example.planet.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun LoginScreen(navController: NavHostController) {
    val pretendardsemibold = FontFamily(Font(R.font.pretendardsemibold))
    val pretendardextrabold = FontFamily(Font(R.font.pretendardextrabold))

    val db = FirebaseFirestore.getInstance()

    var selectedSchool by remember { mutableStateOf<String?>(null) }
    var selectedGrade by remember { mutableStateOf<String?>(null) }
    var selectedClass by remember { mutableStateOf<String?>(null) }

    var schoolList by remember { mutableStateOf(listOf<String>()) }
    var gradeList by remember { mutableStateOf(listOf<String>()) }
    var classList by remember { mutableStateOf(listOf<String>()) }

    var name by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser

    // DB User 연결
    LaunchedEffect(Unit) {
        Log.d("LoginScreen", "화면 초기화 시작")

        db.collection("schools").get()
            .addOnSuccessListener { result ->
                schoolList = result.mapNotNull { it.getString("name") }
                Log.d("LoginScreen", "학교 목록 로드 완료: ${schoolList.size}개")
            }
            .addOnFailureListener { e ->
                Log.e("LoginScreen", "학교 목록 로드 실패", e)
            }

        currentUser?.let { user ->
            Log.d("LoginScreen", "기존 사용자 확인: ${user.uid}")
            db.collection("users").document(user.uid).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        Log.d("LoginScreen", "기존 사용자 발견, 홈으로 이동")
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                    } else {
                        Log.d("LoginScreen", "기존 사용자이지만 정보 없음")
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("LoginScreen", "사용자 확인 실패", e)
                }
        }
    }

    // 학교 연결
    LaunchedEffect(selectedSchool) {
        if (selectedSchool != null) {
            Log.d("LoginScreen", "선택된 학교: $selectedSchool")
            db.collection("schools")
                .whereEqualTo("name", selectedSchool)
                .get()
                .addOnSuccessListener { schoolDocs ->
                    val schoolId = schoolDocs.firstOrNull()?.id ?: return@addOnSuccessListener
                    Log.d("LoginScreen", "학교 ID: $schoolId")
                    db.collection("schools").document(schoolId)
                        .collection("grades")
                        .get()
                        .addOnSuccessListener { gradeDocs ->
                            gradeList = gradeDocs.mapNotNull { it.getLong("number")?.toString() }
                            Log.d("LoginScreen", "학년 목록: $gradeList")
                        }
                        .addOnFailureListener { e ->
                            Log.e("LoginScreen", "학년 로드 실패", e)
                        }
                }
                .addOnFailureListener { e ->
                    Log.e("LoginScreen", "학교 검색 실패", e)
                }
        }
    }

    // 학년 반 연결
    LaunchedEffect(selectedGrade) {
        if (selectedSchool != null && selectedGrade != null) {
            Log.d("LoginScreen", "선택된 학년: $selectedGrade")
            db.collection("schools")
                .whereEqualTo("name", selectedSchool)
                .get()
                .addOnSuccessListener { schoolDocs ->
                    val schoolId = schoolDocs.firstOrNull()?.id ?: return@addOnSuccessListener
                    db.collection("schools").document(schoolId)
                        .collection("grades")
                        .whereEqualTo("number", selectedGrade?.toInt())
                        .get()
                        .addOnSuccessListener { gradeDocs ->
                            val gradeId = gradeDocs.firstOrNull()?.id ?: return@addOnSuccessListener
                            Log.d("LoginScreen", "학년 ID: $gradeId")
                            db.collection("schools").document(schoolId)
                                .collection("grades").document(gradeId)
                                .collection("classes")
                                .get()
                                .addOnSuccessListener { classDocs ->
                                    classList = classDocs.mapNotNull { it.getLong("number")?.toString() }
                                    Log.d("LoginScreen", "반 목록: $classList")
                                }
                                .addOnFailureListener { e ->
                                    Log.e("LoginScreen", "반 로드 실패", e)
                                }
                        }
                        .addOnFailureListener { e ->
                            Log.e("LoginScreen", "학년 검색 실패", e)
                        }
                }
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

                DropdownSelector(
                    label = "학교",
                    options = schoolList,
                    selectedOption = selectedSchool,
                    onOptionSelected = { selectedSchool = it }
                )

                DropdownSelector(
                    label = "학년",
                    options = gradeList,
                    selectedOption = selectedGrade,
                    onOptionSelected = { selectedGrade = it }
                )

                DropdownSelector(
                    label = "반",
                    options = classList,
                    selectedOption = selectedClass,
                    onOptionSelected = { selectedClass = it }
                )

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
                // 로그인 버튼
                Button(
                    onClick = {
                        if (isLoading) return@Button

                        Log.d("LoginScreen", "로그인 버튼 클릭")
                        Log.d("LoginScreen", "선택된 정보 - 학교: $selectedSchool, 학년: $selectedGrade, 반: $selectedClass, 이름: $name")

                        isLoading = true

                        if (currentUser == null) {
                            Log.d("LoginScreen", "익명 로그인 시작")
                            // 익명 로그인 먼저
                            auth.signInAnonymously()
                                .addOnSuccessListener { result ->
                                    val userId = result.user!!.uid
                                    Log.d("LoginScreen", "익명 로그인 성공: $userId")

                                    // 사용자 문서 생성
                                    createUserInFirestore(
                                        userId = userId,
                                        schoolName = selectedSchool!!,
                                        grade = selectedGrade!!.toInt(),
                                        classNum = selectedClass!!.toInt(),
                                        name = name,
                                        onSuccess = {
                                            isLoading = false
                                            Log.d("LoginScreen", "사용자 생성 완료, welcome으로 이동")
                                            navController.navigate("welcome") {
                                                popUpTo("login") { inclusive = true }
                                            }
                                        },
                                        onFailure = { error ->
                                            isLoading = false
                                            Log.e("LoginScreen", "사용자 생성 실패: $error")
                                        }
                                    )
                                }
                                .addOnFailureListener { e ->
                                    isLoading = false
                                    Log.e("LoginScreen", "익명 로그인 실패", e)
                                }
                        } else {
                            // 이미 로그인된 사용자의 경우, 사용자 문서가 있는지 확인
                            val userId = currentUser.uid
                            Log.d("LoginScreen", "이미 로그인된 사용자: $userId")

                            db.collection("users").document(userId).get()
                                .addOnSuccessListener { document ->
                                    if (!document.exists()) {
                                        Log.d("LoginScreen", "기존 사용자지만 문서 없음, 새로 생성")
                                        // 문서가 없으면 새로 생성
                                        createUserInFirestore(
                                            userId = userId,
                                            schoolName = selectedSchool!!,
                                            grade = selectedGrade!!.toInt(),
                                            classNum = selectedClass!!.toInt(),
                                            name = name,
                                            onSuccess = {
                                                isLoading = false
                                                Log.d("LoginScreen", "사용자 문서 생성 완료, welcome으로 이동")
                                                navController.navigate("welcome") {
                                                    popUpTo("login") { inclusive = true }
                                                }
                                            },
                                            onFailure = { error ->
                                                isLoading = false
                                                Log.e("LoginScreen", "사용자 문서 생성 실패: $error")
                                            }
                                        )
                                    } else {
                                        isLoading = false
                                        Log.d("LoginScreen", "기존 사용자 문서 발견, welcome으로 이동")
                                        navController.navigate("welcome") {
                                            popUpTo("login") { inclusive = true }
                                        }
                                    }
                                }
                                .addOnFailureListener { e ->
                                    isLoading = false
                                    Log.e("LoginScreen", "사용자 문서 확인 실패", e)
                                }
                        }
                    },
                    enabled = !isLoading && selectedSchool != null && selectedGrade != null
                            && selectedClass != null && name.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = if (!isLoading && selectedSchool != null && selectedGrade != null
                            && selectedClass != null && name.isNotBlank())
                            Color(0xFF18BEDD) else Color.Gray
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .height(48.dp)
                ) {
                    Text(
                        if (isLoading) "처리 중..." else "로그인",
                        color = Color(0xFFFFFFFF),
                        fontSize = 16.sp,
                        fontFamily = pretendardsemibold
                    )
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
    name: String,
    onSuccess: () -> Unit,
    onFailure: (String) -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    Log.d("CreateUser", "createUserInFirestore 호출됨")
    Log.d("CreateUser", "매개변수 - userId: $userId, school: $schoolName, grade: $grade, class: $classNum, name: $name")

    // 1. 학교 문서 찾기
    db.collection("schools")
        .whereEqualTo("name", schoolName)
        .get()
        .addOnSuccessListener { schoolResult ->
            Log.d("CreateUser", "학교 검색 결과: ${schoolResult.size()}개")
            if (!schoolResult.isEmpty) {
                val schoolDoc = schoolResult.documents[0]
                val schoolRef = schoolDoc.reference
                Log.d("CreateUser", "학교 문서 찾음: ${schoolDoc.id}")

                // 2. 학년 문서 찾기
                schoolRef.collection("grades")
                    .whereEqualTo("number", grade)
                    .get()
                    .addOnSuccessListener { gradeResult ->
                        Log.d("CreateUser", "학년 검색 결과: ${gradeResult.size()}개")
                        if (!gradeResult.isEmpty) {
                            val gradeDoc = gradeResult.documents[0]
                            val gradeRef = gradeDoc.reference
                            Log.d("CreateUser", "학년 문서 찾음: ${gradeDoc.id}")

                            // 3. 반 문서 찾기
                            gradeRef.collection("classes")
                                .whereEqualTo("number", classNum)
                                .get()
                                .addOnSuccessListener { classResult ->
                                    Log.d("CreateUser", "반 검색 결과: ${classResult.size()}개")
                                    if (!classResult.isEmpty) {
                                        val classDoc = classResult.documents[0]
                                        val classRef = classDoc.reference
                                        Log.d("CreateUser", "반 문서 찾음: ${classDoc.id}")

                                        val userData = hashMapOf(
                                            "userId" to userId,
                                            "classId" to classRef,
                                            "name" to name,
                                            "score" to 0,
                                            "ranking" to 0,
                                            "profilePhotoPath" to "",
                                            // WelcomeScreen에서 사용할 추가 정보
                                            "schoolName" to schoolName,
                                            "grade" to grade,
                                            "classNum" to classNum
                                        )

                                        Log.d("CreateUser", "사용자 데이터 저장 시작: $userData")
                                        db.collection("users").document(userId).set(userData)
                                            .addOnSuccessListener {
                                                Log.d("CreateUser", "✅ 유저 저장 성공")
                                                onSuccess()
                                            }
                                            .addOnFailureListener { e ->
                                                Log.e("CreateUser", "❌ 유저 저장 실패", e)
                                                onFailure("유저 저장 실패: ${e.message}")
                                            }

                                    } else {
                                        Log.w("CreateUser", "❗ 반 문서 없음")
                                        onFailure("반 정보를 찾을 수 없습니다")
                                    }
                                }
                                .addOnFailureListener { e ->
                                    Log.e("CreateUser", "반 검색 실패", e)
                                    onFailure("반 검색 실패: ${e.message}")
                                }
                        } else {
                            Log.w("CreateUser", "❗ 학년 문서 없음")
                            onFailure("학년 정보를 찾을 수 없습니다")
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e("CreateUser", "학년 검색 실패", e)
                        onFailure("학년 검색 실패: ${e.message}")
                    }
            } else {
                Log.w("CreateUser", "❗ 학교 문서 없음")
                onFailure("학교 정보를 찾을 수 없습니다")
            }
        }
        .addOnFailureListener { e ->
            Log.e("CreateUser", "❌ 학교 검색 실패", e)
            onFailure("학교 검색 실패: ${e.message}")
        }
}

@Composable
fun DropdownSelector(
    label: String,
    options: List<String>,
    selectedOption: String?,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .border(1.dp, Color.Gray, RoundedCornerShape(12.dp))
            .clickable { expanded = true }
            .padding(horizontal = 12.dp, vertical = 16.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = selectedOption ?: label,
                color = if (selectedOption == null) Color.Gray else Color.Black
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
        }

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(onClick = {
                    onOptionSelected(option)
                    expanded = false
                }) {
                    Text(option)
                }
            }
        }
    }
}