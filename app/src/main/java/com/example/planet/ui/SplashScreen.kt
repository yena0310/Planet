package com.example.planet.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import com.example.planet.R

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