package assistant

import assistant.AssistantConfig.Companion.config
import assistant.model.JsonMessage
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger
import kotlin.system.exitProcess

private val logger = Logger.getLogger("Main")

fun main(args: Array<String>) {
    config = AssistantConfig.load()
    val scanner = Scanner(System.`in`)

    val promptArgs = handleCommandParams(args)

    val prompt = promptArgs.joinToString(" ")
    if (prompt.isBlank()) {
        println("🧠 Интеллектуальный ассистент запущен. Введите задачу:")
        while (true) {
            print("> ")
//            val userInput = readlnOrNull()?.trim()
            val userInput = scanner.nextLine().trim()
            if (userInput.isEmpty()) return

            val userPrompt = ModelClient.sendUserPrompt(userInput)
            processResponse(userPrompt)
        }
    } else {
        val userPrompt = ModelClient.sendUserPrompt(prompt)
        processResponse(userPrompt)
    }
}

private fun handleCommandParams(args: Array<String>): MutableList<String> {
    val promptArgs = mutableListOf<String>()
    config?.let {
        for (i in args.indices) {
            when (args[i]) {
                "--model" -> {
                    if (i + 1 < args.size) {
                        it.model = args[i + 1]
                        AssistantConfig.save(it)
                    }
                }
                "--log-level" -> {
                    if (i + 1 < args.size) {
                        it.logLevel = args[i + 1].uppercase()
                        AssistantConfig.save(it)
                    }
                }
                "--initial-prompt" -> {
                    if (i + 1 < args.size && !args[i + 1].startsWith("--")) {
                        it.initialPrompt = args[i + 1]
                        AssistantConfig.save(it)
                    } else {
                        it.initialPrompt = AssistantConfig().initialPrompt
                        AssistantConfig.save(it)
                    }
                }
                "--help" -> {
                    printHelp()
                    exitProcess(0)
                }
                "--print-config" -> {
                    printConfig()
                    exitProcess(0)
                }
                else -> if (i == 0 || !args[i - 1].startsWith("--")) promptArgs += args[i]
            }
        }
        configureLogging(Level.parse(it.logLevel))
    }
    return promptArgs
}

fun printHelp() {
    println("""
        🤖 Assistant CLI — параметры запуска:

        --model <имя>             Использовать модель (например: llama3.2, gemma3)
        --log-level <уровень>     Уровень логирования: DEBUG, INFO, WARNING, ERROR
        --initial-prompt <промт>  Инициализирующий запрос в LLM, объясняющий формат ответа и роль ассистента (для сброса не передавать значение)
        --help                    Показать эту справку
        --print-config            Вывести настройки
    """.trimIndent())
}

fun printConfig() {
    println("""
  model: ${config?.model ?: ""}
  logLevel: ${config?.logLevel ?: ""}
  initialPrompt: 
${config?.initialPrompt ?: ""}
""".trimIndent())
}

fun processResponse(response: JsonMessage) {
    when (response.type) {
        "execute" -> {
            println("⚙️ Выполнение: ${response.command}")
            val result = CommandExecutor.execute(response.command!!)
            println("📤 Результат:\n$result")
            val nextResponse = ModelClient.sendExecutionResult(result)
            processResponse(nextResponse)
        }

        "message" -> {
            println("💬 Модель: ${response.content}")
        }

        else -> {
            logger.warning("Неизвестный тип ответа: $response")
        }
    }
}

fun configureLogging(level: Level) {
    val rootLogger = Logger.getLogger("")
    rootLogger.level = level
    rootLogger.handlers.forEach { it.level = level }
}