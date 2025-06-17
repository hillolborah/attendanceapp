package com.example.attendanceapp.attendance

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AttendanceStartScreen(navController: NavHostController) {
    var selectedDate by remember { mutableStateOf(getCurrentDate()) }
    var selectedCourse by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
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
    var selectedDay by remember { mutableStateOf(calendar.get(Calendar.DAY_OF_MONTH)) }
    var selectedMonth by remember { mutableStateOf(calendar.get(Calendar.MONTH)) }
    var selectedYear by remember { mutableStateOf(calendar.get(Calendar.YEAR)) }

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
                        val formattedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
                        onDateSelected(formattedDate)
                    }) { Text("Select") }
                }
            }
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

@Composable
@Preview(showBackground = true)
fun AttendanceStartScreenPreview() {
    AttendanceStartScreen(navController = rememberNavController())
}
