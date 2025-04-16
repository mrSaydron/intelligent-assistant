package assistant.model

data class ChatMessage(
    val role: RoleEnum,
    val content: String
)

