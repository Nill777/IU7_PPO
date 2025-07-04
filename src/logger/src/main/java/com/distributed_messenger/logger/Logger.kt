package com.distributed_messenger.logger

import timber.log.Timber
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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

    fun initialize(logDirPath: String) {
        val logFile = File(logDirPath, "app_${SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())}.log")
        if (!logFile.exists()) {
            try {
                logFile.createNewFile()
            } catch (e: IOException) {
                throw RuntimeException("Failed to create log file", e)
            }
        }
        Timber.plant(CustomFormattingTree(logFile))
    }

    override fun log(tag: String, message: String, level: LogLevel, throwable: Throwable?) {
        val formattedMessage = "$tag - $message"
        when (level) {
            LogLevel.DEBUG -> Timber.tag(tag).d(throwable, formattedMessage)
            LogLevel.INFO -> Timber.tag(tag).i(throwable, formattedMessage)
            LogLevel.WARN -> Timber.tag(tag).w(throwable, formattedMessage)
            LogLevel.ERROR -> Timber.tag(tag).e(throwable, formattedMessage)
        }
    }
}