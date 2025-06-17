package com.example.attendanceapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.attendanceapp.attendance.AttendanceScreen
import com.example.attendanceapp.attendance.AttendanceStartScreen
import com.example.attendanceapp.dataexport.DataExportScreen
import com.example.attendanceapp.student_management.StudentManagementScreen
import com.example.attendanceapp.ui.theme.AttendanceappTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AttendanceappTheme {
                val navController = rememberNavController()
                MainApp(navController)
            }
        }
    }
}

@Composable
fun MainApp(navController: NavHostController) {
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomNavigationBar(selectedTab) { newIndex ->
                selectedTab = newIndex
                when (newIndex) {
                    0 -> navController.navigate("student_management")
                    1 -> navController.navigate("attendance_start")
                    2 -> navController.navigate("data_export")
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "student_management",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("student_management") { StudentManagementScreen(navController) }
            composable("attendance_start") { AttendanceStartScreen(navController) }
            composable("attendance") { AttendanceScreen(navController) }

            composable("data_export") { DataExportScreen(navController) }
        }
    }
}

@Composable
fun BottomNavigationBar(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    val items = listOf(
        Triple("Student Management", Icons.Default.Person, "student_management"),
        Triple("Attendance", Icons.Default.List, "attendance_start"),
        Triple("Data Export", Icons.Default.Share, "data_export")
    )

    NavigationBar {
        items.forEachIndexed { index, (title, icon, _) ->
            NavigationBarItem(
                label = { Text(title) },
                icon = { Icon(imageVector = icon, contentDescription = title) },
                selected = selectedTab == index,
                onClick = { onTabSelected(index) }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainAppPreview() {
    AttendanceappTheme {
        val navController = rememberNavController() // Mock controller for preview
        MainApp(navController) // Previewing the MainApp
    }
}