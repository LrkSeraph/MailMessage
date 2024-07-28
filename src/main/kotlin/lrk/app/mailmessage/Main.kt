@file:JvmName("Main")

package lrk.app.mailmessage

import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val paramMap = ParamParser.parse(args)

    if (paramMap["disable_log"] == "true") Log.disabledLogFile = true
    Log debug "Mail Message called with params: $paramMap"

    when (paramMap["mode"]){
        "mailto" -> MailTo.start(paramMap)
        "daemon" -> {
            when(paramMap["protocol"]){
                "imap" -> IMAPDaemon.start(paramMap)
                "pop3" -> POP3Daemon.start(paramMap)
            }
        }
        else -> exitProcess(0)
    }
}

