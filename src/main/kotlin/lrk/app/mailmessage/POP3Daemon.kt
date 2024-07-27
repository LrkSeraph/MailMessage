package lrk.app.mailmessage

import java.lang.UnsupportedOperationException

object POP3Daemon : MailProtocol {
    override fun start(paramMap: Map<String, String>) {
        checkParam(paramMap, arrayOf("account", "password", "pop3_server", "pop3_port"))
        throw UnsupportedOperationException("Function not implemented")
    }
}