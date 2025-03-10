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
                    val courseList = remember { mutableStateListOf<Course?>() }
                    val context = LocalContext.current

                    // Gọi Firestore để lấy dữ liệu
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

// Hàm lấy dữ liệu từ Firestore
fun fetchCoursesFromFirebase(courseList: SnapshotStateList<Course?>, context: android.content.Context) {
    val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    db.collection("Courses").get()
        .addOnSuccessListener { queryDocumentSnapshots ->
            if (!queryDocumentSnapshots.isEmpty) {
                courseList.clear() // Xóa dữ liệu cũ trước khi cập nhật
                for (document in queryDocumentSnapshots.documents) {
                    val course: Course? = document.toObject(Course::class.java)
                    courseList.add(course)
                }
            } else {
                Toast.makeText(context, "No data found in Database", Toast.LENGTH_SHORT).show()
            }
        }
        .addOnFailureListener {
            Toast.makeText(context, "Failed to fetch data.", Toast.LENGTH_SHORT).show()
        }
}

// Composable hiển thị danh sách khóa học
@Composable
fun firebaseUI(context: android.content.Context, courseList: SnapshotStateList<Course?>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyColumn {
            itemsIndexed(courseList) { index, item ->
                CourseCard(context, item)
            }
        }
    }
}

// Composable hiển thị từng khóa học
@Composable
fun CourseCard(context: android.content.Context, course: Course?) {
    Card(
        onClick = {
            Toast.makeText(context, "${course?.courseName} selected.", Toast.LENGTH_SHORT).show()
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            course?.courseName?.let {
                Text(
                    text = it,
                    modifier = Modifier.padding(4.dp),
                    color = Color.Blue,
                    textAlign = TextAlign.Center,
                    style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold)
                )
            }

            course?.courseDuration?.let {
                Text(
                    text = it,
                    modifier = Modifier.padding(4.dp),
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    style = TextStyle(fontSize = 15.sp)
                )
            }

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

    val course = Course(courseName, courseDescription, courseDuration)

    dbCourses.add(course).addOnSuccessListener {
        Toast.makeText(context, "Course added successfully", Toast.LENGTH_SHORT).show()
    }.addOnFailureListener { e ->
        Toast.makeText(context, "Failed to add course \n$e", Toast.LENGTH_SHORT).show()
    }
}

