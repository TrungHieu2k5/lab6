package com.example.lab6

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

import androidx.compose.foundation.layout.*

import androidx.compose.material3.TextField
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.Button
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.widget.Toast
import androidx.compose.foundation.background

class MainActivity : ComponentActivity() {
    @SuppressLint("UnrememberedMutableState")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Gọi hàm firebaseUI để hiển thị giao diện nhập liệu
            firebaseUI(LocalContext.current)
        }
    }
}

@Composable
fun firebaseUI(context: Context) {
    // Tạo các biến trạng thái để lưu trữ dữ liệu nhập vào từ người dùng
    val courseName = remember { mutableStateOf("") }
    val courseDuration = remember { mutableStateOf("") }
    val courseDescription = remember { mutableStateOf("") }

    // Hiển thị giao diện nhập liệu sử dụng Column
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .background(Color.White), // Đặt nền trắng
        verticalArrangement = Arrangement.Center, // Căn giữa theo chiều dọc
        horizontalAlignment = Alignment.CenterHorizontally // Căn giữa theo chiều ngang
    ) {
        // Ô nhập liệu cho tên khóa học
        TextField(
            value = courseName.value,
            onValueChange = { courseName.value = it },
            placeholder = { Text(text = "Nhập tên khóa học") },
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            textStyle = TextStyle(color = Color.Black, fontSize = 15.sp),
            singleLine = true,
        )

        Spacer(modifier = Modifier.height(10.dp)) // Khoảng cách giữa các ô nhập liệu

        // Ô nhập liệu cho thời gian khóa học
        TextField(
            value = courseDuration.value,
            onValueChange = { courseDuration.value = it },
            placeholder = { Text(text = "Nhập thời gian khóa học") },
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            textStyle = TextStyle(color = Color.Black, fontSize = 15.sp),
            singleLine = true,
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Ô nhập liệu cho mô tả khóa học
        TextField(
            value = courseDescription.value,
            onValueChange = { courseDescription.value = it },
            placeholder = { Text(text = "Nhập mô tả khóa học") },
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            textStyle = TextStyle(color = Color.Black, fontSize = 15.sp),
            singleLine = true,
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Nút thêm dữ liệu vào Firebase
        Button(
            onClick = {
                // Kiểm tra dữ liệu nhập vào có bị trống không
                if (TextUtils.isEmpty(courseName.value.toString())) {
                    Toast.makeText(context, "Vui lòng nhập tên khóa học", Toast.LENGTH_SHORT).show()
                } else if (TextUtils.isEmpty(courseDuration.value.toString())) {
                    Toast.makeText(context, "Vui lòng nhập thời gian khóa học", Toast.LENGTH_SHORT)
                        .show()
                } else if (TextUtils.isEmpty(courseDescription.value.toString())) {
                    Toast.makeText(context, "Vui lòng nhập mô tả khóa học", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    // Gọi hàm thêm dữ liệu vào Firebase
                    addDataToFirebase(
                        courseName.value,
                        courseDuration.value,
                        courseDescription.value,
                        context
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(text = "Thêm dữ liệu", modifier = Modifier.padding(8.dp))
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Nút chuyển sang màn hình xem danh sách khóa học
        Button(
            onClick = {
                // Mở Activity mới để xem danh sách khóa học
                context.startActivity(Intent(context, CourseDetailsActivity::class.java))
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(text = "Xem khóa học", modifier = Modifier.padding(8.dp))
        }
    }
}




