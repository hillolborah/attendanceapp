package com.example.attendanceapp.dataexport

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.attendanceapp.attendance.AttendanceDatabase
import com.example.attendanceapp.attendance.AttendanceViewModel
import com.example.attendanceapp.attendance.AttendanceViewModelFactory
import com.example.attendanceapp.course.CourseEntity
import com.example.attendanceapp.course.CourseViewModel
import com.example.attendanceapp.course.CourseViewModelFactory

import android.widget.Toast



@Composable
fun DataExportScreen(
    navController: NavHostController,
    database: AttendanceDatabase
) {
    // Get ViewModels
    val courseViewModel: CourseViewModel = viewModel(factory = CourseViewModelFactory(database.courseDao()))
    val attendanceViewModel: AttendanceViewModel = viewModel(factory = AttendanceViewModelFactory(database.attendanceDao()))

    // Collect the list of courses from CourseViewModel
    val courses by courseViewModel.courseList.collectAsState(initial = emptyList())

    // Local state for UI management
    var selectedCourse by remember { mutableStateOf<CourseEntity?>(null) }
    val showConfirmation by remember { mutableStateOf(false) }
    var attendanceData by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Export Attendance Data",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Select a course to export.",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Dropdown for course selection
        CourseDropdown(courses = courses, selectedCourse = selectedCourse) { course ->
            selectedCourse = course
            course.let {
                attendanceViewModel.fetchAllAttendanceForCourse(it.courseCode) // ✅ Correct function call
            }
        }

// Observe attendance list from ViewModel
        val attendanceData by attendanceViewModel.attendanceList.collectAsState(initial = emptyList())

// Map AttendanceEntity to a list of maps
        val attendanceRecords = attendanceData.map { record ->
            mapOf(
                "courseCode" to record.courseCode,
                "date" to record.date,
                "enrollmentNumber" to record.enrollmentNumber,
                "status" to record.status
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Get context using LocalContext.current
        val context = LocalContext.current

// Observe the export status from the ViewModel
        val exportStatus by attendanceViewModel.exportStatus.collectAsState()

// Export button
        Button(
            onClick = {
                selectedCourse?.let {
                    // Call export function from AttendanceViewModel
                    attendanceViewModel.exportAttendanceDataToCSV(
                        context = context,
                        courseCode = it.courseCode
                    )
                }
            },
            enabled = selectedCourse != null && attendanceData.isNotEmpty(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Export as CSV")
        }

// Show a Toast based on export status
        LaunchedEffect(exportStatus) {
            exportStatus?.let { status ->
                Toast.makeText(
                    context,
                    status,
                    Toast.LENGTH_LONG
                ).show()
            }
        }


        // Confirmation message
        if (showConfirmation) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Export successful for course: ${selectedCourse?.courseCode}",
                color = MaterialTheme.colorScheme.primary
            )
        }

        // Debugging: Display attendance data
        if (attendanceData.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text("Fetched Attendance Data:")
            attendanceData.forEach { row ->
                Text(text = row.toString(), style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
fun CourseDropdown(
    courses: List<CourseEntity>,
    selectedCourse: CourseEntity?,
    onCourseSelected: (CourseEntity) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = selectedCourse?.courseCode ?: "Select Course")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            courses.forEach { course ->
                DropdownMenuItem(
                    text = { Text(course.courseCode) },
                    onClick = {
                        onCourseSelected(course)
                        expanded = false
                    }
                )
            }
        }
    }
}


@Composable
@Preview(showBackground = true)
fun DataExportScreenPreview() {
    val mockNavController = rememberNavController()

    // Mock database is still required, even if no data is fetched
    val mockDatabase = Room.inMemoryDatabaseBuilder(
        LocalContext.current,
        AttendanceDatabase::class.java
    ).build()

    // Pass the database to DataExportScreen
    DataExportScreen(
        navController = mockNavController,
        database = mockDatabase
    )
}

