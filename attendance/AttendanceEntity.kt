package com.example.attendanceapp.attendance

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.attendanceapp.course.CourseEntity
import com.example.attendanceapp.student.StudentEntity

@Entity(
    tableName = "attendance",
    foreignKeys = [
        ForeignKey(
            entity = StudentEntity::class,
            parentColumns = ["enrollmentNumber"],
            childColumns = ["enrollmentNumber"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CourseEntity::class,
            parentColumns = ["courseCode"],
            childColumns = ["courseCode"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["enrollmentNumber"]), // Speeds up queries involving enrollmentNumber
        Index(value = ["courseCode"]) // Speeds up queries involving courseCode
    ]
)
data class AttendanceEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val enrollmentNumber: String, // Foreign key to StudentEntity
    val courseCode: String, // Foreign key to CourseEntity
    val date: String, // Date of attendance in ISO format (e.g., "2025-01-27")
    val status: String // Attendance status (e.g., "P" for Present, "A" for Absent)
)
