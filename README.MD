# Описание
Здесь должно быть описание

# Работа
Для выхода из приложения, необходимо отправить пустой запрос

# Установка
Проект использует контейнеризатор для LLM - [ollama](https://ollama.com/)
После установки ollama необходимо загрузить одну из моделей, например llama3.2
Загрузка модели производится командой ```ollama run llama3.2```
Список доступных моделей можно посмотреть на странице https://github.com/ollama/ollama?tab=readme-ov-file

Сборка проекта производится командой: ```./gradlew fatJar```
Для запуска в корне проекта лежит скрипт: assistant запускающий собраный jar файл

# Тесты
| Описание                       | Команда                                                                                                          | codellama(7b) | mistral(7b)                                                           | gemma3(4b)                                                               | deepseek-r1(7b) | llama3.2(3b) |
|--------------------------------|------------------------------------------------------------------------------------------------------------------|---------------|-----------------------------------------------------------------------|--------------------------------------------------------------------------|-----------------|--------------|
| Вывпод фразы в консоль         | Выведи в консоль hello world                                                                                     | y             | y                                                                     | y                                                                        | x               |              |
| Вывод списка файлов в папке    | Выведи список файлов из текущей папки                                                                            | y             | y                                                                     | y                                                                        |                 |              |
| Создание файла с текстом       | Создай файл answer.txt с текстом почему небо голубое                                                             | x             | y - что бы команда отработала успешно, до нее надо спросить что попроще | Y - понял буквально                                                      |                 |              |
| Редактирование файла с текстом | Удали все пробелы из файла answer.txt                                                                            |               | y                                                                     | y - но зачем то результат сохранил в новом файле, удалив при этом старый |                 |              |
| Поиск файла в папке            | Найди файл содержащий в названии one в текущей директории и поддиректориях                                       |               | y - упорно ищет папки                                                 | y - после указания, что папки не нужны исключил их из поиска             |                 |              |
| Поиск файла по тексту          | В текущем каталоге и подкаталогах найди файлы содержащие в себе слово forest                                     |               | y - после подсказки что стоит использовать grep                       | n - справился только после того как я написал ему команду полностью      |                 |              |
| Создание docker-compose файла  | Создай файл docker-compose с образом postgresql                                                                  |               | y - но только простой запрос                                          | n - текст для файла написал, но записать в файл не смог                  |                 |              |
| Загрузка файла                 | Загрузи изображение https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTXpq0YYOx4opQI1Rf-93FIN_vMPdxfTAqjbQ&s |               | n - не экранировал адрес файла, из-за этого получил ошибку            |                                                                          |                 |              |
| Загрузка файла                 | Загрузи https://github.com/ollama/ollama/blob/main/docs/api.md                                                   |               | y                                                                     |                                                                          |                 |              |
| Загрузка и анализ файла        | Загрузи https://raw.githubusercontent.com/ollama/ollama/refs/heads/main/docs/api.md и кратко перескажи что в нем |               | y -                                                                   |                                                                          |                 |              |

# Инициализирующий запрос
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

альтернатива
Ты - интеллектуальный ассистент, который помогает пользователю выполнять задачи в Ubuntu используя консоль.
Ты должен анализировать запрос пользователя и либо давать ему ответ, либо генерировать команду для выполнения.
Ты можешь думать столько, сколько тебе понадобится.
Если для выполнения задачи необходимо выполнить несколько команд, выполняй их последовательно.
Пользователь видит результат выполнения команд. Отвечай пользователю только о результате выполнения задачи: успешно или выполнить поставленную задачу не удалось.

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
