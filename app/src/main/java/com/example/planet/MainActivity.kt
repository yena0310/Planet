package com.example.planet

// Screens
import com.example.planet.ui.SplashScreen
import com.example.planet.ui.LoginScreen
import com.example.planet.ui.HomeScreen
import com.example.planet.ui.StudyQuizPage
import com.example.planet.ui.QuizOXQuestionScreen
import com.example.planet.ui.QuizSubjectiveQuestionScreen
import com.example.planet.ui.QuizMatchingQuestionScreen
import com.example.planet.ui.QuizMultipleChoiceQuestionScreen
import com.example.planet.ui.CameraScreen
import com.example.planet.ui.RecycleSignGuide
import com.example.planet.ui.GuideResultScreen
import com.example.planet.ui.Mypage
import com.example.planet.ui.LeaderboardScreen
import com.example.planet.ui.BottomNavigationBar

// 이외 kt files
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
import androidx.compose.ui.Modifier
import java.nio.ByteBuffer

import androidx.navigation.navArgument
import androidx.navigation.NavType


class MainActivity : ComponentActivity() {

    public lateinit var imageCapture: ImageCapture
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
                            composable("camera") { CameraScreen(navController, this@MainActivity) }
                            composable("recycle_sign_guide") { RecycleSignGuide(navController, guideText = labelGuideText) }
                            composable("waste_guide") { GuideResultScreen(navController, guideText = wasteGuideText) }
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

                    val results = detector.detect(bitmap)
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
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
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
                        onError = { resultBitmap, _ ->
                            labelGuideText = "분리배출 표시를 인식하지 못했습니다.\n다시 촬영해주세요 :("
                            labelCapturedBitmap = resultBitmap
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

    // 테스트 이미지
    fun processDummyLabelImage(navController: NavHostController) {
        val dummyBitmap = BitmapFactory.decodeResource(resources, R.drawable.test9)

        labelDetector.process(
            dummyBitmap,
            onResult = { resultBitmap, guideText ->
                labelCapturedBitmap = resultBitmap
                labelGuideText = guideText
                navController.navigate("recycle_sign_guide")
            },
            onError = { resultBitmap, _ ->
                labelGuideText = "분리배출 표시를 인식하지 못했습니다.\n다시 촬영해주세요 :("
                labelCapturedBitmap = resultBitmap
                navController.navigate("recycle_sign_guide")
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

