package com.example.planet

import com.example.planet.guide.LabelDetector

// Android 기본
import GuideResultScreen
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log

// Activity & CameraX
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview as CameraXPreview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView

// Compose 기본
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.text.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.geometry.*
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.currentBackStackEntryAsState


// Compose Foundation
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.*
import androidx.compose.foundation.lazy.LazyColumn

// Compose Material3
import androidx.compose.material3.*
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween

// Icons
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.automirrored.rounded.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.LifecycleOwner
import androidx.compose.material.icons.outlined.CheckCircle

// 기타
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

import kotlinx.coroutines.delay

import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.content.ContextCompat
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

import androidx.camera.core.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.navigation.compose.*
import java.nio.ByteBuffer

import androidx.navigation.navArgument
import androidx.navigation.NavType

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot


class MainActivity : ComponentActivity() {

    public lateinit var imageCapture: ImageCapture
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var detector: Yolov8sDetector
    private lateinit var labelDetector: LabelDetector

    companion object {
        var latestGuideText: String = "가이드를 불러오는 중입니다."
        var latestCapturedBitmap: Bitmap? = null
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        detector = Yolov8sDetector(this) // "폐기물 분리"
        labelDetector = LabelDetector(this) // "분리배출 표시"
        cameraExecutor = Executors.newSingleThreadExecutor()

        setContent {
            val navController = rememberNavController()
            var showSplash by remember { mutableStateOf(true) }
            val context = LocalContext.current

            LaunchedEffect(Unit) {
                delay(2000)
                showSplash = false

                // ✅ 테스트용 이미지 넣기
                val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.test_image)

                try {
                    val results = detector.detect(bitmap, confidenceThreshold = 0.1f)
                    if (results.isEmpty()) {
                        Log.d("YOLO-DEBUG", "결과 없음")
                    } else {
                        results.forEachIndexed { i, it ->
                            Log.d("YOLO-RESULT", "[$i] classId=${it.classId}, confidence=${it.confidence}, guide=${it.guide}")
                        }
                    }
                    Log.d("YOLO-DEBUG", "Test Image Detection Count: ${results.size}")

                    results.forEachIndexed { i, it ->
                        Log.d("YOLO-CLASS", "[$i] class=${it.classId}, confidence=${it.confidence}")
                        Log.d("YOLO-GUIDE", "guide=${it.guide}")
                    }
                } catch (e: Exception) {
                    Log.e("YOLO-ERROR", "테스트 이미지 처리 실패", e)
                }
            }
            MaterialTheme {
                if (showSplash) {
                    SplashScreen()
                } else {
                    Scaffold(
                        bottomBar = {
                            val currentRoute = getCurrentRoute(navController)
                            if (currentRoute != "camera") {
                                BottomNavigationBar(
                                    selectedItem = currentRoute,
                                    onItemClick = { route -> navController.navigate(route) }
                                )
                            }
                        }
                    ) { innerPadding ->
                        NavHost(
                            navController = navController,
                            startDestination = "home",
                            modifier = Modifier.padding(innerPadding)
                        ) {
                            composable("login") { LoginScreen(navController) }
                            composable("home") { HomeScreen(navController) }
                            composable("quiz") { StudyQuizPage(navController) }
                            composable("rank") { LeaderboardScreen(navController) }
                            composable("mypage") { Mypage(navController) }
                            composable("camera") { CameraScreenPreview(navController, this@MainActivity) }
                            composable("guide") { GuideResultScreen(navController) }
                            composable("guide_result") {
                                GuideResultScreen(navController = navController, guideText = latestGuideText)
                            }
                            composable("study_quiz") { StudyQuizPage(navController) }
                            composable("matching_quiz") {
                                QuizMatchingQuestionScreen(
                                    navController = navController,
                                    quiz = chapter3Quizzes[0], // 혹은 필요한 index로 설정
                                    index = 10 // 11번째 문제라면 10
                                )
                            }
                            composable(
                                route = "quiz_question/{index}",
                                arguments = listOf(navArgument("index") { type = NavType.IntType })
                            ) { backStackEntry ->
                                val index = backStackEntry.arguments?.getInt("index") ?: 0
                                val quiz = chapter1FullQuizzes.getOrNull(index)
                                if (quiz != null) {
                                    QuizQuestionScreen(navController, quiz, index)
                                }
                            }
                            // 이미 존재하던 해설 페이지
                            composable(
                                route = "quiz_answer/{index}?userAnswer={userAnswer}",
                                arguments = listOf(
                                    navArgument("index") { type = NavType.IntType },
                                    navArgument("userAnswer") {
                                        type = NavType.StringType
                                        defaultValue = ""
                                        nullable = true
                                    }
                                )
                            ) { backStackEntry ->
                                val index = backStackEntry.arguments?.getInt("index") ?: 0
                                val userAnswer = backStackEntry.arguments?.getString("userAnswer") ?: ""
                                val quiz = chapter1FullQuizzes.getOrNull(index)
                                if (quiz != null) {
                                    QuizAnswerScreen(navController, quiz, index, userAnswer)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun takePhoto(navController: NavHostController) {
        val capture = imageCapture ?: return

        capture.takePicture(
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(imageProxy: ImageProxy) {
                    val bitmap = imageProxyToBitmap(imageProxy)
                    imageProxy.close()
                    // 클래스 목록 확인 (한 번만 보면 되니 로그 확인 후 지워도 됩니다)

                    val results = detector.detect(bitmap)
                    if (results.isNotEmpty()) {
                        latestGuideText = results[0].guide
                        latestCapturedBitmap = bitmap
                        navController.navigate("guide_result")
                    }
                }
                override fun onError(exception: ImageCaptureException) {
                    Log.e("Camera", "촬영 실패", exception)
                }
            }
        )
    }

    private fun imageProxyToBitmap(imageProxy: ImageProxy): Bitmap {
        val buffer: ByteBuffer = imageProxy.planes[0].buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    fun takeLabelPhoto(navController: NavHostController) {
        val capture = imageCapture ?: return

        capture.takePicture(
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(imageProxy: ImageProxy) {
                    val bitmap = imageProxyToBitmap(imageProxy)
                    imageProxy.close()

                    labelDetector.process(
                        bitmap,
                        onResult = { guideText ->
                            navController.navigate("guide_result/${Uri.encode(guideText)}")
                        },
                        onError = { error ->
                            Log.e("LabelDetector", "오류: $error")
                        }
                    )
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e("Camera", "촬영 실패", exception)
                }
            }
        )
    }
}

@Composable
fun QuizQuestionScreen(navController: NavHostController, quiz: QuizItem, index: Int) {
    when (quiz.type) {
        QuizType.OX -> QuizOXQuestionScreen(navController, quiz, index)
        QuizType.SUBJECTIVE -> QuizSubjectiveQuestionScreen(navController, quiz, index)
        QuizType.MATCHING -> QuizMatchingQuestionScreen(navController, quiz, index)
        QuizType.MULTIPLE_CHOICE -> QuizMultipleChoiceQuestionScreen(navController, quiz, index)
    }
}

@Composable
fun getCurrentRoute(navController: NavHostController): String {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route ?: "home"
}

@Composable
fun SplashScreen() {
    val pretendardbold = FontFamily(Font(R.font.pretendardbold))

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF18BEDD)), // 원하는 색상
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Planet",
            fontSize = 40.sp,
            color = Color.White,
            fontFamily = pretendardbold
        )
    }
}

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

//@Preview(showBackground = true)
@Composable
fun HomeScreen(navController: NavHostController) {

    val pretendardsemibold = FontFamily(Font(R.font.pretendardsemibold))
    val pretendardbold = FontFamily(Font(R.font.pretendardbold))
    val iconTint = Color(0xFF546A6E)


    Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFCAEBF1))
                .padding(
                    start = 20.dp,
                    end = 20.dp,
                    top = 70.dp
                    //bottom = innerPadding.calculateBottomPadding()
                )
        ) {

            // ======= 출석 헤더 =======
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🌞 연속 7일 출석하고 있어요!",
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 12.sp,
                    fontFamily = pretendardsemibold
                )
                Text(
                    text = "89 P",
                    fontSize = 14.sp,
                    color = Color.Black,
                    fontFamily = pretendardsemibold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ======= 최근 퀴즈 박스 (버튼 + 그림자 + TODO 이동) =======
            Surface(
                tonalElevation = 8.dp,
                shadowElevation = 8.dp,
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .customShadow()
                    .clickable {
                        navController.navigate("quiz_question/0")
                    }
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "RECENT QUIZ",
                        color = Color.Gray,
                        fontSize = 12.06.sp,
                        fontFamily = pretendardbold,
                        modifier = Modifier.padding(start = 15.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 15.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "첫 문제를 풀어보세요 !", // TODO: 히스토리 확인해서 최근 문제 또는 첫 문제로 멘트 변경
                                color = Color(0xFF546A6E),
                                fontSize = 16.64.sp,
                                style = MaterialTheme.typography.titleMedium,
                                fontFamily = pretendardbold
                            )

                            Spacer(modifier = Modifier.width(6.dp))

                            Icon(
                                imageVector = Icons.Default.KeyboardDoubleArrowRight,
                                contentDescription = "Next",
                                tint = Color(0xFF546A6E)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ======= 순위 박스 (그림자 + 텍스트 색상 수정 + 구분선 추가) =======
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF)),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .customShadow()
                    .height(60.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "내 등수",
                            fontSize = 13.sp,
                            style = MaterialTheme.typography.bodyMedium,
                            fontFamily = pretendardbold,
                            color = Color(0xFF284449)
                        )
                        Text(
                            text = "# 6",
                            fontSize = 15.sp,
                            style = MaterialTheme.typography.titleLarge,
                            fontFamily = pretendardbold,
                            color = Color(0xFF284449)
                        )
                    }

                    // 👉 중앙 세로 구분선
                    Box(
                        modifier = Modifier
                            .width(1.dp)               // 세로선이므로 width는 얇게
                            .height(30.dp)             // 높이는 원하는 만큼
                            .background(Color.LightGray)
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "학교 점수",
                            fontSize = 13.sp,
                            style = MaterialTheme.typography.bodyMedium,
                            fontFamily = pretendardbold,
                            color = Color(0xFF284449)
                        )
                        Text(
                            text = "# 14",
                            fontSize = 15.sp,
                            style = MaterialTheme.typography.titleLarge,
                            fontFamily = pretendardbold,
                            color = Color(0xFF284449)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .customShadow()
                    .height(IntrinsicSize.Min), // 높이 고정보다는 콘텐츠에 맞게
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "분리배출 도우미",
                        fontSize = 19.sp,
                        style = MaterialTheme.typography.titleLarge,
                        fontFamily = pretendardbold,
                        color = Color(0xFF284449)
                    )

                    Text(
                        text = "헷갈리는 분리배출, AI 가이드를 받아보세요!",
                        fontSize = 14.sp,
                        style = MaterialTheme.typography.bodyMedium,
                        fontFamily = pretendardbold,
                        color = Color(0xff859DA1)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center, // 버튼들 전체 중앙 정렬
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Button(
                                onClick = { navController.navigate("camera") },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE4FBFF))
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "폐기물 분리",
                                        fontSize = 16.sp,
                                        fontFamily = pretendardbold,
                                        color = Color(0xFF284449)
                                    )

                                    Icon(
                                        imageVector = Icons.AutoMirrored.Rounded.ArrowForwardIos,
                                        contentDescription = "Next",
                                        tint = iconTint,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }

                            Button(
                                onClick = { navController.navigate("camera") },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE4FBFF))
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "분리배출 표시",
                                        fontSize = 16.sp,
                                        fontFamily = pretendardbold,
                                        color = Color(0xFF284449)
                                    )

                                    Icon(
                                        imageVector = Icons.AutoMirrored.Rounded.ArrowForwardIos,
                                        contentDescription = "Next",
                                        tint = iconTint,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

//@Preview(showBackground = true)
@SuppressLint("SuspiciousIndentation")
@Composable//-->메인퀴즈페이지
fun StudyQuizPage(navController: NavHostController) {
    val pretendardsemibold = FontFamily(Font(R.font.pretendardsemibold))
    val pretendardbold = FontFamily(Font(R.font.pretendardbold))
    val context = LocalContext.current
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFCAEBF1))
                .padding(
                    start = 20.dp,
                    end = 20.dp,
                    top = 70.dp
                    //bottom = innerPadding.calculateBottomPadding()
                )
        ) {

            // ======= 출석 헤더 =======
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🌞 연속 7일 출석하고 있어요!",
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 14.sp,
                    fontFamily = pretendardsemibold
                )
                Text(
                    text = "89 P",
                    fontSize = 14.sp,
                    color = Color.Black,
                    fontFamily = pretendardsemibold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ======= 최근 퀴즈 박스 (버튼 + 그림자 + TODO 이동) =======
            Surface(
                tonalElevation = 8.dp,
                shadowElevation = 8.dp,
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .customShadow()
                    .clickable {
                        val sharedPref =
                            context.getSharedPreferences("quiz_prefs", Context.MODE_PRIVATE)
                        val lastIndex = sharedPref.getInt("last_index", 0)
                        navController.navigate("quiz_question/$lastIndex")
                    }
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "RECENT QUIZ",
                        color = Color.Gray,
                        fontSize = 12.06.sp,
                        fontFamily = pretendardbold,
                        modifier = Modifier.padding(start = 15.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 15.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "이어서 문제를 풀어보세요 !", // TODO: 히스토리 확인해서 최근 문제 또는 첫 문제로 멘트 변경
                                color = Color(0xFF546A6E),
                                fontSize = 16.64.sp,
                                style = MaterialTheme.typography.titleMedium,
                                fontFamily = pretendardbold
                            )

                            Spacer(modifier = Modifier.width(6.dp))

                            Icon(
                                imageVector = Icons.Default.KeyboardDoubleArrowRight,
                                contentDescription = "Next",
                                tint = Color(0xFF546A6E)
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // ======= 틀린문제 복습 박스 =======
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(20.dp),
                //elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .customShadow()
                    .clickable { /*TODO*/ }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 30.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "틀렸던 문제를 다시 풀어볼까요?",
                        color = Color(0xFF546A6E),
                        fontSize = 18.sp,
                        style = MaterialTheme.typography.titleMedium,
                        fontFamily = pretendardbold
                    )
                    Spacer(modifier = Modifier.width(6.dp))

                    Icon(
                        imageVector = Icons.Default.KeyboardDoubleArrowRight,
                        contentDescription = "Next",
                        tint = Color(0xFF546A6E)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ===== 흰색 박스 =====
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
                    )

            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()) // ← 스크롤 적용
                        .padding(24.dp)
                ) {
                    Text(
                        text = "Study Quizzes",
                        fontSize = 20.sp,
                        fontFamily = pretendardbold,
                        color = Color(0xFF546A6E) // 변경된 제목 색상
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    val selectedChapterIndex = remember { mutableStateOf(0) }

                    listOf(
                        Triple("1", "Chapter 1", "20 문제 | 완료!"),
                        Triple("2", "Chapter 2", "20 문제"),
                        Triple("3", "Chapter 3", "20 문제"),
                        Triple("4", "Chapter 4", "20 문제"),
                        Triple("5", "Chapter 5", "20 문제")
                    ).forEachIndexed { index, (number, title, subtitle) ->

                        val isSelected = selectedChapterIndex.value == index

                        val backgroundColor = if (isSelected) Color(0xFF4E4E58) else Color.White
                        val borderColor = if (isSelected) Color.Transparent else Color(0xFFB9DEE4)
                        val titleColor = if (isSelected) Color(0xFFC2EFF7) else Color(0xFF546A6E)
                        val subtitleColor = if (isSelected) Color(0xFF95D0DB) else Color(0xFF858494)
                        val context = LocalContext.current
                        val sharedPref = context.getSharedPreferences("quiz_prefs", Context.MODE_PRIVATE)
                        Button(
                            onClick = {
                                val startIndex = if (number == "1") {
                                    sharedPref.getInt("last_index", 0)
                                } else {
                                    0
                                }
                                navController.navigate("quiz_question/$startIndex")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = backgroundColor),
                            shape = RoundedCornerShape(20.dp),
                            contentPadding = PaddingValues(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp)
                                .border(
                                    width = 2.dp,
                                    color = borderColor,
                                    shape = RoundedCornerShape(20.dp)
                                )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 11.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .background(
                                            Color(0xFF53AEBE),
                                            shape = RoundedCornerShape(17.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = number,
                                        fontSize = 27.sp,
                                        fontFamily = pretendardbold,
                                        color = Color.White
                                    )
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                Column {
                                    Text(
                                        text = title,
                                        fontSize = 16.sp,
                                        fontFamily = pretendardbold,
                                        color = titleColor
                                    )
                                    Text(
                                        text = subtitle,
                                        fontSize = 14.sp,
                                        fontFamily = pretendardsemibold,
                                        color = subtitleColor
                                    )
                                }

                                Spacer(modifier = Modifier.weight(1f))

                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.NavigateNext,
                                    contentDescription = "Next",
                                    tint = Color(0xFF53AEBE)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }}}}}

@Composable//-->하단 네비게이션바
fun BottomNavigationBar(
    modifier: Modifier = Modifier,
    selectedItem: String = "none",
    onItemClick: (String) -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
            .shadow(
                elevation = 25.dp,
                shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
                clip = false
            )
            .background(Color.Transparent)
    ) {
        // 배경
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
        ) {
            val width = size.width
            val height = size.height
            val cornerRadius = 40.dp.toPx()

            drawPath(
                path = Path().apply {
                    moveTo(0f, cornerRadius)
                    quadraticBezierTo(0f, 0f, cornerRadius, 0f)
                    lineTo(width - cornerRadius, 0f)
                    quadraticBezierTo(width, 0f, width, cornerRadius)
                    lineTo(width, height)
                    lineTo(0f, height)
                    close()
                },
                color = Color.White
            )
        }

        // 아이콘 버튼들
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 30.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavItem(
                icon = Icons.Default.Home,
                isSelected = selectedItem == "home",
                onClick = { onItemClick("home") }
            )
            NavItem(
                icon = Icons.Default.School,
                isSelected = selectedItem == "quiz",
                onClick = { onItemClick("quiz") }
            )

            Spacer(modifier = Modifier.width(50.dp))

            NavItem(
                icon = Icons.Default.BarChart,
                isSelected = selectedItem == "rank",
                onClick = { onItemClick("rank") }
            )
            NavItem(
                icon = Icons.Default.Person,
                isSelected = selectedItem == "mypage",
                onClick = { onItemClick("mypage") }
            )
        }

        // 카메라 버튼
        FloatingActionButton(
            onClick = { onItemClick("camera") },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-28).dp)
                .size(68.dp),
            containerColor = Color(0xFF53AEBE),
            shape = CircleShape
        ) {
            Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = "카메라",
                tint = Color.White,
                modifier = Modifier.size(40.dp)
            )
        }
    }
}

@Composable//-->네비게이션바 아이콘들
fun NavItem(
    icon: ImageVector,
    isSelected: Boolean,
    iconSize: Dp = 28.dp,
    onClick: () -> Unit
) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isSelected) Color(0xFF0C092A) else Color.Gray,
            modifier = Modifier.size(iconSize)
        )
    }
}

@Composable//-->그림자
fun Modifier.customShadow(
    shadowColors: List<Color> = listOf(
        Color(0x00CCCCCC),
        Color(0x10CCCCCC),
        Color(0x30CCCCCC),
        Color(0x50000000) // 진한 그림자 마지막
    ),
    cornerRadius: Dp = 20.dp
): Modifier = this.then(
    Modifier.drawBehind {
        val radius = cornerRadius.toPx()
        shadowColors.forEachIndexed { index, color ->
            drawRoundRect(
                color = color,
                topLeft = Offset(2f, (index + 1) * 2f),
                size = size,
                cornerRadius = CornerRadius(radius, radius)
            )
        }
    }
)


//@Preview(showBackground = true)
@Composable//-->O/X 문제페이지
fun QuizOXQuestionScreen(navController: NavHostController, quiz: QuizItem, index: Int) {

    val pretendardsemibold = FontFamily(
        Font(R.font.pretendardsemibold)
    )
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        context.getSharedPreferences("quiz_prefs", Context.MODE_PRIVATE)
            .edit()
            .putInt("last_index", index)
            .apply()
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF7AC5D3)) // 배경
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

            // 상단 정보: 뒤로가기, 문제 수, 점수
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
                    text = "${index + 1} / 20", // 문제 번호
                    fontSize = 18.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    fontFamily = pretendardsemibold
                )

                Text(
                    text = "89 P",
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
                    .padding(horizontal = 60.dp, vertical = 250.dp),
                textAlign = TextAlign.Center
            )

            // O / X 버튼
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .height(180.dp)
            ) {
                // O 버튼
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(topStart = 16.dp))
                        .background(Color(0xFFE56A6A))
                        .clickable {
                            val route = "quiz_answer/$index?userAnswer=O"
                            navController.navigate(route)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "O",
                        color = Color.White,
                        fontSize = 100.sp,
                        fontFamily = pretendardsemibold
                    )
                }

                // X 버튼
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(topEnd = 16.dp))
                        .background(Color(0xFF6A93E5))
                        .clickable {
                            val route = "quiz_answer/$index?userAnswer=X" // 또는 "X"
                            navController.navigate(route)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "X",
                        color = Color.White,
                        fontSize = 100.sp,
                        fontFamily = pretendardsemibold
                    )
                }
            }
        }
    }
}

//@Preview(showBackground = true)
@Composable//->주관식형 문제 페이지
fun QuizSubjectiveQuestionScreen(navController: NavHostController, quiz: QuizItem, index: Int) {

    val pretendardsemibold = FontFamily(Font(R.font.pretendardsemibold))
    var answer by remember { mutableStateOf("") }
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        context.getSharedPreferences("quiz_prefs", Context.MODE_PRIVATE)
            .edit()
            .putInt("last_index", index)
            .apply()
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
                    text = "${index + 1} / 20",
                    fontSize = 18.sp,
                    color = Color.Black,
                    fontFamily = pretendardsemibold
                )

                Text(
                    text = "89 P",
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

//@Preview(showBackground = true)
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

                                    )}
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


//@Preview(showBackground = true)
@Composable//-->4지선다 문제페이지
fun QuizMultipleChoiceQuestionScreen(navController: NavHostController, quiz: QuizItem, index: Int) {
    val pretendardsemibold = FontFamily(Font(R.font.pretendardsemibold))
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        context.getSharedPreferences("quiz_prefs", Context.MODE_PRIVATE)
            .edit()
            .putInt("last_index", index)
            .apply()
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
                    text = "${index + 1} / 20",
                    fontSize = 18.sp,
                    color = Color.Black,
                    fontFamily = pretendardsemibold
                )

                Text(
                    text = "89 P",
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
                                            val label =
                                                ('A' + globalIndex).toString() // 이미 위에서 정의되어 있음
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

@Composable
fun CameraScreenContent(
    navController: NavHostController,
    selectedTab: String,
    onTabChange: (String) -> Unit,
    onCaptureClick: () -> Unit,
    pretendardbold: FontFamily,
) {
    RequestCameraPermission {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 30.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // 🔙 뒤로가기 버튼 (왼쪽)
                IconButton(onClick = {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = false }
                    }
                }) {
                    Icon(
                        imageVector = Icons.Rounded.ArrowBackIosNew,
                        contentDescription = "뒤로 가기",
                        tint = Color.White,
                        modifier = Modifier.size(25.dp)
                    )
                }

                Spacer(modifier = Modifier.width(20.dp))

                // 🔘 탭 스위치 (오른쪽)
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(Color.DarkGray),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    listOf("폐기물 분리", "분리배출 표시").forEach { tab ->
                        Text(
                            text = tab,
                            fontFamily = pretendardbold,
                            fontSize = 16.sp,
                            color = if (tab == selectedTab) Color.White else Color.LightGray,
                            modifier = Modifier
                                .padding(8.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (tab == selectedTab) Color(0xFF00A6C4) else Color.Transparent)
                                .clickable { onTabChange(tab) }
                                .padding(horizontal = 20.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // 🔳 카메라 프리뷰 영역 (가운데)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp) // ✅ 높이 줄임
                    .padding(horizontal = 16.dp)
            ) {
                CameraPreviewView(
                    context = LocalContext.current,
                    lifecycleOwner = LocalLifecycleOwner.current,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(8.dp)) // ✅ 위아래 여백 줄임

            // 📸 하단 촬영 버튼
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(bottom = 0.dp), // ✅ 아래 여백 제거로 위로 올림
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = onCaptureClick,
                    modifier = Modifier
                        .zIndex(1f)
                        .size(80.dp) // 약간 줄여도 좋음
                ) {
                    Icon(
                        imageVector = Icons.Default.Circle,
                        contentDescription = "촬영",
                        modifier = Modifier.size(65.dp), // 아이콘 크기도 비율 맞춰 조정 가능
                        tint = Color.White
                    )
                }
            }
        }
    }
}

//@Preview(showBackground = true)
@Composable//-->카메라페이지
fun CameraScreenPreview(navController: NavHostController, mainActivity: MainActivity) {
    val pretendardbold = FontFamily(Font(R.font.pretendardbold))
    var selectedTab by remember { mutableStateOf("폐기물 분리") }

    if (!LocalInspectionMode.current) {
        CameraScreenContent(
            navController = navController,
            selectedTab = selectedTab,
            onTabChange = { selectedTab = it },
            onCaptureClick = {
                if (selectedTab == "폐기물 분리") {
                    mainActivity.takePhoto(navController)
                } else {
                    mainActivity.takeLabelPhoto(navController)
                    navController.navigate("guide")
                }
            },
            pretendardbold = pretendardbold
        )
    } else {
        // Preview 전용 대체 UI
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.DarkGray),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("📷 카메라 화면은\n미리보기에 표시되지 않아요", color = Color.White, textAlign = TextAlign.Center)
        }
    }
}

@Composable//-->카메라뷰
fun CameraPreviewView(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    modifier: Modifier = Modifier
) {
    val previewView = remember { PreviewView(context) }
    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

    if (!LocalInspectionMode.current) {
        AndroidView(
            factory = { previewView },
            modifier = modifier
        )

        LaunchedEffect(Unit) {
            val cameraProvider = cameraProviderFuture.get()
            // ✅ Preview Builder (고쳤음)
            val preview = CameraXPreview.Builder().build()
            preview.setSurfaceProvider(previewView.surfaceProvider)
            // ⬇️ 추가된 부분 (imageCapture 생성)
            val imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()
            // ⬆️ 추가된 부분

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture  // ⬅️ 여기에도 imageCapture 추가 바인딩
                )
                (context as MainActivity).imageCapture = imageCapture // ⬅️ MainActivity에 연결
            } catch (e: Exception) {
                Log.e("CameraPreview", "카메라 바인딩 실패", e)
            }
        }
    } else {
        // Preview 모드일 땐 단순 Box로 대체
        Box(
            modifier = modifier
                .background(Color.DarkGray)
                .fillMaxWidth()
                .height(550.dp) // ✅ 원하는 높이 지정
                .padding(horizontal = 16.dp), // ✅ 원하는 높이 지정
            contentAlignment = Alignment.Center
        ) {
            Text("카메라 미리보기", color = Color.White)
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable//-->카메라 권한 요청
fun RequestCameraPermission(content: @Composable () -> Unit) {
    val permissionState = rememberPermissionState(android.Manifest.permission.CAMERA)

    when (permissionState.status) {
        is com.google.accompanist.permissions.PermissionStatus.Granted -> {
            content() // 권한 허용됨 → 콘텐츠 보여주기
        }

        is com.google.accompanist.permissions.PermissionStatus.Denied -> {
            LaunchedEffect(Unit) {
                permissionState.launchPermissionRequest()
            }

            // 거부된 경우 → 안내 메시지
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "카메라 권한이 필요합니다",
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
        }
    }
}

//@Preview(showBackground = true)
@Composable
fun GuideResultScreen(navController: NavHostController) {

    val pretendardsemibold = FontFamily(Font(R.font.pretendardsemibold))
    val guideText = Uri.decode(navController.currentBackStackEntry?.arguments?.getString("guideText") ?: "분리배출 표시를 인식하지 못했습니다.\n다시 촬영해주세요 :(")

    Scaffold(

    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFF7AC5D3))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(
                        top = 40.dp,
                        start = 16.dp,
                        end = 16.dp
                    )
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .background(Color.White)
                    .height(800.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(30.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 🔹 상단 바
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { navController.navigate("camera") }) {
                            Icon(
                                imageVector = Icons.Rounded.ArrowBackIosNew,
                                modifier = Modifier.size(25.dp),
                                tint = Color.Gray,
                                contentDescription = "뒤로 가기"
                            )
                        }

                        Text(
                            text = "분리배출 도우미",
                            fontSize = 18.sp,
                            color = Color.Black,
                            fontFamily = pretendardsemibold
                        )

                        IconButton(onClick = { navController.navigate("home") }) {
                            Icon(
                                imageVector = Icons.Rounded.Close,
                                modifier = Modifier.size(28.dp),
                                tint = Color.Gray,
                                contentDescription = "닫기"
                            )
                        }
                    }

                    // 🔹 이미지 박스 (중앙 위치)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.Gray)
                            .fillMaxWidth(0.8f)
                            .aspectRatio(1f)
                    ) {
                        Image(
                            painter = ColorPainter(Color.LightGray),
                            contentDescription = "촬영 이미지",
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Text(
                        text = guideText,
                        fontSize = 20.sp,
                        color = Color.Black,
                        fontFamily = pretendardsemibold,
                        textAlign = TextAlign.Center
                    )
                    }
                }
            }
        }
    }

//@Preview(showBackground = true)
@Composable
fun LeaderboardScreen(navController: NavHostController) {
    val pretendard = FontFamily(Font(R.font.pretendardsemibold))
    var selectedTab by remember { mutableStateOf("학생별") }

        Box(
            modifier = Modifier
                .fillMaxSize()
                //.padding(innerPadding)
                .background(Color(0xFFCAEBF1))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .padding(top = 64.dp)
                        .width(300.dp)
                        .height(47.dp)
                        .align(Alignment.CenterHorizontally)
                        .border(2.dp, Color(0xFF60B6C2), RoundedCornerShape(22.dp))
                        .clip(RoundedCornerShape(20.dp))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(5.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val activeColor = Color(0xFF7AC5D3)
                        val inactiveColor = Color.Gray

                        listOf("학생별", "학급별").forEach { tab ->
                            Box(
                                modifier = Modifier
                                    .width(150.dp)
                                    .height(36.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(if (selectedTab == tab) Color.White else Color.Transparent)
                                    .clickable { selectedTab = tab },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = tab,
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = pretendard,
                                    fontSize = 15.sp,
                                    color = if (selectedTab == tab) activeColor else inactiveColor
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier
                        .width(350.dp)
                        .height(80.dp)
                        .padding(horizontal = 24.dp)
                        .align(Alignment.CenterHorizontally)
                        .border(2.dp, Color(0xFF60B6C2), RoundedCornerShape(22.dp))
                        .clip(RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .background(Color(0xFF60B6C2), shape = RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "#4",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 25.sp,
                                fontFamily = pretendard
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = "다른 사용자들보다\n60% 앞서고 있어요!",
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = pretendard,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Start,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Column(modifier = Modifier.fillMaxSize()) {

                    // podium 이미지 + 프로필을 포함한 Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp), // 이미지 + Row를 담을 공간
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        // podium 이미지
                        Image(
                            painter = painterResource(id = R.drawable.podium),
                            contentDescription = "시상대",
                            modifier = Modifier
                                .width(300.dp)
                                .height(200.dp)
                                .offset(y = 60.dp)
                        )

                        // podium 위 Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .offset(y = (-120).dp), // 너무 겹치지 않도록 적절히 조절
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            PodiumItem(
                                name = "이슈니",
                                score = 1469,
                                rank = 2,
                                modifier = Modifier.offset(x = 10.dp)
                            )
                            PodiumItem(name = "김슈니", score = 2569, rank = 1)
                            PodiumItem(
                                name = "박슈니",
                                score = 1053,
                                rank = 3,
                                modifier = Modifier.offset(x = (-10).dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    LeaderboardList()

                }
            }
        }
    }

@Composable
fun PodiumItem(name: String, score: Int, rank: Int, modifier: Modifier = Modifier) {
    val avatarColor = when (rank) {
        1 -> Color(0xFFDFF5E5)
        2 -> Color(0xFFFFD5DC)
        3 -> Color(0xFFD9D6FF)
        else -> Color.LightGray
    }
    val offsetY = when (rank) {
        1 -> (-30).dp  // 가장 높이
        2 -> (0).dp  // 중간
        3 -> (+30).dp
        else -> 0.dp
    }

    Column(
        modifier = modifier
            .width(80.dp)
            .offset(y = offsetY),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .border(0.5.dp, Color(0xff53AEBE), shape = CircleShape)
                .background(avatarColor, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {}

        Text(
            text = name,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )

        Box(
            modifier = Modifier
                .background(Color.White, shape = RoundedCornerShape(40))
                .padding(horizontal = 12.dp, vertical = 7.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$score QP",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )
        }
    }
}

@Composable
fun LeaderboardRow(rank: Int, name: String, score: Int, color: Color, isMe: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(95.dp)
            .clip(RoundedCornerShape(32.dp))
            .background(Color.White)
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 순위 번호 (작고 연한 동그라미)
        Box(
            modifier = Modifier
                .size(23.dp)
                .border(1.dp, Color(0xFFDADADA), shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text("$rank", fontSize = 10.sp, color = Color.Gray)
        }

        Spacer(modifier = Modifier.width(12.dp))

        // 프로필 색상 원
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(color, CircleShape)
        )

        Spacer(modifier = Modifier.width(12.dp))

        // 이름과 점수
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Text("${score} points", fontSize = 12.sp, color = Color.Gray)
        }

        // 왕관 아이콘 (hexagon 느낌 흉내내기용 background + padding)
        Box(
            modifier = Modifier
                .background(
                    color = if (isMe) Color(0xFFFFC107) else Color(0xFFF1F1F1),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(6.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = if (isMe) "내 등수" else null,
                tint = if (isMe) Color.White else Color.LightGray,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun LeaderboardList() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE0F7FA), shape = RoundedCornerShape(24.dp)) // 하늘색 배경 전체로 적용
            .padding(horizontal = 16.dp, vertical = 15.dp)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { LeaderboardRow(1, "김슈니", 2569, Color(0xFFCCF1E5), isMe = true) }
            item { LeaderboardRow(2, "이슈니", 1469, Color(0xFFFFD6DC)) }
            item { LeaderboardRow(3, "박슈니", 1053, Color(0xFFD7D7FB)) }
            items(30) { index ->
                LeaderboardRow(
                    rank = index + 4,
                    name = "사용자 $index",
                    score = 1000 - index * 10,
                    color = Color.LightGray
                )
            }
        }
    }
}

//@Preview(showBackground = true)
@Composable
fun Mypage(navController: NavHostController){
    val pretendardBold = FontFamily(Font(R.font.pretendardbold))

        Box(
            modifier = Modifier
                .fillMaxSize()
                //.padding(innerPadding)
                .background(Color(0xFFCAEBF1))
        ) {// Settings Icon
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings",
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 50.dp, end = 20.dp)
                    .size(28.dp),
                tint = Color.Gray
            )



            // 흰색 카드 박스
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 160.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.weight(1f))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 25.dp, end = 25.dp)
                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                        .background(Color.White)
                        .height(600.dp)
                ) {
                    Text(
                        text = "한국초등학교\n1학년 1반",
                        fontSize = 12.sp,
                        fontFamily = pretendardBold,
                        color = Color.Gray,
                        modifier = Modifier.padding(start = 24.dp, top=20.dp) // 왼쪽 패딩만 줌
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 100.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Rank box
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp)
                                .background(Color.White)
                                .customShadow()
                                .background(Color(0xFF7AD1E0), RoundedCornerShape(16.dp))
                                .padding(vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "내 등수\n#6",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF343434),
                                textAlign = TextAlign.Center,
                                //modifier = Modifier.fillMaxWidth(0.4f)
                            )
                            // ✅ 수직 선 (Divider)
                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(36.dp) // 선의 높이
                                    .background(Color(0xFF0C092A).copy(alpha = 0.3f)) // 연한 검정
                            )
                            Text(
                                "학급 등수\n#14",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF343434),
                                textAlign = TextAlign.Center,
                                //modifier = Modifier.fillMaxWidth(0.4f)
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Quiz progress box
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(500.dp)
                                .background(Color(0xFFBCE4EC), RoundedCornerShape(16.dp))
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                buildAnnotatedString {
                                    append("지금까지 총 ")
                                    withStyle(style = SpanStyle(color = Color(0xFF259CB2))) {
                                        append("75문제")
                                    }
                                    append("를 풀었어요!")
                                },
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(20.dp))

                            // Circular progress indicator
                            Box(
                                modifier = Modifier.size(160.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                // 배경 원: 흰색 전체 100%
                                CircularProgressIndicator(
                                    progress = {1f},
                                    modifier = Modifier.fillMaxSize(),
                                    color = Color.White,
                                    strokeWidth = 12.dp
                                )

                                // 실제 진행도: 75%
                                CircularProgressIndicator(
                                    progress = {0.75f},
                                    modifier = Modifier.fillMaxSize(),
                                    color = Color(0xFF28B6CC),
                                    strokeWidth = 12.dp
                                )

                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Row(verticalAlignment = Alignment.Bottom) {
                                        Text("75", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0A0A32))
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text("/100", fontSize = 16.sp, color = Color(0x8028B6CC))
                                    }
                                    Text("quiz played", fontSize = 16.sp, color = Color.Gray) // ✅ 원 안에 들어감
                                }

                            }

                        }
                    }

                }
            }
        }
        // 프로필 영역 전체
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 100.dp), // 🔽 화면 아래로 내림
            horizontalAlignment = Alignment.CenterHorizontally // 🔽 가운데 정렬
        ){
        Box(
            contentAlignment = Alignment.BottomEnd,
            modifier = Modifier
                .size(100.dp) // 프로필 이미지 전체 크기
        ) {
            // 핑크색 원
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFD69ACC)) // 연한 분홍색
            )

            // ✏️ 편집 아이콘
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .offset(x = (-6).dp, y = (-6).dp)
                    .border(2.dp, Color.Gray, CircleShape)
                    .background(Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Profile",
                    modifier = Modifier.size(18.dp),
                    tint = Color.Gray
                )
            }
        }
            Spacer(modifier = Modifier.height(10.dp))
            // Name
            Text("김아무개", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
    }
