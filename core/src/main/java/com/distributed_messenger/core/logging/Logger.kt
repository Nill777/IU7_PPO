package com.distributed_messenger.core.logging

import java.io.File
import java.time.LocalDateTime

interface Logger {
    fun log(
        tag: String,
        message: String,
        level: LogLevel = LogLevel.INFO,
        throwable: Throwable? = null
    )
}

enum class LogLevel { DEBUG, INFO, WARN, ERROR }

class AppLogger(private val logFile: String) : Logger {
    override fun log(tag: String, message: String, level: LogLevel, throwable: Throwable?) {
        val time = LocalDateTime.now().toString()
        val logMessage = "[$time] [${level.name}] $tag: $message ${throwable?.message ?: ""}"
        // File(logFile).appendText("$logMessage\n")
        println(logMessage)
    }
}