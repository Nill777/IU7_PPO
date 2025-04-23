package com.distributed_messenger.core.logging

import java.io.File
import java.time.LocalDateTime

interface ILogger {
    fun log(
        tag: String,
        message: String,
        level: LogLevel = LogLevel.INFO,
        throwable: Throwable? = null
    )
}

enum class LogLevel { DEBUG, INFO, WARN, ERROR }

object Logger : ILogger {
    private lateinit var logFile: File

    fun initialize(appContext: Context, fileName: String = "application.log") {
        context = appContext
        logFile = File(context.filesDir, fileName)

        if (!logFile.exists()) {
            logFile.parentFile?.mkdirs()
            logFile.createNewFile()
        }
    }

//    override fun log(tag: String, message: String, level: LogLevel, throwable: Throwable?) {
//        val time = LocalDateTime.now().toString()
//        val logMessage = "[$time] [${level.name}] $tag: $message ${throwable?.message ?: ""}"
//        logFile.appendText("$logMessage\n")
//        println(logMessage)
//    }
    override fun log(tag: String, message: String, level: LogLevel, throwable: Throwable?) {
        try {
            val time = LocalDateTime.now().toString()
            val logMessage = "[$time] [${level.name}] $tag: $message ${throwable?.message ?: ""}"
            logFile.appendText("$logMessage\n")
            println(logMessage)
        } catch (e: Exception) {
            System.err.println("Logging failed: ${e.message}")
            e.printStackTrace()
        }
    }
}