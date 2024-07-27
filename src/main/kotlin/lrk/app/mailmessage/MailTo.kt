package lrk.app.mailmessage

import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

object MailTo : MailProtocol {
    override fun start(paramMap: Map<String, String>) {
        checkParam(paramMap, arrayOf("account", "password", "smtp_server", "smtp_port", "recipient", "message"))

        val props = Properties()
        props["mail.smtp.auth"] = "true"
        props["mail.smtp.host"] = paramMap["smtp_server"]
        // use SSL
        props["mail.smtp.ssl.enable"] = "true"
        props["mail.smtp.socketFactory.class"] = "javax.net.ssl.SSLSocketFactory"
        props["mail.smtp.socketFactory.port"] = paramMap["smtp_port"]
        props["mail.smtp.port"] = paramMap["smtp_port"]
        // account and password
        props["mail.smtp.from"] = paramMap["account"]
        props["mail.user"] = paramMap["account"]
        props["mail.password"] = paramMap["password"]

        val session = Session.getInstance(props, object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                val userName = props.getProperty("mail.user")
                val password = props.getProperty("mail.password")
                return PasswordAuthentication(userName, password)
            }
        })

        val message: Message = MimeMessage(session)
        message.setFrom(InternetAddress(props.getProperty("mail.smtp.from")))
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(paramMap["recipient"]))
        message.subject = if (paramMap.containsKey("subject")) {
            paramMap["subject"]
        } else {
            "Mail From Kotlin Program"
        }
        message.setText(paramMap["message"])
        Transport.send(message)
    }
}