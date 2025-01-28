
package com.example.attendanceapp.attendance

import android.content.Context
import androidx.room.Room

object DatabaseInstance {
    @Volatile
    private var INSTANCE: AttendanceDatabase? = null
    private lateinit var appContext: Context

    fun initialize(context: Context) {
        appContext = context.applicationContext
    }

    fun getDatabase(): AttendanceDatabase {
        check(::appContext.isInitialized) { "DatabaseInstance is not initialized. Call initialize(context) first." }
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                appContext,
                AttendanceDatabase::class.java,
                "attendance_database"
            ).build()
            INSTANCE = instance
            instance
        }
    }

}

