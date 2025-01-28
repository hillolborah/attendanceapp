package com.example.attendanceapp.student_management

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.foundation.clickable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.attendanceapp.attendance.AttendanceDatabase
import com.example.attendanceapp.course.CourseEntity
import com.example.attendanceapp.student.StudentEntity
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.attendanceapp.course.CourseViewModel
import com.example.attendanceapp.course.CourseViewModelFactory
import com.example.attendanceapp.student.StudentViewModel


// Define the Student data class
//data class Student(val name: String, val enrollmentNumber: String)

@Composable
fun StudentManagementScreen(navController: NavHostController, database: AttendanceDatabase) {
    // Get the CourseViewModel instance
    val courseViewModel: CourseViewModel = viewModel(
        factory = CourseViewModelFactory(database.courseDao())
    )

    // Get StudentViewModel instance
    val studentViewModel: StudentViewModel = viewModel(
        factory = StudentViewModel.Factory(database.studentDao())
    )

    // Observe courses and selectedCourse
    val courses by courseViewModel.courseList.collectAsState()
    val selectedCourse by courseViewModel.selectedCourse.collectAsState()

    // Observe students from StudentViewModel
    val students by studentViewModel.students.collectAsState()

    var showAddStudentDialog by remember { mutableStateOf(false) }
    var showAddCourseDialog by remember { mutableStateOf(false) }
    var studentToEdit by remember { mutableStateOf<StudentEntity?>(null) }
    var showEditStudentDialog by remember { mutableStateOf(false) }


    // Coroutine scope for async operations
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Course Dropdown Row
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Course: ", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.width(8.dp))
            CourseDropdownMenu(courseViewModel = courseViewModel, studentViewModel = studentViewModel) { course ->
                // Update selected course in CourseViewModel
                courseViewModel.fetchCourseByCode(course.courseCode)
                // Fetch students for the selected course
                studentViewModel.fetchStudents(course.courseCode)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Add Course Button Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(onClick = { showAddCourseDialog = true }) { // Show Add Course Dialog
                Text("Add Course")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Add Student Button
        Button(
            onClick = { showAddStudentDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Student")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Student List
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(students.size) { index ->
                val student = students[index]
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${student.name} - ${student.enrollmentNumber}",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f)
                    )
                    Row {
                        // Edit Button
                        IconButton(onClick = {
                            studentToEdit = student
                            showEditStudentDialog = true
                        }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                        }

                        // Delete Button
                        IconButton(onClick = {
                            studentViewModel.deleteStudent(student.enrollmentNumber, student.courseCode)
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                        }
                    }
                }
            }
        }
    }


    // Add Student Dialog
    if (showAddStudentDialog) {
        AddStudentDialog(
            courseViewModel = courseViewModel,
            studentViewModel = studentViewModel,
            onDismiss = { showAddStudentDialog = false }
        )
    }



// Edit Student Dialog
    if (showEditStudentDialog && studentToEdit != null) {
        EditStudentDialog(
            courseViewModel = courseViewModel,  // Pass the courseViewModel here
            student = studentToEdit!!,
            onDismiss = { showEditStudentDialog = false },
            onUpdateStudent = { updatedStudent ->
                studentViewModel.updateStudent(updatedStudent)
                showEditStudentDialog = false
            }
        )
    }


// Add Course Dialog
    if (showAddCourseDialog) {
        AddCourseDialog(
            courseViewModel = courseViewModel, // Pass the ViewModel here
            onDismiss = { showAddCourseDialog = false },
            onAddCourse = { courseEntity ->
                if (courseEntity.courseCode.isNotBlank()) {
                    // Add the new CourseEntity to the list via ViewModel
                    courseViewModel.addOrUpdateCourse(courseEntity)
                }
                showAddCourseDialog = false
            }
        )
    }

}

@Composable
fun CourseDropdownMenu(courseViewModel: CourseViewModel, studentViewModel: StudentViewModel, onCourseSelected: (CourseEntity) -> Unit) {
    // Observe the courses and selectedCourse from CourseViewModel
    val courses by courseViewModel.courseList.collectAsState()
    val selectedCourse by courseViewModel.selectedCourse.collectAsState()

    var expanded by remember { mutableStateOf(false) }

    // Observe the students related to the selected course
    LaunchedEffect(selectedCourse) {
        selectedCourse?.courseCode?.let {
            studentViewModel.fetchStudents(it) // Fetch students when course is selected
        }
    }

    Box(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Selected Course: ${selectedCourse?.courseCode ?: "None"}",
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(16.dp)
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            courses.forEach { course ->
                DropdownMenuItem(
                    text = { Text(course.courseCode) },
                    onClick = {
                        courseViewModel.selectCourse(course) // Update selected course in ViewModel
                        onCourseSelected(course) // Notify the parent about the selected course
                        expanded = false
                    }
                )
            }
        }
    }
}


@Composable
fun AddStudentDialog(
    courseViewModel: CourseViewModel,
    studentViewModel: StudentViewModel,
    onDismiss: () -> Unit
) {
    // Local states for the student details
    var name by remember { mutableStateOf("") }
    var enrollmentNumber by remember { mutableStateOf("") }

    // Observe selectedCourse from CourseViewModel
    val selectedCourse by courseViewModel.selectedCourse.collectAsState()

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Add Student", style = MaterialTheme.typography.headlineSmall)

                // Input fields for name and enrollment number
                BasicTextField(
                    value = name,
                    onValueChange = { name = it },
                    modifier = Modifier.fillMaxWidth(),
                    decorationBox = { innerTextField ->
                        Box(
                            Modifier
                                .padding(4.dp)
                                .fillMaxWidth()
                        ) {
                            if (name.isEmpty()) Text("Enter name", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            innerTextField()
                        }
                    }
                )
                BasicTextField(
                    value = enrollmentNumber,
                    onValueChange = { enrollmentNumber = it },
                    modifier = Modifier.fillMaxWidth(),
                    decorationBox = { innerTextField ->
                        Box(
                            Modifier
                                .padding(4.dp)
                                .fillMaxWidth()
                        ) {
                            if (enrollmentNumber.isEmpty()) Text("Enter enrollment number", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            innerTextField()
                        }
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Action buttons
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Cancel Button
                    TextButton(onClick = onDismiss) { Text("Cancel") }

                    // Add Button
                    TextButton(
                        onClick = {
                            selectedCourse?.courseCode?.let { courseCode ->
                                val student = StudentEntity(
                                    enrollmentNumber = enrollmentNumber,
                                    name = name,
                                    courseCode = courseCode
                                )
                                studentViewModel.addStudent(student)
                                onDismiss() // Close the dialog after adding
                            }
                        },
                        enabled = selectedCourse != null && name.isNotEmpty() && enrollmentNumber.isNotEmpty()
                    ) {
                        Text("Add")
                    }

                }
            }
        }
    }
}


@Composable
fun EditStudentDialog(
    student: StudentEntity,
    courseViewModel: CourseViewModel, // Add courseViewModel parameter here
    onDismiss: () -> Unit,
    onUpdateStudent: (StudentEntity) -> Unit
) {
    var name by remember { mutableStateOf(student.name) }
    var enrollmentNumber by remember { mutableStateOf(student.enrollmentNumber) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Edit Student", style = MaterialTheme.typography.headlineSmall)
                BasicTextField(
                    value = name,
                    onValueChange = { name = it },
                    modifier = Modifier.fillMaxWidth(),
                    decorationBox = { innerTextField ->
                        Box(
                            Modifier
                                .padding(4.dp)
                                .fillMaxWidth()
                        ) {
                            if (name.isEmpty()) Text("Enter name", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            innerTextField()
                        }
                    }
                )
                BasicTextField(
                    value = enrollmentNumber,
                    onValueChange = { enrollmentNumber = it },
                    modifier = Modifier.fillMaxWidth(),
                    decorationBox = { innerTextField ->
                        Box(
                            Modifier
                                .padding(4.dp)
                                .fillMaxWidth()
                        ) {
                            if (enrollmentNumber.isEmpty()) Text("Enter enrollment number", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            innerTextField()
                        }
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    TextButton(onClick = {
                        val updatedStudent = student.copy(name = name, enrollmentNumber = enrollmentNumber)
                        onUpdateStudent(updatedStudent)
                    }) { Text("Update") }
                }
            }
        }
    }
}


@Composable
fun AddCourseDialog(
    courseViewModel: CourseViewModel, // Accept the ViewModel
    onDismiss: () -> Unit, // Function to dismiss the dialog
    onAddCourse: (CourseEntity) -> Unit // Function to handle adding the course
) {
    var courseName by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Add Course", style = MaterialTheme.typography.headlineSmall)

                // Course Name Input Field
                BasicTextField(
                    value = courseName,
                    onValueChange = { courseName = it },
                    modifier = Modifier.fillMaxWidth(),
                    decorationBox = { innerTextField ->
                        Box(
                            Modifier
                                .padding(4.dp)
                                .fillMaxWidth()
                        ) {
                            if (courseName.isEmpty()) Text("Enter course name", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            innerTextField()
                        }
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    TextButton(onClick = {
                        val newCourse = CourseEntity(courseCode = courseName)
                        courseViewModel.addOrUpdateCourse(newCourse)  // Add the course using ViewModel
                        onDismiss()  // Close the dialog
                    }) { Text("Add") }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    val navController = rememberNavController()

    // Create an in-memory database for preview
    val context = LocalContext.current
    val database = Room.inMemoryDatabaseBuilder(
        context,
        AttendanceDatabase::class.java
    ).build()

    // Pass only the database and NavController to the composable
    StudentManagementScreen(
        navController = navController,
        database = database
    )
}



