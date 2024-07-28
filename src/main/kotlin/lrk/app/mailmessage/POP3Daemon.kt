package lrk.app.mailmessage

import com.sun.mail.pop3.POP3Folder
import com.sun.mail.pop3.POP3Message
import com.sun.mail.pop3.POP3SSLStore
import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeUtility

object POP3Daemon : MailProtocol {
    override fun start(paramMap: Map<String, String>) {
        checkParam(paramMap, arrayOf("account", "password", "pop3_server", "pop3_port"))

        Log debug "POP3Daemon start"

        val props = Properties()
        props["mail.store.protocol"] = "pop3s"
        props["mail.pop3s.host"] = paramMap["pop3_server"]
        props["mail.pop3s.port"] = paramMap["pop3_port"]
        props["mail.pop3s.ssl.enable"] = "true"

        val session = Session.getInstance(props)
        val store: POP3SSLStore = session.getStore("pop3s") as POP3SSLStore
        store.connect(paramMap["pop3_server"], paramMap["account"], paramMap["password"])

        while (true) {
            if (store.isConnected) {
                val folder: POP3Folder = store.getFolder("INBOX") as POP3Folder
                folder.open(Folder.READ_WRITE)
                folder.use {
                    val count = folder.unreadMessageCount
                    if (count != 0) {
                        folder.messages.forEach { message ->
                            val pop3message = message as POP3Message
                            if (!pop3message.flags.contains(Flags.Flag.SEEN)) {
                                val sender = (pop3message.from[0] as InternetAddress).address
                                val subject = MimeUtility.decodeText(pop3message.subject)
                                val contentString = StringBuilder()
                                when (val content = pop3message.content) {
                                    is Multipart -> {
                                        contentString.append(content.getBodyPart(0).content.toString())
                                    }

                                    is Part -> {
                                        contentString.append(content.content.toString())
                                    }

                                    else -> {
                                        contentString.append(content.toString())
                                    }
                                }
                                Log info "sender=$sender, subject=$subject, content=$contentString"
                                ScriptRunner(paramMap["script"]!!, sender, subject, contentString.toString())
                                pop3message.setFlag(Flags.Flag.DELETED, true)
                            }
                        }
                    }
                }
                Thread.sleep(3000)
            } else {
                throw RuntimeException("POP3 Connect failed.")
            }
        }
    }
}