package com.example.attendanceapp.attendance

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.attendanceapp.utils.CSVExportHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AttendanceViewModel(private val attendanceDao: AttendanceDao) : ViewModel() {

    private val _attendanceList = MutableStateFlow<List<AttendanceEntity>>(emptyList())
    val attendanceList: StateFlow<List<AttendanceEntity>> = _attendanceList.asStateFlow()

    private val _exportStatus = MutableStateFlow<String?>(null)
    val exportStatus: StateFlow<String?> = _exportStatus.asStateFlow()

    // Function to insert or update an attendance record
    fun markAttendance(courseCode: String, date: String, enrollmentNumber: String, status: String) {
        viewModelScope.launch {
            val attendance = AttendanceEntity(
                courseCode = courseCode,
                date = date,
                enrollmentNumber = enrollmentNumber,
                status = status
            )
            attendanceDao.insertAttendance(attendance)
            // Refresh the data after insertion
            fetchAttendanceByCourseAndDate(courseCode, date)
        }
    }

    // Function to fetch attendance for a specific course and date
    fun fetchAttendanceByCourseAndDate(courseCode: String, date: String) {
        viewModelScope.launch {
            _attendanceList.value = attendanceDao.getAttendanceByCourseAndDate(courseCode, date)
        }
    }

    // Function to fetch all attendance records for a specific course
    fun fetchAllAttendanceForCourse(courseCode: String) {
        viewModelScope.launch {
            _attendanceList.value = attendanceDao.getAllAttendanceForCourse(courseCode)
        }
    }

//    // Function to fetch attendance for a specific student
//    fun fetchAttendanceByStudent(enrollmentNumber: String, courseCode: String) {
//        viewModelScope.launch {
//            _attendanceList.value = attendanceDao.getAttendanceForStudentInCourse(courseCode, enrollmentNumber)
//        }
//    }



    // Function to export attendance data to CSV
    fun exportAttendanceDataToCSV(context: Context, courseCode: String) {
        viewModelScope.launch {
            try {
                val attendanceList = attendanceDao.getAllAttendanceForCourse(courseCode)

                if (attendanceList.isNotEmpty()) {
                    val headers = listOf("Course Code", "Date", "Enrollment Number", "Status")
                    val rows = attendanceList.map { record ->
                        listOf(
                            record.courseCode,
                            record.date,
                            record.enrollmentNumber,
                            record.status
                        )
                    }

                    val filePath = CSVExportHelper.exportAttendanceToCSV(
                        context = context,
                        fileName = "Attendance_${courseCode}",
                        headers = headers,
                        data = rows
                    )

                    _exportStatus.value = filePath
                } else {
                    _exportStatus.value = "No attendance data available for export."
                }
            } catch (e: Exception) {
                _exportStatus.value = "Failed to export data: ${e.message}"
            }
        }
    }
}

class AttendanceViewModelFactory(private val attendanceDao: AttendanceDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AttendanceViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AttendanceViewModel(attendanceDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

