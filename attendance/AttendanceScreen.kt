package com.example.attendanceapp.attendance

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController

@Composable
fun AttendanceScreen(navController: NavHostController) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Attendance Screen")
    }
}



/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

//package com.example.attendanceapp.attendance
//
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.navigation.NavHostController
//import androidx.navigation.compose.rememberNavController
//
//@Composable
//fun AttendanceScreen(navController: NavHostController, course: String, date: String) {
//    val studentList = remember {
//        mutableStateListOf("Student 1", "Student 2", "Student 3", "Student 4")
//    }
//    val attendanceMap = remember { mutableStateMapOf<String, Boolean>() }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp)
//    ) {
//        Text(text = "Course: $course", style = MaterialTheme.typography.titleLarge)
//        Text(text = "Date: $date", style = MaterialTheme.typography.titleMedium)
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        LazyColumn(modifier = Modifier.weight(1f)) {
//            items(studentList) { student ->
//                AttendanceRow(
//                    studentName = student,
//                    isPresent = attendanceMap[student] ?: false,
//                    onAttendanceChange = { isPresent -> attendanceMap[student] = isPresent }
//                )
//            }
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        Button(
//            onClick = {
//                navController.popBackStack()
//            },
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            Text("Save Attendance")
//        }
//    }
//}
//
//@Composable
//fun AttendanceRow(studentName: String, isPresent: Boolean, onAttendanceChange: (Boolean) -> Unit) {
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(vertical = 8.dp),
//        horizontalArrangement = Arrangement.SpaceBetween,
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        Text(text = studentName, style = MaterialTheme.typography.bodyLarge)
//        Row {
//            Button(
//                onClick = { onAttendanceChange(true) },
//                colors = ButtonDefaults.buttonColors(containerColor = if (isPresent) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.surface)
//            ) {
//                Text("Present")
//            }
//            Spacer(modifier = Modifier.width(8.dp))
//            Button(
//                onClick = { onAttendanceChange(false) },
//                colors = ButtonDefaults.buttonColors(containerColor = if (!isPresent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
//            ) {
//                Text("Absent")
//            }
//        }
//    }
//}
//
//@Composable
//@Preview(showBackground = true)
//fun AttendanceScreenPreview() {
//    val mockNavController = rememberNavController()
//    AttendanceScreen(
//        navController = mockNavController,
//        course = "Mathematics",
//        date = "2025-01-12"
//    )
//}

