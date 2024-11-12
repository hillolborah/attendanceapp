package com.example.attendanceapp.attendance

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AttendanceStartScreen(navController: NavHostController) {
    var selectedDate by remember { mutableStateOf(getCurrentDate()) }
    var selectedTime by remember { mutableStateOf(getCurrentTime()) }
    var selectedCourse by remember { mutableStateOf("") }
    val courseList = listOf("Mathematics", "Physics", "Computer Science", "English") // Example courses

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Date Picker
        Text(text = "Select Date")
        BasicTextField(
            value = selectedDate,
            onValueChange = { selectedDate = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Time Picker
        Text(text = "Select Time")
        BasicTextField(
            value = selectedTime,
            onValueChange = { selectedTime = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Course Dropdown
        Text(text = "Select Course/Class")
        DropdownMenuExample(courseList, selectedCourse) { selectedCourse = it }

        Spacer(modifier = Modifier.height(32.dp))

        // Start Attendance Button
        Button(
            onClick = {
                navController.navigate("attendance") // Navigate to AttendanceScreen.kt
            }
        ) {
            Text(text = "Start Attendance")
        }
    }
}

@Composable
fun DropdownMenuExample(courseList: List<String>, selectedCourse: String, onCourseSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Button(onClick = { expanded = true }) {
            Text(text = if (selectedCourse.isEmpty()) "Select Course" else selectedCourse)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            courseList.forEach { course ->
                DropdownMenuItem(
                    text = { Text(course) },
                    onClick = {
                        onCourseSelected(course)
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

// Utility to get current time in "HH:mm" format
fun getCurrentTime(): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(Date())
}

@Composable
@Preview(showBackground = true)
fun AttendanceStartScreenPreview() {
    AttendanceStartScreen(navController = rememberNavController())
}
