package assistant

import assistant.model.*
import com.fasterxml.jackson.module.kotlin.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.logging.Logger
import kotlin.math.log

object ModelClient {
    private val logger = Logger.getLogger(ModelClient::class.java.name)
    private val client = OkHttpClient.Builder()
        .connectTimeout(600, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(600, java.util.concurrent.TimeUnit.SECONDS)
        .writeTimeout(600, java.util.concurrent.TimeUnit.SECONDS)
        .build()
    private val mapper = jacksonObjectMapper()

    private const val URL = "http://localhost:11434/api/chat"

    // Храним всю историю диалога
    private val conversationHistory = mutableListOf<ChatMessage>(
        ChatMessage(RoleEnum.user, getInitialPrompt())
    )

    fun sendUserPrompt(prompt: String): JsonMessage {
        conversationHistory += ChatMessage(role = RoleEnum.user, content = prompt)
        return sendRequestAndParseResponse()
    }

    fun sendExecutionResult(result: String): JsonMessage {
        // Добавляем результат выполнения как assistant-ответ, чтобы модель помнила его
        conversationHistory += ChatMessage(role = RoleEnum.user, content = result)

        return sendRequestAndParseResponse()
    }

    private fun sendRequestAndParseResponse(): JsonMessage {
        val requestBodyJson = mapper.writeValueAsString(
            ChatRequest(
                model = AssistantConfig.config?.model ?: "llama3.2",
                messages = conversationHistory,
            )
        )
        logger.info(conversationHistory.toString())

        val request = Request.Builder()
            .url(URL)
            .post(requestBodyJson.toRequestBody("application/json".toMediaTypeOrNull()!!))
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
            val responseBody = response.body?.string() ?: throw IOException("Empty response")
            logger.info(responseBody)

            val chatResponse = mapper.readValue<ChatResponse>(responseBody)

            // Сохраняем ответ модели в историю
            conversationHistory += chatResponse.message

            // Пытаемся извлечь JSON-команду из ответа
            return try {
                mapper.readValue(chatResponse.message.content)
            } catch (e: Exception) {
                logger.warning("⚠️ Ошибка при разборе JSON из ответа модели:\n${responseBody}")
                JsonMessage(type = "message", content = chatResponse.message.content)
            }
        }
    }

    private fun getInitialPrompt(): String {
        return """
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
        """.trimIndent()
    }

//    fun sendUserPromptndInitialPrompt(userInput: String): JsonMessage {
//        val prompt = """
//            Ты - интеллектуальный ассистент, который помогает пользователю выполнять задачи в Ubuntu через консоль.
//            Ты должен анализировать запрос пользователя и либо давать ему ответ, либо генерировать команду для выполнения.
//
//            Ответ должен быть в одном из двух форматов. Один вариант для выполнения комады в консоли, другой вариант для ответа о результатах выполнения пользователю. Других форматов ответа быть не должно.
//            1. Ответ для выполнения команды в консоли:
//            {
//              "type": "execute",
//              "command": "...",
//              "explanation": "..."
//            }
//            2. Ответ пользователю о результате работы:
//            {
//              "type": "message",
//              "content": "..."
//            }
//
//            Получена задача от пользователя: "$userInput"
//        """.trimIndent()
//
//        return sendUserPrompt(prompt)
//    }

}