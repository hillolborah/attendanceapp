package com.example.attendanceapp.attendance

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import com.example.attendanceapp.course.CourseEntity
import com.example.attendanceapp.course.CourseViewModel
import com.example.attendanceapp.course.CourseViewModelFactory
import com.example.attendanceapp.student.StudentEntity
import com.example.attendanceapp.student.StudentViewModel

@Composable
fun AttendanceScreen(
    navController: NavHostController,
    course: CourseEntity,  // Use the passed course directly
    date: String,
    database: AttendanceDatabase
) {
    // Initialize AttendanceViewModel
    val attendanceDao = database.attendanceDao()
    val attendanceViewModel: AttendanceViewModel = viewModel(factory = AttendanceViewModelFactory(attendanceDao))

    // Initialize StudentViewModel
    val studentDao = database.studentDao()
    val studentViewModel: StudentViewModel = viewModel(factory = StudentViewModel.Factory(studentDao))

    // Fetch students for the selected course
    val studentList by studentViewModel.getStudentsByCourse(course.courseCode)
        .collectAsState(initial = emptyList())

    // Use a composite key (enrollmentNumber, courseCode) for tracking attendance
    val attendanceMap = remember { mutableStateMapOf<Pair<String, String>, Boolean?>() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Course: ${course.courseCode}", style = MaterialTheme.typography.titleLarge)
        Text(text = "Date: $date", style = MaterialTheme.typography.titleMedium)

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(studentList) { student ->
                val key = student.enrollmentNumber to course.courseCode  // Use composite key
                AttendanceRow(
                    student = student,
                    isPresent = attendanceMap[key],
                    onAttendanceChange = { isPresent ->
                        // Update local state
                        attendanceMap[key] = isPresent

                        // Update attendance in ViewModel
                        attendanceViewModel.markAttendance(
                            courseCode = course.courseCode,
                            date = date,
                            enrollmentNumber = student.enrollmentNumber,
                            status = if (isPresent) "P" else "A"
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Attendance")
        }
    }
}



@Composable
fun AttendanceRow(student: StudentEntity, isPresent: Boolean?, onAttendanceChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = student.name, style = MaterialTheme.typography.bodyLarge)
        Row {
            Button(
                onClick = {
                    // Mark as present and update in the ViewModel
                    onAttendanceChange(true)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isPresent == true) Color.Green else MaterialTheme.colorScheme.surface
                )
            ) {
                Text("Present", color = Color.White)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    // Mark as absent and update in the ViewModel
                    onAttendanceChange(false)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isPresent == false) Color.Red else MaterialTheme.colorScheme.surface
                )
            ) {
                Text("Absent", color = Color.White)
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun AttendanceScreenPreview() {
    val mockNavController = rememberNavController()

    // Create an in-memory database for the preview
    val context = LocalContext.current
    val database = Room.inMemoryDatabaseBuilder(
        context,
        AttendanceDatabase::class.java
    ).build()

    // Initialize CourseViewModel
    val courseDao = database.courseDao()
    val courseViewModel: CourseViewModel = viewModel(factory = CourseViewModelFactory(courseDao))

    // Observe selected course from the ViewModel
    val selectedCourse by courseViewModel.selectedCourse.collectAsState()

    val courseToUse = selectedCourse ?: CourseEntity(courseCode = "DEFAULT001")

    // Pass the actual database and selected course to the screen
    AttendanceScreen(
        navController = mockNavController,
        course = courseToUse, // Use the selected course
        date = "2025-01-12",
        database = database // Pass the real database to use in the screen
    )
}
