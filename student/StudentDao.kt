package com.example.attendanceapp.student

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface StudentDao {
    @Insert
    suspend fun insertStudent(student: StudentEntity)

    @Query("SELECT * FROM students WHERE courseCode = :courseCode")
    suspend fun getStudentsByCourse(courseCode: String): List<StudentEntity>

    @Query("DELETE FROM students WHERE enrollmentNumber = :enrollmentNumber")
    suspend fun deleteStudent(enrollmentNumber: String)


    @Query("SELECT * FROM students WHERE enrollmentNumber = :enrollmentNumber")
    suspend fun getStudentByEnrollment(enrollmentNumber: String): StudentEntity?

    @Update
    suspend fun updateStudent(student: StudentEntity)
}
