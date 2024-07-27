package lrk.app.mailmessage

import java.io.FileInputStream
import java.util.*
import kotlin.system.exitProcess

object ParamParser {
    fun parse(args: Array<String>): Map<String, String> {
        if (args.isEmpty()) usage()
        val result = HashMap<String, String>()
        val message = StringBuilder()
        args.forEachIndexed { index, arg ->
            if (index == 0) {
                when (arg) {
                    "mailto" -> result["mode"] = arg
                    "daemon" -> result["mode"] = arg
                    else -> usage()
                }
            } else {
                when {
                    arg.startsWith("--config=") -> result.putAll(parseConfig(arg.substring("--config=".length)))
                    arg.startsWith("--account=") -> result["account"] = arg.substring("--account=".length)
                    arg.startsWith("--password=") -> result["password"] = arg.substring("--password=".length)
                    arg.startsWith("--smtp_server=") -> result["smtp_server"] = arg.substring("--smtp_server=".length)
                    arg.startsWith("--smtp_port=") -> result["smtp_port"] = arg.substring("--smtp_port=".length)
                    arg.startsWith("--imap_server=") -> result["imap_server"] = arg.substring("--imap_server=".length)
                    arg.startsWith("--imap_port=") -> result["imap_port"] = arg.substring("--imap_port=".length)
                    arg.startsWith("--pop3_server=") -> result["pop3_server"] = arg.substring("--pop3_server=".length)
                    arg.startsWith("--pop3_port=") -> result["pop3_port"] = arg.substring("--pop3_port=".length)
                    arg.startsWith("--sender=") -> result["sender"] = arg.substring("--sender=".length)
                    arg.startsWith("--recipient=") -> result["recipient"] = arg.substring("--recipient=".length)
                    arg.startsWith("--subject=") -> result["subject"] = arg.substring("--subject=".length)
                    arg.startsWith("--script=") -> result["script"] = arg.substring("--script=".length)
                    else -> {
                        if (result["mode"] == "daemon") usage()
                        message.append(arg).append(" ")
                    }
                }
            }
        }
        if (!result.containsKey("sender") && result.containsKey("account")) result["sender"] = result["account"]!!
        if (message.isNotEmpty()) result["message"] = message.toString()
        return result
    }

    private fun parseConfig(path: String): Map<String, String> {
        val result = HashMap<String, String>()
        val props = Properties()
        props.load(FileInputStream(path))
        props.forEach { prop ->
            result[prop.key.toString()] = prop.value.toString()
        }
        return result
    }

    fun usage() {
        println(
            """
            usage: MailMessage mailto [--config=file path] [--account=] [--password=] [--smtp_server=] [--smtp_port=] [--sender=] --recipient= --subject= message
                                daemon [--config=file path] [--account=] [--password=] [--imap_server=] [--imap_port=] [--pop3_server=] [--pop3_port=] --script=
            Mode:
                mailto  send an email
                daemon  read emails from pop3 server and execute the script with message as argument
            Options:
                --config        provide a config file 
                --account       E-mail account
                --password      E-mail password
                --smtp_server   SMTP server address
                --smtp_port     SMTP port
                --imap_server   IMAP server address
                --imap_port     IMAP port
                --pop3_server   POP3 server address
                --pop3_port     POP3 port
                --sender        who send the message, default is account
                --recipient     who will receive this email
                --subject       subject of this mail
                --script        it will be executed like: script <sender> <subject> <message>
        """.trimIndent()
        )
        exitProcess(1)
    }
}