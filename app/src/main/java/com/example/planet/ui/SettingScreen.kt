package com.example.planet.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.planet.R
import com.example.planet.utils.UserStateManager
import com.google.firebase.auth.FirebaseAuth

@Composable
fun SettingScreen(navController: NavHostController) {
    val pretendardBold = FontFamily(Font(R.font.pretendardbold))
    val auth = FirebaseAuth.getInstance()

    // 스위치 상태 관리
    var pushNotificationEnabled by remember { mutableStateOf(true) }
    var darkModeEnabled by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xffffffff))
            .padding(20.dp)
    ) {
        // 상단바 - 뒤로가기 버튼
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Rounded.ArrowBackIosNew,
                contentDescription = "뒤로가기",
                modifier = Modifier
                    .size(24.dp)
                    .clickable {
                        Log.d("SettingScreen", "뒤로가기 버튼 클릭")
                        navController.popBackStack()
                    },
                tint = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 푸시알림 설정
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xffF2F1F0))
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "푸시알림",
                    fontSize = 16.sp,
                    fontFamily = pretendardBold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.weight(1f))
                Switch(
                    checked = pushNotificationEnabled,
                    onCheckedChange = {
                        pushNotificationEnabled = it
                        Log.d("SettingScreen", "푸시알림 설정: $it")
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0xFF4FC3F7),
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color.Gray
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 다크모드 설정
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xffF2F1F0))
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "다크모드",
                    fontSize = 16.sp,
                    fontFamily = pretendardBold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.weight(1f))
                Switch(
                    checked = darkModeEnabled,
                    onCheckedChange = {
                        darkModeEnabled = it
                        Log.d("SettingScreen", "다크모드 설정: $it")
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0xFF4FC3F7),
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color.Gray
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 구분선
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color(0xFFE0E0E0))
        )

        // 문의하기
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    Log.d("SettingScreen", "문의하기 클릭")
                    // 문의하기 기능 구현 가능
                }
                .padding(vertical = 20.dp, horizontal = 20.dp)
        ) {
            Text(
                text = "문의하기",
                fontSize = 16.sp,
                fontFamily = pretendardBold,
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(0.dp))

        // 구분선
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color(0xFFE0E0E0))
        )

        // 로그아웃
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    Log.d("SettingScreen", "로그아웃 클릭")
                    // Firebase 로그아웃
                    auth.signOut()
                    // UserStateManager 클리어
                    UserStateManager.clearUser()
                    Log.d("SettingScreen", "로그아웃 완료, 로그인 화면으로 이동")
                    // 로그인 화면으로 이동하고 백스택 초기화
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
                .padding(vertical = 20.dp, horizontal = 20.dp)
        ) {
            Text(
                text = "로그아웃",
                fontSize = 16.sp,
                fontFamily = pretendardBold,
                color = Color.Black
            )
        }
    }
}