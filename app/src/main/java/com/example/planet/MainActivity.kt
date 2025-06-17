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
import com.example.planet.data.QuizRepository
import com.example.planet.data.QuizUploader
import com.example.planet.ui.QuizMatchingAnswerScreen
import com.example.planet.utils.UserStateManager

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

        UserStateManager.initialize(this)

        detector = Yolov8sDetector(this)
        labelDetector = LabelDetector(this)
        cameraExecutor = Executors.newSingleThreadExecutor()

        setContent {
            val navController = rememberNavController()
            var showSplash by remember { mutableStateOf(true) }
            var startDestination by remember { mutableStateOf("login") }
            var isCheckingAuth by remember { mutableStateOf(true) }
            val context = LocalContext.current
            var fullQuizList by remember { mutableStateOf<List<QuizItem>>(emptyList()) }

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
                QuizRepository.fetchAllChapter1Quizzes { fetched ->
                    fullQuizList = fetched
                }
            }

            MaterialTheme {
                if (showSplash || isCheckingAuth) {
                    SplashScreen()
                } else {
                    Scaffold(
                        containerColor = Color.Transparent, //배경 투명하게
                        bottomBar = {
                            val currentRoute = getCurrentRoute(navController)
                            // 카메라와 퀴즈 관련 화면에서 네비게이션 숨김
                            if (currentRoute != "camera" &&
                                !currentRoute.startsWith("quiz_")) {
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
                            composable(
                                "welcome/{userId}",
                                arguments = listOf(navArgument("userId") { type = NavType.StringType })
                            ) { backStackEntry ->
                                val userId = backStackEntry.arguments?.getString("userId")
                                WelcomeScreen(navController, userId)
                            }
                            composable("home") { HomeScreen(navController) }
                            composable("quiz") { StudyQuizPage(navController) }
                            composable("rank") { LeaderboardScreen(navController) }
                            composable("mypage") { Mypage(navController) }
                            composable("setting") { SettingScreen(navController) }
                            composable("camera") { CameraScreen(navController, this@MainActivity) }
                            composable("recycle_sign_guide") { RecycleSignGuide(navController, guideText = labelGuideText) }
                            composable("waste_guide") { GuideResultScreen(navController, guideText = wasteGuideText) }

                            composable(
                                route = "quiz_question/{index}",
                                arguments = listOf(navArgument("index") { type = NavType.IntType })
                            ) { backStackEntry ->
                                val index = backStackEntry.arguments?.getInt("index") ?: 0
                                QuizQuestionScreen(navController, quizList = fullQuizList, index = index)
                            }

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

                                if (fullQuizList.isNotEmpty()) {
                                    val quiz = fullQuizList[index]
                                    if (quiz.type == QuizType.MATCHING) {
                                        // 매칭형은 전체 리스트 넘기고, 결과 화면에서 매칭 로직 따로 처리할 수도 있음
                                        navController.navigate("quiz_result") // 매칭형은 해설 화면 없이 결과로 직행하도록 설계 가능
                                    } else {
                                        QuizAnswerScreen(
                                            navController = navController,
                                            quizList = fullQuizList,
                                            index = index,
                                            userAnswer = userAnswer
                                        )
                                    }
                                }
                            }
                            // NavHost에 매칭 해설 라우트 추가
                            composable(
                                route = "quiz_matching_answer/{index}?results={results}&quizIds={quizIds}",
                                arguments = listOf(
                                    navArgument("index") { type = NavType.IntType },
                                    navArgument("results") { type = NavType.StringType; defaultValue = "" },
                                    navArgument("quizIds") { type = NavType.StringType; defaultValue = "" }
                                )
                            ) { backStackEntry ->
                                val index = backStackEntry.arguments?.getInt("index") ?: 0
                                val resultsString = backStackEntry.arguments?.getString("results") ?: ""
                                val quizIdsString = backStackEntry.arguments?.getString("quizIds") ?: ""

                                // 매칭 결과 파싱
                                val matchedPairs = if (resultsString.isNotBlank()) {
                                    try {
                                        val decoded = java.net.URLDecoder.decode(resultsString, "UTF-8")
                                        decoded.split(",").associate { pair ->
                                            val parts = pair.split("|||")
                                            if (parts.size == 2) {
                                                parts[0] to parts[1]
                                            } else {
                                                "" to ""
                                            }
                                        }.filterKeys { it.isNotBlank() }
                                    } catch (e: Exception) {
                                        Log.e("MainActivity", "매칭 결과 파싱 실패", e)
                                        emptyMap()
                                    }
                                } else {
                                    emptyMap()
                                }
                                val quizIds = if (quizIdsString.isNotBlank()) {
                                    try {
                                        val decoded = java.net.URLDecoder.decode(quizIdsString, "UTF-8")
                                        decoded.split(",")
                                    } catch (e: Exception) {
                                        emptyList()
                                    }
                                } else {
                                    emptyList()
                                }
                                QuizMatchingAnswerScreen(navController, fullQuizList, index, matchedPairs, quizIds)
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
fun QuizQuestionScreen(navController: NavHostController, quizList: List<QuizItem>, index: Int) {
    val quiz = quizList.getOrNull(index) ?: return
    when (quiz.type) {
        QuizType.OX -> QuizOXQuestionScreen(navController, quizList, index)
        QuizType.SUBJECTIVE -> QuizSubjectiveQuestionScreen(navController, quizList, index)
        QuizType.MATCHING -> QuizMatchingQuestionScreen(navController, quizList, index)
        QuizType.MULTIPLE_CHOICE -> QuizMultipleChoiceQuestionScreen(navController, quizList, index)
    }
}

@Composable
fun getCurrentRoute(navController: NavHostController): String {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route ?: "home"
}