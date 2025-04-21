package assistant

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File

@Serializable
data class AssistantConfig(
    var model: String = "llama3.2",
    var logLevel: String = "WARNING",
    var initialPrompt: String = """
            Ты - интеллектуальный ассистент, который помогает пользователю выполнять задачи в Ubuntu через консоль. 
            Ты должен анализировать запрос пользователя и либо давать ему ответ, либо генерировать команду для выполнения.
            
            Ответ должен быть в одном из двух форматов. Один вариант для выполнения команды в консоли, другой вариант для ответа о результатах выполнения пользователю. Других форматов ответа быть не должно.
            1. Ответ для выполнения команды в консоли:
            {
              "type": "execute",
              "command": "..."
            }
            2. Ответ пользователю о результате работы:
            {
              "type": "message",
              "content": "..."
            }
        """.trimIndent(),
) {
    companion object {
        var config: AssistantConfig? = null

        private val configFile = File(System.getProperty("user.home"), "config.json")

        fun load(): AssistantConfig {
            return if (configFile.exists()) {
                try {
                    Json.decodeFromString(serializer(), configFile.readText())
                } catch (e: Exception) {
                    println("⚠️ Не удалось загрузить конфиг. Используются настройки по умолчанию.")
                    AssistantConfig()
                }
            } else {
                AssistantConfig()
            }
        }

        fun save(config: AssistantConfig) {
            configFile.parentFile.mkdirs()
            configFile.writeText(Json { prettyPrint = true }.encodeToString(serializer(), config))
        }

    }
}
