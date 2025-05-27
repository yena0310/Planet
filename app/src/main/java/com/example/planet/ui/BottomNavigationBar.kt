package com.example.planet.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

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