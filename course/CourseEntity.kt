package com.example.attendanceapp.course

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "courses",
    indices = [Index(value = ["courseCode"], unique = true)]
)
data class CourseEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val courseCode: String // This column now has a unique index
)
