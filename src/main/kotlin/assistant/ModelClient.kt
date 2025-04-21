package assistant

import assistant.model.*
import com.fasterxml.jackson.module.kotlin.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.logging.Logger

object ModelClient {
    private val logger = Logger.getLogger(ModelClient::class.java.name)
    private val client = OkHttpClient.Builder()
        .connectTimeout(1200, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(1200, java.util.concurrent.TimeUnit.SECONDS)
        .writeTimeout(1200, java.util.concurrent.TimeUnit.SECONDS)
        .build()
    private val mapper = jacksonObjectMapper()

    private const val URL = "http://localhost:11434/api/chat"

    // Храним всю историю диалога
    private val conversationHistory = mutableListOf(ChatMessage(RoleEnum.user, AssistantConfig.config?.initialPrompt ?: ""))

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

}