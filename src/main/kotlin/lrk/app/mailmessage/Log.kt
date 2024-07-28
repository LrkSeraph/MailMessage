package lrk.app.mailmessage

import java.io.FileOutputStream
import java.text.DateFormat
import java.util.*

object Log {
    private const val LOG_FILE = "MailMessage.log"
    var disabledLogFile: Boolean = false

    private fun formatLogString(level: String, message: String): String {
        return "[${DateFormat.getDateTimeInstance().format(Date(System.currentTimeMillis()))}][$level]\t$message"
    }

    infix fun info(message: String) {
        val logString = formatLogString("INFO", message)
        println(logString)
        logToFile(message = logString)
    }

    infix fun debug(message: String) {
        val logString = formatLogString("DEBUG", message)
        println(logString)
        logToFile(message = logString)
    }

    infix fun error(message: String) {
        val logString = formatLogString("ERROR", message)
        println(logString)
        logToFile(message = logString)
    }

    private fun logToFile(path: String = LOG_FILE, message: String) {
        if (!disabledLogFile) {
            FileOutputStream(path, true).use {
                it.write(message.toByteArray())
                it.write(System.lineSeparator().toByteArray())
            }
        }
    }
}