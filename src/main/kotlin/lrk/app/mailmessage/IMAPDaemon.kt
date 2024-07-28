package lrk.app.mailmessage

import com.sun.mail.imap.IMAPFolder
import com.sun.mail.imap.IMAPMessage
import com.sun.mail.imap.IMAPSSLStore
import java.util.*
import javax.mail.Flags
import javax.mail.Folder
import javax.mail.Multipart
import javax.mail.Part
import javax.mail.Session
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeUtility

object IMAPDaemon : MailProtocol {
    override fun start(paramMap: Map<String, String>) {
        checkParam(paramMap, arrayOf("account", "password", "imap_server", "imap_port", "script"))

        Log debug "IMAPDaemon start"

        val props = Properties()
        props["mail.store.protocol"] = "imaps"
        props["mail.imaps.host"] = paramMap["imap_server"]
        props["mail.imaps.port"] = paramMap["imap_port"]
        props["mail.imaps.ssl.enable"] = "true"
        props["mail.imaps.fetchsize"] = "${16 * 1024}"

        val session: Session = Session.getInstance(props)
        val store: IMAPSSLStore = session.getStore("imaps") as IMAPSSLStore
        store.connect(paramMap["imap_server"], paramMap["account"], paramMap["password"])

        Log debug "Session prepared"

        while (true) {
            if (store.isConnected) {
                val folder: IMAPFolder = store.getFolder("INBOX") as IMAPFolder
                folder.open(Folder.READ_WRITE)
                folder.use {
                    val count = folder.unreadMessageCount
                    if (count != 0) {
                        folder.messages.forEach { message ->
                            val imapMessage = message as IMAPMessage
                            if (!imapMessage.flags.contains(Flags.Flag.SEEN)) {
                                val sender = (imapMessage.from[0] as InternetAddress).address
                                val subject = MimeUtility.decodeText(imapMessage.subject)
                                val contentString = StringBuilder()
                                when (val content = imapMessage.content) {
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
                                imapMessage.setFlag(Flags.Flag.SEEN, true)
                                imapMessage.setFlag(Flags.Flag.DELETED, true)
                            }
                        }
                    }
                }
                Thread.sleep(5000)
            } else {
                throw RuntimeException("IMAP Connect failed.")
            }
        }

    }
}