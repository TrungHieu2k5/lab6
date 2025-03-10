package com.example.lab6

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lab6.ui.theme.Lab6Theme
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class CourseDetailsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Lab6Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Danh sách khóa học được lưu trữ trong state để cập nhật giao diện khi dữ liệu thay đổi
                    val courseList = remember { mutableStateListOf<Course?>() }
                    val context = LocalContext.current

                    // Gọi Firestore để lấy dữ liệu khi Composable này được tạo
                    LaunchedEffect(Unit) {
                        fetchCoursesFromFirebase(courseList, context)
                    }

                    // Hiển thị danh sách khóa học từ Firebase
                    firebaseUI(context, courseList)
                }
            }
        }
    }
}

// Hàm lấy danh sách khóa học từ Firestore
fun fetchCoursesFromFirebase(courseList: SnapshotStateList<Course?>, context: android.content.Context) {
    val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    db.collection("Courses").get()
        .addOnSuccessListener { queryDocumentSnapshots ->
            if (!queryDocumentSnapshots.isEmpty) {
                courseList.clear() // Xóa danh sách cũ trước khi cập nhật dữ liệu mới
                for (document in queryDocumentSnapshots.documents) {
                    val course: Course? = document.toObject(Course::class.java)
                    courseList.add(course) // Thêm từng khóa học vào danh sách
                }
            } else {
                Toast.makeText(context, "Không có dữ liệu trong cơ sở dữ liệu", Toast.LENGTH_SHORT).show()
            }
        }
        .addOnFailureListener {
            Toast.makeText(context, "Lấy dữ liệu thất bại.", Toast.LENGTH_SHORT).show()
        }
}

// Composable hiển thị danh sách khóa học
@Composable
fun firebaseUI(context: android.content.Context, courseList: SnapshotStateList<Course?>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White), // Đặt màu nền trắng
        verticalArrangement = Arrangement.Top, // Căn trên cùng
        horizontalAlignment = Alignment.CenterHorizontally // Căn giữa theo chiều ngang
    ) {
        // Sử dụng LazyColumn để hiển thị danh sách khóa học
        LazyColumn {
            itemsIndexed(courseList) { index, item ->
                CourseCard(context, item) // Hiển thị từng khóa học
            }
        }
    }
}

// Composable hiển thị thông tin của một khóa học dưới dạng Card
@Composable
fun CourseCard(context: android.content.Context, course: Course?) {
    Card(
        onClick = {
            // Hiển thị thông báo khi người dùng nhấn vào khóa học
            Toast.makeText(context, "${course?.courseName} được chọn.", Toast.LENGTH_SHORT).show()
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp), // Thêm padding để dễ nhìn hơn
        elevation = CardDefaults.cardElevation(6.dp) // Đổ bóng cho Card
    ) {
        Column(
            modifier = Modifier.padding(8.dp) // Khoảng cách giữa nội dung và viền Card
        ) {
            // Hiển thị tên khóa học nếu có
            course?.courseName?.let {
                Text(
                    text = it,
                    modifier = Modifier.padding(4.dp),
                    color = Color.Blue,
                    textAlign = TextAlign.Center,
                    style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold)
                )
            }

            // Hiển thị thời gian khóa học nếu có
            course?.courseDuration?.let {
                Text(
                    text = it,
                    modifier = Modifier.padding(4.dp),
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    style = TextStyle(fontSize = 15.sp)
                )
            }

            // Hiển thị mô tả khóa học nếu có
            course?.courseDescription?.let {
                Text(
                    text = it,
                    modifier = Modifier.padding(4.dp),
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    style = TextStyle(fontSize = 15.sp)
                )
            }
        }
    }
}

// Hàm thêm khóa học vào Firestore
fun addDataToFirebase(courseName: String, courseDuration: String, courseDescription: String, context: android.content.Context) {
    val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    val dbCourses: CollectionReference = db.collection("Courses")

    // Tạo một đối tượng khóa học mới
    val course = Course(courseName, courseDescription, courseDuration)

    // Thêm khóa học vào Firestore
    dbCourses.add(course)
        .addOnSuccessListener {
            Toast.makeText(context, "Thêm khóa học thành công", Toast.LENGTH_SHORT).show()
        }
        .addOnFailureListener { e ->
            Toast.makeText(context, "Thêm khóa học thất bại\n$e", Toast.LENGTH_SHORT).show()
        }
}
