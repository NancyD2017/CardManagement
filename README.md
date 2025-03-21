# Task Management Service

## Описание проекта

Task Management — это REST API сервис для управления задачами и пользователями.

## Стек технологий

- Java 17+
- Spring Boot, Spring Security, Spring Data JPA
- MySQL (как база данных)
- Maven (для сборки проекта)
- Docker и Docker Compose (для запуска MySQL)
- Swagger (для документации API)

## Установка и запуск

1. Клонирование репозитория

Перейдите в папку, в которой Вы желаете сохранить проект и вызовите в ней командную строку. Выполните команды:

git clone https://github.com/NancyD2017/TaskManagement.git

cd TaskManagement\TaskManagement\src\main\resources

2. Запуск инфраструктуры (MySQL)

docker-compose up -d

3. Запуск приложения

cd ..

cd ..

cd ..

.\gradlew bootRun

4. После успешного запуска API доступно по адресу:

http://localhost:8080/taskManagement/

Swagger UI:

http://localhost:8080/swagger-ui.html

