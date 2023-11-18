package com.judas.katachi.utility

import android.util.Log

object Logger {
    enum class Level { VERBOSE, DEBUG, INFO, WARNING, ERROR }

    var logLevel: Level = Level.ERROR
    var appName: String = ""

    // Logs a message with Android default logger.
    internal fun log(
        level: Level = Level.VERBOSE,
        tag: String = "",
        message: String = "",
        error: Throwable? = null,
    ) {
        val fullTag = tag.take(23) // Max tag length is 23 on older Android versions

        // Add prefix, error cause & stacktrace if necessary + cut to max length
        var fullMessage = "[$appName] $message"
        error?.let {
            fullMessage += ": ${it.message}. Caused by ${it.cause}\n"
            fullMessage += error.stackTraceToString()
        }

        // Log to Android Logcat
        if (level.ordinal >= logLevel.ordinal) {
            when (level) {
                Level.VERBOSE -> Log.v(fullTag, fullMessage)
                Level.DEBUG -> Log.d(fullTag, fullMessage)
                Level.INFO -> Log.i(fullTag, fullMessage)
                Level.WARNING -> Log.w(fullTag, fullMessage)
                Level.ERROR -> Log.e(fullTag, fullMessage)
            }
        }
    }
}

fun Any.log(
    level: Logger.Level = Logger.Level.VERBOSE,
    message: String = "",
    error: Throwable? = null
) = Logger.log(
    level = level,
    tag = this::class.java.simpleName,
    message = message,
    error = error
)
