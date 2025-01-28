package com.example.attendanceapp.attendance

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.attendanceapp.course.CourseEntity
import com.example.attendanceapp.student.StudentEntity
import com.example.attendanceapp.course.CourseDao
import com.example.attendanceapp.student.StudentDao

@Database(
    entities = [CourseEntity::class, StudentEntity::class, AttendanceEntity::class],
    version = 1, // Update the version if schema changes
    exportSchema = true
)
abstract class AttendanceDatabase : RoomDatabase() {
    abstract fun courseDao(): CourseDao
    abstract fun studentDao(): StudentDao
    abstract fun attendanceDao(): AttendanceDao
}
