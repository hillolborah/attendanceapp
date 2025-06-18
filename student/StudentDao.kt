package com.example.attendanceapp.student

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentDao {
    @Insert
    suspend fun insertStudent(student: StudentEntity)

    @Query("SELECT * FROM students WHERE courseCode = :courseCode")
    fun getStudentsByCourseFlow(courseCode: String): Flow<List<StudentEntity>> // ✅ Using Flow

    @Query("SELECT * FROM students WHERE courseCode = :courseCode")
    suspend fun getStudentsByCourse(courseCode: String): List<StudentEntity>

//    @Query("DELETE FROM students WHERE enrollmentNumber = :enrollmentNumber")
//    suspend fun deleteStudent(enrollmentNumber: String)
    @Query("DELETE FROM students WHERE enrollmentNumber = :enrollmentNumber AND courseCode = :courseCode")
    suspend fun deleteStudent(enrollmentNumber: String, courseCode: String)

//    @Query("SELECT * FROM students WHERE enrollmentNumber = :enrollmentNumber")
//    suspend fun getStudentByEnrollment(enrollmentNumber: String): StudentEntity?

    @Update
    suspend fun updateStudent(student: StudentEntity)

    @Query("SELECT * FROM students WHERE enrollmentNumber = :enrollmentNumber AND courseCode = :courseCode")
    suspend fun getStudentByEnrollmentAndCourse(enrollmentNumber: String, courseCode: String): StudentEntity?

}

