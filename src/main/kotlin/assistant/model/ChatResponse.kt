package assistant.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class ChatResponse(
    val model: String,

    @JsonProperty("created_at")
    val createdAt: Date,

    val message: ChatMessage,

    @JsonProperty("done_reason")
    val doneReason: String? = null,

    val done: Boolean? = null,

    @JsonProperty("total_duration")
    val totalDuration: Long? = null,

    @JsonProperty("load_duration")
    val loadDuration: Long? = null,

    @JsonProperty("prompt_eval_count")
    val promptEvalCount: Long? = null,

    @JsonProperty("prompt_eval_duration")
    val promptEvalDuration: Long? = null,

    @JsonProperty("eval_count")
    val evalCount: Long? = null,

    @JsonProperty("eval_duration")
    val evalDuration: Long? = null,
)
