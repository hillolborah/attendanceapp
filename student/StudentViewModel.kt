package com.example.attendanceapp.student

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class StudentViewModel(private val studentDao: StudentDao) : ViewModel() {

    // Expose Flow directly from Room (Automatically updates UI)
    fun getStudentsByCourse(courseCode: String): Flow<List<StudentEntity>> =
        studentDao.getStudentsByCourseFlow(courseCode)

//    // Insert student
//    fun addStudent(student: StudentEntity) {
//        viewModelScope.launch(Dispatchers.IO) {
//            studentDao.insertStudent(student)
//        }
//    }

    fun addStudent(student: StudentEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            // First, check if the student already exists with the same enrollmentNumber in the same courseCode
            val existingStudent = studentDao.getStudentByEnrollmentAndCourse(student.enrollmentNumber, student.courseCode)

            if (existingStudent == null) {
                // No student exists with the same enrollmentNumber and courseCode, so proceed with the insert
                studentDao.insertStudent(student)
            } else {
                // Handle the case where the student already exists. You may show a message or take action accordingly
                Log.d("StudentViewModel", "Student already exists in this course.")
            }
        }
    }


    // Update student
    fun updateStudent(student: StudentEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            studentDao.updateStudent(student)
        }
    }

    // Delete student (Updated to require both enrollmentNumber and courseCode)
    fun deleteStudent(enrollmentNumber: String, courseCode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            studentDao.deleteStudent(enrollmentNumber, courseCode)
        }
    }

    // ✅ Add method to fetch a student by enrollmentNumber AND courseCode
    suspend fun getStudentByEnrollmentAndCourse(enrollmentNumber: String, courseCode: String): StudentEntity? {
        return studentDao.getStudentByEnrollmentAndCourse(enrollmentNumber, courseCode)
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


