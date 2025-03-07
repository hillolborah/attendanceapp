package com.example.attendanceapp.course

//import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CourseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourse(course: CourseEntity)

    @Query("SELECT * FROM courses")
    fun getAllCourses(): List<CourseEntity>  // Correct return type

    @Update
    suspend fun updateCourse(course: CourseEntity)

    @Query("SELECT * FROM courses WHERE courseCode = :courseCode LIMIT 1")
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    suspend fun getCourseByCode(courseCode: String): CourseEntity?

    @Query("SELECT * FROM courses")
    fun getAllCoursesAsFlow(): Flow<List<CourseEntity>>



}
