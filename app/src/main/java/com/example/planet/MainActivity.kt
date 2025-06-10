package com.example.planet

// UI 및 other files
import com.example.planet.ui.BottomNavigationBar
import com.example.planet.ui.CameraScreen
import com.example.planet.ui.GuideResultScreen
import com.example.planet.ui.HomeScreen
import com.example.planet.ui.LeaderboardScreen
import com.example.planet.ui.LoginScreen
import com.example.planet.ui.WelcomeScreen
import com.example.planet.ui.Mypage
import com.example.planet.ui.QuizAnswerScreen
import com.example.planet.ui.QuizMatchingQuestionScreen
import com.example.planet.ui.QuizMultipleChoiceQuestionScreen
import com.example.planet.ui.QuizOXQuestionScreen
import com.example.planet.ui.QuizSubjectiveQuestionScreen
import com.example.planet.ui.RecycleSignGuide
import com.example.planet.ui.SplashScreen
import com.example.planet.ui.StudyQuizPage
import com.example.planet.guide.LabelDetector

// Android 기본
import android.os.Bundle
import android.util.Log

// Activity & CameraX
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

// Compose 기본
import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.currentBackStackEntryAsState

// Compose Foundation
import androidx.compose.foundation.layout.*

// Compose Material3
import androidx.compose.material3.*

import kotlinx.coroutines.delay

import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.core.content.ContextCompat
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.background
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import java.nio.ByteBuffer

import androidx.navigation.navArgument
import androidx.navigation.NavType
import android.graphics.Matrix
import com.example.planet.ui.SettingScreen

// Firebase 추가
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {

    lateinit var imageCapture: ImageCapture
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var detector: Yolov8sDetector
    private lateinit var labelDetector: LabelDetector

    companion object {
        // 폐기물 분리
        var wasteGuideText: String = "가이드를 불러오는 중입니다."
        var wasteCapturedBitmap: Bitmap? = null

        // 분리배출 표시
        var labelGuideText: String = ""
        var labelCapturedBitmap: Bitmap? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        detector = Yolov8sDetector(this) // "폐기물 분리"
        labelDetector = LabelDetector(this) // "분리배출 표시"
        cameraExecutor = Executors.newSingleThreadExecutor()

        setContent {
            val navController = rememberNavController()
            var showSplash by remember { mutableStateOf(true) }
            var startDestination by remember { mutableStateOf("login") }
            var isCheckingAuth by remember { mutableStateOf(true) }
            val context = LocalContext.current

            // 🆕 사용자 인증 상태 확인
            LaunchedEffect(Unit) {
                Log.d("MainActivity", "앱 시작 - 사용자 인증 상태 확인")
                checkUserAuthentication { destination ->
                    startDestination = destination
                    isCheckingAuth = false
                    Log.d("MainActivity", "시작 경로 결정: $destination")
                }

                delay(2000) // 스플래시 화면 시간
                showSplash = false
            }

            MaterialTheme {
                if (showSplash || isCheckingAuth) {
                    SplashScreen()
                } else {
                    Scaffold(
                        containerColor = Color.Transparent, //배경 투명하게
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
                            startDestination = startDestination, // 🆕 동적으로 시작 경로 설정
                            modifier = Modifier.padding(innerPadding)
                                .background(Color(0xFFCAEBF1))//배경색 지정
                        ) {
                            composable("login") { LoginScreen(navController) }
                            composable("welcome") { WelcomeScreen(navController) }
                            composable("home") { HomeScreen(navController) }
                            composable("quiz") { StudyQuizPage(navController) }
                            composable("rank") { LeaderboardScreen(navController) }
                            composable("mypage") { Mypage(navController) }
                            composable("setting") { SettingScreen(navController) }
                            composable("camera") { CameraScreen(navController, this@MainActivity) }
                            composable("recycle_sign_guide") { RecycleSignGuide(navController, guideText = labelGuideText) }
                            composable("waste_guide") { GuideResultScreen(navController, guideText = wasteGuideText) }
                            composable("study_quiz") { StudyQuizPage(navController) }
                            // 매칭 퀴즈 경로 등록
                            composable("matching_quiz") {
                                QuizMatchingQuestionScreen(
                                    navController = navController,
                                    quiz = chapter3Quizzes[0], // 혹은 필요한 index로 설정
                                    index = 15 // 11번째 문제라면 10
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

    // 🆕 사용자 인증 상태 확인 함수
    private fun checkUserAuthentication(onResult: (String) -> Unit) {
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        Log.d("MainActivity", "현재 사용자: ${currentUser?.uid}")

        if (currentUser != null) {
            // 사용자가 로그인되어 있으면 Firestore에서 사용자 정보 확인
            val db = FirebaseFirestore.getInstance()

            db.collection("users").document(currentUser.uid).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        Log.d("MainActivity", "사용자 정보 존재 - 홈으로 이동")
                        onResult("home") // 사용자 정보가 있으면 바로 홈으로
                    } else {
                        Log.d("MainActivity", "사용자 정보 없음 - 로그인 화면으로")
                        onResult("login") // 사용자 정보가 없으면 로그인 화면으로
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("MainActivity", "사용자 정보 확인 실패", e)
                    onResult("login") // 오류 발생 시 로그인 화면으로
                }
        } else {
            Log.d("MainActivity", "로그인되지 않은 사용자 - 로그인 화면으로")
            onResult("login") // 로그인되지 않았으면 로그인 화면으로
        }
    }

    fun takePhoto(navController: NavHostController) {
        if (!::imageCapture.isInitialized) {
            Log.w("Camera", "imageCapture not initialized")
            Toast.makeText(this, "카메라 준비 중입니다. 잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
            return
        }
        val capture = imageCapture ?: return

        capture.takePicture(
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(imageProxy: ImageProxy) {
                    val bitmap = imageProxyToBitmap(imageProxy)
                    imageProxy.close()

                    val results = detector.detect(bitmap,confidenceThreshold = 0.3f)
                    val filtered = results.filterNot { it.classId == 0 || it.classId == 1 || it.classId == 2}
                        .maxByOrNull { it.confidence }

                    //val results = detector.detect(bitmap)
                    if (results.isNotEmpty()) {
                        wasteGuideText = results[0].guide
                        wasteCapturedBitmap = bitmap
                        navController.navigate("waste_guide")
                    } else {
                        wasteGuideText = "사진을 인식하지 못했습니다.\n다시 촬영해주세요."
                        wasteCapturedBitmap = bitmap
                        navController.navigate("waste_guide")
                    }
                }
                override fun onError(exception: ImageCaptureException) {
                    wasteCapturedBitmap = null
                    wasteGuideText = "촬영에 실패했습니다.\n다시 시도해주세요."
                    navController.navigate("waste_guide")
                    Log.e("Camera", "촬영 실패", exception)
                }
            }
        )
    }

    private fun imageProxyToBitmap(imageProxy: ImageProxy): Bitmap {
        val buffer: ByteBuffer = imageProxy.planes[0].buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)
        val originalBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

        // 📌 왼쪽으로 90도 회전 (반시계 방향)
        val matrix = Matrix().apply {
            postRotate(90f)
        }

        return Bitmap.createBitmap(
            originalBitmap,
            0, 0,
            originalBitmap.width,
            originalBitmap.height,
            matrix,
            true
        )
    }

    fun takeLabelPhoto(navController: NavHostController) {
        if (!::imageCapture.isInitialized) {
            Log.w("Camera", "imageCapture not initialized")
            Toast.makeText(this, "카메라 준비 중입니다. 잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        val capture = imageCapture ?: return

        capture.takePicture(
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(imageProxy: ImageProxy) {
                    val bitmap = imageProxyToBitmap(imageProxy)
                    imageProxy.close()

                    labelDetector.process(
                        bitmap,
                        onResult = { resultBitmap, guideText ->
                            labelCapturedBitmap = resultBitmap
                            labelGuideText = guideText
                            navController.navigate("recycle_sign_guide")
                        },
                        onError = { resultBitmap, guideText ->
                            labelCapturedBitmap = resultBitmap
                            labelGuideText = guideText
                            navController.navigate("recycle_sign_guide")
                        }
                    )
                }

                override fun onError(exception: ImageCaptureException) {
                    labelCapturedBitmap = null
                    labelGuideText = "촬영에 실패했습니다.\n다시 시도해주세요."
                    navController.navigate("recycle_sign_guide")
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