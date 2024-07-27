package lrk.app.mailmessage

interface MailProtocol {
    fun checkParam(paramMap: Map<String, String>, requiredFields: Array<String>) {
        requiredFields.forEach { requiredField ->
            if (!paramMap.containsKey(requiredField)) {
                println("No $requiredField provided")
                ParamParser.usage()
            }
        }
    }

    fun start(paramMap: Map<String, String>)
}