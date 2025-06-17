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
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController


// Define the Student data class
data class Student(val name: String, val enrollmentNumber: String)

@Composable
fun StudentManagementScreen(navController: NavHostController) {
    var courses by remember { mutableStateOf(listOf("CS101", "MATH202", "PHY303")) }
    var selectedCourse by remember { mutableStateOf(courses.first()) }
    var students by remember { mutableStateOf(listOf<Student>()) }
    var showAddStudentDialog by remember { mutableStateOf(false) }
    var showAddCourseDialog by remember { mutableStateOf(false) }
    var studentToEdit by remember { mutableStateOf<Student?>(null) }
    var showEditStudentDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Course Dropdown Row
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Course: ", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.width(8.dp))
            CourseDropdownMenu(courses = courses, selectedCourse = selectedCourse) { selectedCourse = it }
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
                            students = students.filterNot { it == student }
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
            onDismiss = { showAddStudentDialog = false },
            onAddStudent = { name, enrollmentNumber ->
                students = students + Student(name, enrollmentNumber)
                showAddStudentDialog = false
            }
        )
    }

    // Edit Student Dialog
    if (showEditStudentDialog && studentToEdit != null) {
        EditStudentDialog(
            student = studentToEdit!!,
            onDismiss = { showEditStudentDialog = false },
            onUpdateStudent = { updatedStudent ->
                students = students.map {
                    if (it == studentToEdit) updatedStudent else it
                }
                showEditStudentDialog = false
            }
        )
    }

    // Add Course Dialog
    if (showAddCourseDialog) {
        AddCourseDialog(
            onDismiss = { showAddCourseDialog = false },
            onAddCourse = { courseName ->
                if (courseName.isNotBlank()) {
                    courses = courses + courseName // Add the new course to the list
                }
                showAddCourseDialog = false
            }
        )
    }
}

@Composable
fun CourseDropdownMenu(courses: List<String>, selectedCourse: String, onCourseSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Selected Course: $selectedCourse",
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

@Composable
fun AddStudentDialog(onDismiss: () -> Unit, onAddStudent: (String, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var enrollmentNumber by remember { mutableStateOf("") }

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
                        onAddStudent(name, enrollmentNumber)
                    }) { Text("Add") }
                }
            }
        }
    }
}

@Composable
fun EditStudentDialog(student: Student, onDismiss: () -> Unit, onUpdateStudent: (Student) -> Unit) {
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
                        onUpdateStudent(Student(name, enrollmentNumber))
                    }) { Text("Update") }
                }
            }
        }
    }
}

@Composable
fun AddCourseDialog(onDismiss: () -> Unit, onAddCourse: (String) -> Unit) {
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
                        onAddCourse(courseName)
                    }) { Text("Add") }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    StudentManagementScreen(navController = rememberNavController())
}
