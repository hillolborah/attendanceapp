package com.example.attendanceapp.student

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.attendanceapp.course.CourseEntity


@Entity(
    tableName = "students",
    foreignKeys = [ForeignKey(
        entity = CourseEntity::class,
        parentColumns = ["courseCode"],
        childColumns = ["courseCode"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["courseCode"])] // Index on courseCode is already added
)
data class StudentEntity(
    @PrimaryKey val enrollmentNumber: String, // Make enrollmentNumber the primary key
    val name: String,
    val courseCode: String // Foreign key
)
