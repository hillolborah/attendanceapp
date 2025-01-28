package com.example.attendanceapp.attendance

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.attendanceapp.utils.CSVExportHelper
import kotlinx.coroutines.launch

class AttendanceViewModel(private val attendanceDao: AttendanceDao) : ViewModel() {

    // Function to insert or update an attendance record
    fun markAttendance(
        courseCode: String,
        date: String,
        enrollmentNumber: String,
        status: String
    ) {
        viewModelScope.launch {
            val attendance = AttendanceEntity(
                courseCode = courseCode,
                date = date,
                enrollmentNumber = enrollmentNumber,
                status = status
            )
            attendanceDao.insertAttendance(attendance)
        }
    }

    // Function to fetch attendance for a specific course and date
    fun getAttendanceByCourseAndDate(
        courseCode: String,
        date: String,
        onResult: (List<AttendanceEntity>) -> Unit
    ) {
        viewModelScope.launch {
            val attendanceList = attendanceDao.getAttendanceByCourseAndDate(courseCode, date)
            onResult(attendanceList)
        }
    }

    // Function to fetch all attendance records for a specific course
    fun getAllAttendanceForCourse(
        courseCode: String,
        onResult: (List<AttendanceEntity>) -> Unit
    ) {
        viewModelScope.launch {
            val attendanceList = attendanceDao.getAllAttendanceForCourse(courseCode)
            onResult(attendanceList)
        }
    }

    // Function to fetch attendance for a specific student
    fun getAttendanceByStudent(
        enrollmentNumber: String,
        onResult: (List<AttendanceEntity>) -> Unit
    ) {
        viewModelScope.launch {
            val attendanceList = attendanceDao.getAttendanceByStudent(enrollmentNumber)
            onResult(attendanceList)
        }
    }

    // Function to fetch attendance for a specific student in a course
    fun getAttendanceForStudentInCourse(
        courseCode: String,
        enrollmentNumber: String,
        onResult: (List<AttendanceEntity>) -> Unit
    ) {
        viewModelScope.launch {
            val attendanceList =
                attendanceDao.getAttendanceForStudentInCourse(courseCode, enrollmentNumber)
            onResult(attendanceList)
        }
    }

    // Function to export attendance data to CSV
    fun exportAttendanceDataToCSV(
        context: Context,
        courseCode: String,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val attendanceList = attendanceDao.getAllAttendanceForCourse(courseCode)

                if (attendanceList.isNotEmpty()) {
                    // Extract headers and data
                    val headers = listOf("Course Code", "Date", "Enrollment Number", "Status")
                    val rows = attendanceList.map { record ->
                        listOf(
                            record.courseCode,
                            record.date,
                            record.enrollmentNumber,
                            record.status
                        )
                    }

                    // Use CSVExportHelper to export the data
                    val filePath = CSVExportHelper.exportAttendanceToCSV(
                        context = context,
                        fileName = "Attendance_${courseCode}",
                        headers = headers,
                        data = rows
                    )

                    onSuccess(filePath)
                } else {
                    onFailure("No attendance data available for export.")
                }
            } catch (e: Exception) {
                onFailure("Failed to export data: ${e.message}")
            }
        }
    }
}

class AttendanceViewModelFactory(private val attendanceDao: AttendanceDao) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AttendanceViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AttendanceViewModel(attendanceDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
