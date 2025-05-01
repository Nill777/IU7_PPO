package com.distributed_messenger.logger

import android.util.Log
import timber.log.Timber
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CustomFormattingTree(private val logFile: File) : Timber.Tree() {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        val time = dateFormat.format(Date())
        val level = when (priority) {
            Log.DEBUG -> "DEBUG"
            Log.INFO -> "INFO"
            Log.WARN -> "WARN"
            Log.ERROR -> "ERROR"
            else -> "UNKNOWN"
        }

        // Формат: %d{yyyy-MM-dd HH:mm:ss} [%t] %-5level %msg%n
        val formattedMessage = "$time [$level] $tag - $message"
        Log.println(priority, tag ?: "App", formattedMessage)
        logFile.appendText(formattedMessage + "\n")
    }
}