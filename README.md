# Credit Card Management

Система управления кредитными картами с аутентификацией и базовыми CRUD-операциями.

## Требования

- Docker 20.10+
- Docker Compose 2.20+
- Java 17

## Локальный запуск

### 1. Клонирование репозитория
git clone https://github.com/NancyD2017/CardManagement.git
cd CardManagement

### 2. Запуск через Docker Compose
docker-compose up --build

Сервисы будут доступны:

 - Приложение: http://localhost:8080
 - Swagger UI: http://localhost:8080/swagger-ui.html
   
 ## Конфигурация
Основные настройки можно изменить в файле docker-compose.yml:

environment:
  SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/credit_card_management
  SPRING_DATASOURCE_USERNAME: user
  SPRING_DATASOURCE_PASSWORD: password
  JWT_SECRET: your-secret-key
