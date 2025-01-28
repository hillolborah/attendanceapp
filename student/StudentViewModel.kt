package com.example.attendanceapp.student

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class StudentViewModel(private val studentDao: StudentDao) : ViewModel() {
    // StateFlow for observing changes
    private val _students = MutableStateFlow<List<StudentEntity>>(emptyList())
    val students: StateFlow<List<StudentEntity>> = _students

    fun fetchStudents(courseCode: String) {
        viewModelScope.launch {
            _students.value = studentDao.getStudentsByCourse(courseCode)
        }
    }

    fun addStudent(student: StudentEntity) {
        viewModelScope.launch {
            studentDao.insertStudent(student)
            fetchStudents(student.courseCode)
        }
    }

    fun updateStudent(student: StudentEntity) {
        viewModelScope.launch {
            studentDao.updateStudent(student)
            fetchStudents(student.courseCode)
        }
    }

    fun deleteStudent(enrollmentNumber: String, courseCode: String) {
        viewModelScope.launch {
            studentDao.deleteStudent(enrollmentNumber)
            fetchStudents(courseCode)
        }
    }

    class Factory(private val studentDao: StudentDao) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(StudentViewModel::class.java)) {
                return StudentViewModel(studentDao) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
