package com.example.attendanceapp.course

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CourseViewModel(private val courseDao: CourseDao) : ViewModel() {

    // StateFlow to observe course list changes
    private val _courseList = MutableStateFlow<List<CourseEntity>>(emptyList())
    val courseList: StateFlow<List<CourseEntity>> = _courseList

    // StateFlow for a single course (used when fetching details)
    private val _selectedCourse = MutableStateFlow<CourseEntity?>(null)
    val selectedCourse: StateFlow<CourseEntity?> = _selectedCourse

    // Fetch courses
    private fun fetchCourses() {
        viewModelScope.launch {
            _courseList.value = courseDao.getAllCourses()
        }
    }

    // Select a course (update selected course)
    fun selectCourse(course: CourseEntity) {
        _selectedCourse.value = course
    }

    init {
        // Automatically fetch all courses when the ViewModel is created
        fetchAllCourses()
    }

    // Insert or update a course
    fun addOrUpdateCourse(course: CourseEntity) {
        viewModelScope.launch {
            courseDao.insertCourse(course)
            fetchCourses() // Refresh the course list
        }
    }

//    // Delete a course
//    fun deleteCourse(course: CourseEntity) {
//        viewModelScope.launch {
//            courseDao.deleteCourse(course)
//            fetchCourses() // Refresh the course list
//        }
//    }

    // Fetch all courses
    private fun fetchAllCourses() {
        viewModelScope.launch {
            _courseList.value = courseDao.getAllCourses()
        }
    }

    // Fetch a course by its code
    fun fetchCourseByCode(courseCode: String) {
        viewModelScope.launch {
            _selectedCourse.value = courseDao.getCourseByCode(courseCode)
        }
    }

    // Flow-based real-time updates for courses
    val allCoursesFlow: Flow<List<CourseEntity>> = courseDao.getAllCoursesAsFlow()
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
