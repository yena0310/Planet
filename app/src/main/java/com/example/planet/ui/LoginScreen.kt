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
import com.example.planet.utils.UserStateManager
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

        // 1. 먼저 학교 목록 로드
        db.collection("schools").get()
            .addOnSuccessListener { result ->
                schoolList = result.mapNotNull { it.getString("name") }
                Log.d("LoginScreen", "학교 목록 로드 완료: ${schoolList.size}개")
            }
            .addOnFailureListener { e ->
                Log.e("LoginScreen", "학교 목록 로드 실패", e)
            }

        // 2. 저장된 사용자 확인 (SharedPreferences)
        val savedUserId = UserStateManager.currentUserId
        if (!savedUserId.isNullOrEmpty()) {
            Log.d("LoginScreen", "저장된 사용자 발견: $savedUserId")
            // 사용자 문서가 실제로 존재하는지 확인
            db.collection("users").document(savedUserId).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        Log.d("LoginScreen", "저장된 사용자 문서 확인 완료, 홈으로 이동")
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                    } else {
                        Log.w("LoginScreen", "저장된 사용자 문서가 존재하지 않음, 로그인 유지")
                        UserStateManager.clearUser() // 잘못된 정보 정리
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("LoginScreen", "저장된 사용자 확인 실패", e)
                }
            return@LaunchedEffect
        }

        // 3. Firebase Auth 사용자 확인 (기존 로직)
        currentUser?.let { user ->
            Log.d("LoginScreen", "Firebase 사용자 확인: ${user.uid}")
            db.collection("users").document(user.uid).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        Log.d("LoginScreen", "Firebase 사용자 발견, 홈으로 이동")
                        UserStateManager.setUser(user.uid) // UserStateManager에도 설정
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                    } else {
                        Log.d("LoginScreen", "Firebase 사용자이지만 정보 없음")
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("LoginScreen", "Firebase 사용자 확인 실패", e)
                }
        } ?: run {
            Log.d("LoginScreen", "로그인된 사용자 없음, 로그인 화면 유지")
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
                Button(
                    onClick = {
                        if (isLoading) return@Button

                        Log.d("LoginScreen", "로그인 버튼 클릭")
                        Log.d("LoginScreen", "선택된 정보 - 학교: $selectedSchool, 학년: $selectedGrade, 반: $selectedClass, 이름: $name")

                        isLoading = true

                        // 1. 먼저 기존 사용자 찾기
                        findExistingUser(
                            db = db,
                            schoolName = selectedSchool!!,
                            grade = selectedGrade!!.toInt(),
                            classNum = selectedClass!!.toInt(),
                            name = name,
                            onUserFound = { existingUserId ->
                                Log.d("LoginScreen", "기존 사용자 발견: $existingUserId")
                                // 앱 전체에서 사용할 사용자 상태 설정
                                com.example.planet.utils.UserStateManager.setUser(existingUserId)
                                isLoading = false
                                navController.navigate("welcome/$existingUserId") {
                                    popUpTo("login") { inclusive = true }
                                }
                            },
                            onUserNotFound = {
                                Log.d("LoginScreen", "기존 사용자 없음, 새 사용자 생성")
                                // 2. 기존 사용자가 없으면 새로 생성
                                if (currentUser == null) {
                                    // 익명 로그인 후 새 계정 생성
                                    auth.signInAnonymously()
                                        .addOnSuccessListener { result ->
                                            val userId = result.user!!.uid
                                            Log.d("LoginScreen", "익명 로그인 성공: $userId")

                                            createUserInFirestore(
                                                userId = userId,
                                                schoolName = selectedSchool!!,
                                                grade = selectedGrade!!.toInt(),
                                                classNum = selectedClass!!.toInt(),
                                                name = name,
                                                onSuccess = {
                                                    // 앱 전체에서 사용할 사용자 상태 설정
                                                    com.example.planet.utils.UserStateManager.setUser(userId)
                                                    isLoading = false
                                                    Log.d("LoginScreen", "새 사용자 생성 완료, welcome으로 이동")
                                                    navController.navigate("welcome/$userId") {
                                                        popUpTo("login") { inclusive = true }
                                                    }
                                                },
                                                onFailure = { error ->
                                                    isLoading = false
                                                    Log.e("LoginScreen", "새 사용자 생성 실패: $error")
                                                }
                                            )
                                        }
                                        .addOnFailureListener { e ->
                                            isLoading = false
                                            Log.e("LoginScreen", "익명 로그인 실패", e)
                                        }
                                } else {
                                    // 이미 로그인된 사용자의 경우
                                    val userId = currentUser.uid
                                    createUserInFirestore(
                                        userId = userId,
                                        schoolName = selectedSchool!!,
                                        grade = selectedGrade!!.toInt(),
                                        classNum = selectedClass!!.toInt(),
                                        name = name,
                                        onSuccess = {
                                            // 앱 전체에서 사용할 사용자 상태 설정
                                            com.example.planet.utils.UserStateManager.setUser(userId)
                                            isLoading = false
                                            Log.d("LoginScreen", "새 사용자 생성 완료, welcome으로 이동")
                                            navController.navigate("welcome/$userId") {
                                                popUpTo("login") { inclusive = true }
                                            }
                                        },
                                        onFailure = { error ->
                                            isLoading = false
                                            Log.e("LoginScreen", "새 사용자 생성 실패: $error")
                                        }
                                    )
                                }
                            }
                        )
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

// 기존 사용자 찾기 함수
fun findExistingUser(
    db: FirebaseFirestore,
    schoolName: String,
    grade: Int,
    classNum: Int,
    name: String,
    onUserFound: (String) -> Unit,
    onUserNotFound: () -> Unit
) {
    Log.d("FindUser", "기존 사용자 검색 시작: $schoolName, $grade, $classNum, $name")

    db.collection("users")
        .whereEqualTo("schoolName", schoolName)
        .whereEqualTo("grade", grade)
        .whereEqualTo("classNum", classNum)
        .whereEqualTo("name", name)
        .get()
        .addOnSuccessListener { result ->
            Log.d("FindUser", "검색 결과: ${result.size()}개")
            if (!result.isEmpty) {
                val existingUser = result.documents[0]
                val existingUserId = existingUser.getString("userId")
                if (existingUserId != null) {
                    Log.d("FindUser", "기존 사용자 발견: $existingUserId")
                    onUserFound(existingUserId)
                } else {
                    Log.w("FindUser", "사용자 문서는 있지만 userId가 없음")
                    onUserNotFound()
                }
            } else {
                Log.d("FindUser", "기존 사용자 없음")
                onUserNotFound()
            }
        }
        .addOnFailureListener { e ->
            Log.e("FindUser", "기존 사용자 검색 실패", e)
            onUserNotFound()
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
                                            // 🆕 최근에 푼 문제 인덱스 추가 (1부터 시작)
                                            "lastQuestionIndex" to 1,
                                            // WelcomeScreen에서 사용할 추가 정보
                                            "schoolName" to schoolName,
                                            "grade" to grade,
                                            "classNum" to classNum
                                        )

                                        Log.d("CreateUser", "사용자 데이터 저장 시작: $userData")
                                        db.collection("users").document(userId).set(userData)
                                            .addOnSuccessListener {
                                                Log.d("CreateUser", "✅ 유저 저장 성공 (lastQuestionIndex: 1로 초기화)")
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