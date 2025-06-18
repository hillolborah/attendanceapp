package com.example.attendanceapp

import android.app.Application
import com.example.attendanceapp.attendance.DatabaseInstance

class AttendanceApp : Application() {
    override fun onCreate() {
        super.onCreate()
        DatabaseInstance.initialize(this)
    }

}
