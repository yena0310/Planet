package com.example.planet.ui

import android.content.Context
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavHostController
import com.example.planet.MainActivity
import com.example.planet.R
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState

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
                CameraPreview(
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

@Composable//-->카메라페이지
fun CameraScreen(navController: NavHostController, mainActivity: MainActivity) {
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
                    mainActivity.processDummyLabelImage(navController) // 테스트 이미지
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
fun CameraPreview(
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
            val preview = Preview.Builder().build()
            preview.setSurfaceProvider(previewView.surfaceProvider)
            val imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

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
