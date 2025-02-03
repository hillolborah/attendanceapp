package com.example.attendanceapp.course

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CourseViewModel(private val courseDao: CourseDao) : ViewModel() {

    // Flow-based real-time updates for courses
    val courseList: Flow<List<CourseEntity>> = courseDao.getAllCoursesAsFlow()

    // StateFlow for a single selected course
    private val _selectedCourse = MutableStateFlow<CourseEntity?>(null)
    val selectedCourse: StateFlow<CourseEntity?> = _selectedCourse.asStateFlow()

    // Select a course
    fun selectCourse(course: CourseEntity) {
        _selectedCourse.value = course
    }

    // Insert or update a course
    fun addOrUpdateCourse(course: CourseEntity) {
        viewModelScope.launch(Dispatchers.IO) {  // Ensure DB operation runs on IO thread
            courseDao.insertCourse(course)
        }
    }

    fun fetchCourseByCode(courseCode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val course = courseDao.getCourseByCode(courseCode)
            withContext(Dispatchers.Main) {
                _selectedCourse.value = course
            }
        }
    }

}

// Factory for creating CourseViewModel instances
class CourseViewModelFactory(private val courseDao: CourseDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CourseViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CourseViewModel(courseDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

