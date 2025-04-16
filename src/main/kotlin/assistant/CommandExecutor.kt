package assistant

import java.io.BufferedReader

object CommandExecutor {
    fun execute(command: String): String {
        return try {
            val process = ProcessBuilder("bash", "-c", command)
                .redirectErrorStream(true)
                .start()

            val output = process.inputStream.bufferedReader().use(BufferedReader::readText)
            process.waitFor()
            output.trim()
        } catch (e: Exception) {
            "Ошибка выполнения команды: ${e.message}"
        }
    }
}