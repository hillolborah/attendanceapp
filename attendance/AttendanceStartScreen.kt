package com.example.attendanceapp.attendance

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import java.text.SimpleDateFormat
import java.util.*
import com.example.attendanceapp.course.CourseEntity
import com.example.attendanceapp.course.CourseViewModel
import com.example.attendanceapp.course.CourseViewModelFactory
import com.example.attendanceapp.student.StudentViewModel


@Composable
fun AttendanceStartScreen(navController: NavHostController, database: AttendanceDatabase) {
    var selectedDate by remember { mutableStateOf(getCurrentDate()) }
    var showDatePicker by remember { mutableStateOf(false) }

    // Initialize CourseViewModel
    val courseDao = database.courseDao()
    val courseViewModel: CourseViewModel = viewModel(factory = CourseViewModelFactory(courseDao))

    // Observe course list from the ViewModel
    val courseList by courseViewModel.courseList.collectAsState(initial = emptyList())

    // Observe selected course from CourseViewModel
    val selectedCourse by courseViewModel.selectedCourse.collectAsState()

    // Initialize StudentViewModel
    val studentDao = database.studentDao()
    val studentViewModel: StudentViewModel = viewModel(factory = StudentViewModel.Factory(studentDao))

    val students by studentViewModel.getStudentsByCourse(selectedCourse?.courseCode ?: "")
        .collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Date Picker
        Text(text = "Select Date")
        Button(
            onClick = { showDatePicker = true },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(text = selectedDate)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Course Dropdown
        Text(text = "Select Course/Class")
        DropdownMenuExample(
            courseList = courseList,
            selectedCourse = selectedCourse,
            onCourseSelected = { course ->
                courseViewModel.selectCourse(course)  // Update CourseViewModel
            }
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Start Attendance Button
        Button(
            onClick = {
                when {
                    selectedCourse == null -> {
                        // Show error: "Please select a course"
                    }
                    selectedDate.isEmpty() -> {
                        // Show error: "Please select a date"
                    }
                    else -> {
                        navController.navigate("attendance/${selectedCourse!!.courseCode}/$selectedDate")
                    }
                }
            }
        ) {
            Text(text = "Start Attendance")
        }
    }

    // Date Picker Dialog
    if (showDatePicker) {
        DatePickerDialog(
            onDismiss = { showDatePicker = false },
            onDateSelected = { date ->
                selectedDate = date
                showDatePicker = false
            }
        )
    }
}



@Composable
fun DatePickerDialog(onDismiss: () -> Unit, onDateSelected: (String) -> Unit) {
    val calendar = Calendar.getInstance()
    var selectedDay by remember { mutableIntStateOf(calendar.get(Calendar.DAY_OF_MONTH)) }
    var selectedMonth by remember { mutableIntStateOf(calendar.get(Calendar.MONTH)) }
    var selectedYear by remember { mutableIntStateOf(calendar.get(Calendar.YEAR)) }

    Dialog(onDismissRequest = onDismiss) { // Use androidx.compose.ui.window.Dialog
        Surface(
            shape = MaterialTheme.shapes.medium,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Select Date", style = MaterialTheme.typography.headlineSmall)

                // Date Picker UI
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Day:")
                    BasicTextField(
                        value = TextFieldValue(selectedDay.toString()),
                        onValueChange = { value ->
                            selectedDay = value.text.toIntOrNull()?.coerceIn(1, 31) ?: selectedDay
                        },
                        modifier = Modifier.width(50.dp)
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Month:")
                    BasicTextField(
                        value = TextFieldValue((selectedMonth + 1).toString()), // Month is 0-based
                        onValueChange = { value ->
                            selectedMonth = (value.text.toIntOrNull()?.minus(1))?.coerceIn(0, 11) ?: selectedMonth
                        },
                        modifier = Modifier.width(50.dp)
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Year:")
                    BasicTextField(
                        value = TextFieldValue(selectedYear.toString()),
                        onValueChange = { value ->
                            selectedYear = value.text.toIntOrNull()?.coerceAtLeast(1900) ?: selectedYear
                        },
                        modifier = Modifier.width(70.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    TextButton(onClick = {
                        val formattedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
                        onDateSelected(formattedDate)
                    }) { Text("Select") }
                }
            }
        }
    }
}

@Composable
fun DropdownMenuExample(
    courseList: List<CourseEntity>,  // List of CourseEntity instead of List<String>
    selectedCourse: CourseEntity?,   // selectedCourse is now of type CourseEntity
    onCourseSelected: (CourseEntity) -> Unit  // onCourseSelected receives a CourseEntity
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Button(onClick = { expanded = true }) {
            Text(text = selectedCourse?.courseCode ?: "Select Course")  // Show courseCode if selectedCourse is not null
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            courseList.forEach { course ->
                DropdownMenuItem(
                    text = { Text(course.courseCode) },  // Display the courseCode from CourseEntity
                    onClick = {
                        onCourseSelected(course)  // Update the selected course
                        expanded = false
                    }
                )
            }
        }
    }
}

// Utility to get current date in "yyyy-MM-dd" format
fun getCurrentDate(): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return sdf.format(Date())
}

@Composable
@Preview(showBackground = true)
fun AttendanceStartScreenPreview() {
    val navController = rememberNavController()

    // Use a real context-based database in the preview
    val context = LocalContext.current
    val database = Room.databaseBuilder(
        context,
        AttendanceDatabase::class.java,
        "attendance_database"
    ).build()

    // Pass the actual database to the composable
    AttendanceStartScreen(navController = navController, database = database)
}

