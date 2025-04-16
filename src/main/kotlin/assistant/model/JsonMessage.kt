package assistant.model

data class JsonMessage(
    val type: String,
    val command: String? = null,
//    val explanation: String? = null,
    val content: String? = null
)