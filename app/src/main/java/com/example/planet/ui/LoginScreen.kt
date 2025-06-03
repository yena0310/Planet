package com.example.planet.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.planet.R

// navController: NavHostController
// onLoginClick: () -> Unit = {}

//@Preview
@Composable
fun LoginScreen(
    onLoginClick: () -> Unit = {},
    navController: NavHostController
) {
    val pretendardsemibold = FontFamily(Font(R.font.pretendardsemibold))
    val pretendardextrabold = FontFamily(Font(R.font.pretendardsemibold))

    var school by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var selectedGrade by remember { mutableStateOf("") }
    var selectedClassNum by remember { mutableStateOf("") }
    var gradeExpanded by remember { mutableStateOf(false) }
    var classExpanded by remember { mutableStateOf(false) }

    val gradeOptions = listOf("1", "2", "3", "4", "5", "6")
    val classOptions = listOf("1", "2", "3", "4", "5")
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFCAEBF1))
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 40.dp, vertical = 50.dp)
            ) {
                Text(
                    text = "우리의 지구를 위해",
                    color = Color(0xff636363),
                    fontFamily = pretendardsemibold,
                    fontSize = 14.sp
                )
                Text(
                    text = "Planet",
                    fontFamily = pretendardextrabold,
                    fontSize = 50.sp,
                    modifier = Modifier.padding(top = 10.dp, bottom = 50.dp)
                )


                    // 학교 입력 + 검색 아이콘
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .border(1.dp, Color.Gray, RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            BasicTextField(
                                value = school,
                                onValueChange = { school = it },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                decorationBox = { innerTextField ->
                                    if (school.isEmpty()) {
                                        Text("학교", color = Color.Gray)
                                    }
                                    innerTextField()
                                }
                            )
                            IconButton(onClick = { /* TODO : 검색 기능 */ }) {
                                Icon(Icons.Default.Search, contentDescription = "검색")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 학년 드롭다운
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .border(1.dp, Color.Gray, RoundedCornerShape(12.dp))
                            .clickable { gradeExpanded = true }
                            .padding(horizontal = 12.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text(
                                text = if (selectedGrade.isNotEmpty()) selectedGrade else "학년",
                                color = if (selectedGrade.isNotEmpty()) Color.Black else Color.Gray
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                        }

                        DropdownMenu(
                            expanded = gradeExpanded,
                            onDismissRequest = { gradeExpanded = false }
                        ) {
                            gradeOptions.forEach {
                                DropdownMenuItem(
                                    onClick = {
                                        selectedClassNum = it
                                        classExpanded = false
                                    },
                                    content = {
                                        Text(it)
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 반 드롭다운
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .border(1.dp, Color.Gray, RoundedCornerShape(12.dp))
                            .clickable { classExpanded = true }
                            .padding(horizontal = 12.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text(
                                text = if (selectedClassNum.isNotEmpty()) selectedClassNum else "반",
                                color = if (selectedClassNum.isNotEmpty()) Color.Black else Color.Gray
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                        }

                        DropdownMenu(
                            expanded = classExpanded,
                            onDismissRequest = { classExpanded = false }
                        ) {
                            classOptions.forEach {
                                DropdownMenuItem(
                                    onClick = {
                                        selectedClassNum = it
                                        classExpanded = false
                                    },
                                    content = {
                                        Text(it)
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 이름 입력
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .border(1.dp, Color.Gray, RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        BasicTextField(
                            value = name,
                            onValueChange = { name = it },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            decorationBox = { innerTextField ->
                                if (name.isEmpty()) {
                                    Text("이름", color = Color.Gray)
                                }
                                innerTextField()
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(100.dp))

                    // 로그인 버튼
                    Button(
                        onClick = onLoginClick,
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xff18BEDD)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .height(48.dp),
                    ) {
                        Text("로그인", color = Color(0xFFFFFFFF), fontSize = 16.sp, fontFamily = pretendardsemibold)
                    }
                }
            }
        }
}
