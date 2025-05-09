import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.planet.MainActivity
import com.example.planet.R
import androidx.compose.ui.graphics.asImageBitmap

@Composable
fun GuideResultScreen(
    navController: NavHostController,
    guideText: String
) {
    val pretendardsemibold = FontFamily(Font(R.font.pretendardsemibold))
    val guideText = MainActivity.wasteGuideText.ifBlank {
        "사진을 인식하지 못했습니다.\n다시 촬영해주세요 :("
    }
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
                        top = 40.dp, // ✅ 상단 여백 명시
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
                        val capturedBitmap = MainActivity.wasteCapturedBitmap
                        if (capturedBitmap != null) {
                            Image(
                                bitmap = capturedBitmap.asImageBitmap(), // ✅ 실제 촬영된 이미지 출력
                                contentDescription = "촬영 이미지",
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Image(
                                painter = ColorPainter(Color.LightGray),
                                contentDescription = "이미지 없음",
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }

                    // 🔹 문제 텍스트
                    Text(
                        text = guideText,
                        fontSize = 20.sp,
                        color = Color.Black,
                        fontFamily = pretendardsemibold,
                        textAlign = TextAlign.Center
                    )

                    // 🔹 점수 추가 텍스트
                    Text(
                        text = "+ 10 P",
                        fontSize = 16.sp,
                        color = Color.Gray,
                        fontFamily = pretendardsemibold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}