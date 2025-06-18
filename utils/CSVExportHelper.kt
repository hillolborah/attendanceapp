package com.example.attendanceapp.utils

//export location: Internal Storage > Android > data > com.example.attendanceapp > files

import android.content.Context
import android.os.Environment
import java.io.File
import java.io.FileWriter
import java.io.IOException

object CSVExportHelper {
    fun exportAttendanceToCSV(
        context: Context,
        fileName: String,
        headers: List<String>,
        data: List<List<String>>
    ): String {
        val directory = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "AttendanceExports")
        if (!directory.exists()) {
            directory.mkdirs()
        }

        val file = File(directory, "$fileName.csv")
        var writer: FileWriter? = null

        try {
            writer = FileWriter(file)

            // Write headers
            writer.append(headers.joinToString(","))
            writer.append("\n")

            // Write data rows
            data.forEach { row ->
                writer.append(row.joinToString(","))
                writer.append("\n")
            }

            writer.flush()
        } catch (e: IOException) {
            e.printStackTrace()
            return "Error: ${e.localizedMessage}"
        } finally {
            writer?.close()
        }

        return file.absolutePath // Return the file path for sharing or notification
    }
}
