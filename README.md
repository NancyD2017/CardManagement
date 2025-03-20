# Task Management Service

## Описание проекта

Task Management — это REST API сервис для управления задачами и пользователями.

## Стек технологий

- Docker и Docker Compose
- Java 17+
- Maven 3.8+
- PostgreSQL и Redis (автоматически поднимаются через Docker)

## Установка и запуск

1. Клонирование репозитория

Перейдите в папку, в которой Вы желаете сохранить проект и вызовите в ней командную строку. Выполните команды:

git clone https://github.com/NancyD2017/TaskManagement.git

cd TaskManagement\TaskManagement\src\main\resources

2. Запуск инфраструктуры (PostgreSQL + Redis)

docker-compose up -d

Это поднимет контейнеры с PostgreSQL (порт 5434) и Redis (порт 6379).

3. Запуск приложения

cd ..

cd ..

cd ..

.\gradlew bootRun

4. После успешного запуска API доступно по адресу:

http://localhost:8080/taskManagement/

Swagger UI:

http://localhost:8080/swagger-ui.html

