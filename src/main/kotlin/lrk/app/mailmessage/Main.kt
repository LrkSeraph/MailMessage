@file:JvmName("Main")

package lrk.app.mailmessage

import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val paramMap = ParamParser.parse(args)
    Log debug "Mail Message called with params: $paramMap"
    when (paramMap["mode"]){
        "mailto" -> MailTo.start(paramMap)
        "daemon" -> IMAPDaemon.start(paramMap)
        else -> exitProcess(0)
    }
}

