package com.example.attendanceapp.attendance

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AttendanceDao {

    // Insert a single attendance record
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendance(attendance: AttendanceEntity)

    // Insert multiple attendance records
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendanceRecords(attendances: List<AttendanceEntity>)

    // Get attendance for a specific course and date
    @Query("SELECT * FROM attendance WHERE courseCode = :courseCode AND date = :date")
    suspend fun getAttendanceByCourseAndDate(courseCode: String, date: String): List<AttendanceEntity>

    // Get all attendance records for a specific course
    @Query("SELECT * FROM attendance WHERE courseCode = :courseCode")
    suspend fun getAllAttendanceForCourse(courseCode: String): List<AttendanceEntity>

//    // Get all attendance records for a specific student in a specific course
//    @Query("SELECT * FROM attendance WHERE enrollmentNumber = :enrollmentNumber AND courseCode = :courseCode")
//    suspend fun getAttendanceByStudent(enrollmentNumber: String, courseCode: String): List<AttendanceEntity>


    // Get attendance for a specific student in a course
    @Query("SELECT * FROM attendance WHERE courseCode = :courseCode AND enrollmentNumber = :enrollmentNumber")
    suspend fun getAttendanceForStudentInCourse(
        courseCode: String,
        enrollmentNumber: String
    ): List<AttendanceEntity>

    // Update the status for a specific student and date
    @Query("UPDATE attendance SET status = :status WHERE courseCode = :courseCode AND date = :date AND enrollmentNumber = :enrollmentNumber")
    suspend fun updateAttendanceStatus(
        courseCode: String,
        date: String,
        enrollmentNumber: String,
        status: String
    )

    // Delete all attendance records for a course
    @Query("DELETE FROM attendance WHERE courseCode = :courseCode")
    suspend fun deleteAttendanceByCourse(courseCode: String)

    @Query("DELETE FROM attendance WHERE enrollmentNumber = :enrollmentNumber AND courseCode = :courseCode")
    suspend fun deleteAttendanceByStudent(enrollmentNumber: String, courseCode: String)


    // Delete a specific attendance record
    @Query("DELETE FROM attendance WHERE courseCode = :courseCode AND date = :date AND enrollmentNumber = :enrollmentNumber")
    suspend fun deleteAttendanceRecord(
        courseCode: String,
        date: String,
        enrollmentNumber: String
    )
}

