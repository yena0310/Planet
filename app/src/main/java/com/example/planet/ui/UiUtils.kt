package com.example.planet.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

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

