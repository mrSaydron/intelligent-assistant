# Описание
Здесь должно быть описание

# Запуск

# Установка
Проект использует контейнеризатор для LLM - [ollama](https://ollama.com/)
После установки ollama необходимо загрузить одну из моделей, например llama3.2
Загрузка модели производится командой ```ollama run llama3.2```
Список доступных моделей можно посмотреть на странице https://github.com/ollama/ollama?tab=readme-ov-file

Сборка проекта производится командой: ```./gradlew fatJar```
Для запуска в корне проекта лежит скрипт: assistant запускающий собраный jar файл
