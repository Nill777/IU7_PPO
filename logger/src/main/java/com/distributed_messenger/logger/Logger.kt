package com.distributed_messenger.logger

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

    fun initialize(fileName: String?) {
        if (fileName.isNullOrEmpty()) {
            throw IllegalArgumentException("File name must not be null or empty")
        }
        logFile = File(fileName)
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