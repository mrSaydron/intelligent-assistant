package assistant

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File

@Serializable
data class AssistantConfig(
    var model: String = "llama3.2",
    var logLevel: String = "WARNING"
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
