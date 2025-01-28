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
fun AttendanceScreen(navController: NavHostController, course: CourseEntity, date: String, database: AttendanceDatabase) {
    // Initialize CourseViewModel
    val courseDao = database.courseDao()
    val courseViewModel: CourseViewModel = viewModel(factory = CourseViewModelFactory(courseDao))

    // Observe the selected course from the ViewModel
    val selectedCourse by courseViewModel.selectedCourse.collectAsState()

    // Initialize AttendanceViewModel
    val attendanceDao = database.attendanceDao()
    val attendanceViewModel: AttendanceViewModel = viewModel(factory = AttendanceViewModelFactory(attendanceDao))

    // Fetch students for the selected course
    val studentDao = database.studentDao()
    val studentViewModel: StudentViewModel = viewModel(factory = StudentViewModel.Factory(studentDao))

    // Fetch students for the selected course
    LaunchedEffect(selectedCourse?.courseCode) {
        selectedCourse?.courseCode?.let {
            studentViewModel.fetchStudents(it)
        }
    }

    // Observe the list of students
    val studentList by studentViewModel.students.collectAsState()

    // Initialize a map to track attendance status for each student
    val attendanceMap = remember { mutableStateMapOf<String, Boolean?>() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Course: ${selectedCourse?.courseCode}", style = MaterialTheme.typography.titleLarge)
        Text(text = "Date: $date", style = MaterialTheme.typography.titleMedium)

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(studentList) { student ->
                AttendanceRow(
                    student = student,
                    isPresent = attendanceMap[student.enrollmentNumber],
                    onAttendanceChange = { isPresent ->
                        // Update attendance in the ViewModel
                        attendanceMap[student.enrollmentNumber] = isPresent
                        attendanceViewModel.markAttendance(
                            courseCode = selectedCourse?.courseCode ?: "",
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
            onClick = {
                navController.popBackStack()
            },
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
                    // The ViewModel will handle saving attendance to the database
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isPresent == true) Color.Green else MaterialTheme.colorScheme.surface
                )
            ) {
                Text("Present", color = Color.Black)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    // Mark as absent and update in the ViewModel
                    onAttendanceChange(false)
                    // The ViewModel will handle saving attendance to the database
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isPresent == false) Color.Red else MaterialTheme.colorScheme.surface
                )
            ) {
                Text("Absent", color = Color.Black)
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

    // Ensure that a course is selected (you can choose a default course if necessary)
    val courseToUse = selectedCourse ?: CourseEntity(courseCode = "DEFAULT001")

    // Pass the actual database and selected course to the screen
    AttendanceScreen(
        navController = mockNavController,
        course = courseToUse, // Use the selected course
        date = "2025-01-12",
        database = database // Pass the real database to use in the screen
    )
}
