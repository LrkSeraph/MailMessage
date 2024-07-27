package lrk.app.mailmessage

import lrk.app.mailmessage.ScriptRunner.Platform.*
import java.io.File

object ScriptRunner {

    enum class Platform {
        UnixLike,
        Windows,
        Unknown;

        companion object {
            operator fun invoke(): Platform {
                return when {
                    System.getProperty("os.name").lowercase().contains("windows") -> Windows
                    System.getProperty("os.name").lowercase().contains("linux") -> UnixLike
                    else -> Unknown
                }
            }
        }
    }

    operator fun invoke(script: String, sender: String, subject: String, content: String) {
        if (!File(script).exists()) {
            Log error "script[$script] not exists"
            return
        }
        when (Platform()) {
            UnixLike -> {
                Log info "UnixLike Platform: bash $script $sender $subject \"$content\""
                ProcessBuilder().command("bash", "-c", "bash $script $sender $subject \"$content\"").start()
            }
            Windows -> {
                Log info "Windows Platform: cmd.exe /C $script $sender $subject \"$content\""
                ProcessBuilder().command("cmd.exe", "/C", "$script $sender $subject \"$content\"").start()
            }
            Unknown -> {
                Log info "unknown platform, nothing to do"
            }
        }
    }
}