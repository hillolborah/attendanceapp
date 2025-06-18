package com.example.attendanceapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.attendanceapp.attendance.* // Import the necessary classes
import com.example.attendanceapp.course.CourseEntity
import com.example.attendanceapp.dataexport.DataExportScreen
import com.example.attendanceapp.student_management.StudentManagementScreen
import com.example.attendanceapp.ui.theme.AttendanceappTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize the database
        DatabaseInstance.initialize(applicationContext)

        // Get the database instance
        val database = DatabaseInstance.getDatabase() // Now we can get the database instance

        // Pass the database instance to your composable
        setContent {
            AttendanceappTheme {
                val navController = rememberNavController()
                MainApp(navController, database) // Pass database to the MainApp composable
            }
        }
    }
}

@Composable
fun MainApp(navController: NavHostController, database: AttendanceDatabase) {
    var selectedTab by remember { mutableIntStateOf(0) }

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
            composable("student_management") { StudentManagementScreen(navController, database) }
            composable("attendance_start") { AttendanceStartScreen(navController, database) }
            composable("attendance/{course}/{date}") { backStackEntry ->
                val courseCode = backStackEntry.arguments?.getString("course") ?: ""
                val date = backStackEntry.arguments?.getString("date") ?: ""

                // Create a state to hold the course
                val course = remember { mutableStateOf<CourseEntity?>(null) }

                // Fetch the CourseEntity from the database when the composable is first launched
                LaunchedEffect(courseCode) {
                    // Launch a coroutine to fetch the course data
                    course.value = database.courseDao().getCourseByCode(courseCode)
                }

                // When course is fetched, navigate to the AttendanceScreen
                course.value?.let { fetchedCourse ->
                    AttendanceScreen(navController, fetchedCourse, date, database)
                } ?: run {
                    // Handle the case where the course was not found (e.g., show an error message or navigate back)
                    Log.e("MainApp", "Course with code $courseCode not found.")
                }
            }


            composable("data_export") { DataExportScreen(navController, database) }
        }
    }
}

@Composable
fun BottomNavigationBar(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    val items = listOf(
        Triple("Student Management", Icons.Default.Person, "student_management"),
        Triple("Attendance", Icons.AutoMirrored.Filled.List, "attendance_start"),
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
        val fakeDatabase = DatabaseInstance.getDatabase() // Mock database for preview
        MainApp(navController, fakeDatabase) // Previewing the MainApp
    }
}