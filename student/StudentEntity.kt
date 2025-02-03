package com.example.attendanceapp.student

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.attendanceapp.course.CourseEntity


//@Entity(
//    tableName = "students",
//    foreignKeys = [ForeignKey(
//        entity = CourseEntity::class,
//        parentColumns = ["courseCode"],
//        childColumns = ["courseCode"],
//        onDelete = ForeignKey.CASCADE
//    )],
//    indices = [Index(value = ["courseCode"])] // Index on courseCode is already added
//)
//data class StudentEntity(
//    @PrimaryKey val enrollmentNumber: String, // Make enrollmentNumber the primary key
//    val name: String,
//    val courseCode: String // Foreign key
//)
@Entity(
    tableName = "students",
    foreignKeys = [ForeignKey(
        entity = CourseEntity::class,
        parentColumns = ["courseCode"],
        childColumns = ["courseCode"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [
        Index(value = ["courseCode"]),
//        Index(value = ["enrollmentNumber"], unique = true)  // Add unique index on enrollmentNumber
    ],
    primaryKeys = ["enrollmentNumber", "courseCode"]  // Composite primary key
)
data class StudentEntity(
    val enrollmentNumber: String,  // enrollmentNumber is part of the primary key
    val name: String,
    val courseCode: String // courseCode is part of the primary key
)

