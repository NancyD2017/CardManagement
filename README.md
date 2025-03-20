**Task Management Service**
Task Management — это REST API сервис для управления задачами и пользователями.

**Стек технологий**
Docker и Docker Compose
Java 17+
Maven 3.8+
PostgreSQL и Redis (автоматически поднимаются через Docker)

**Установка и запуск**
_1) Клонирование репозитория_
sh
git clone https://github.com/your-repo/task-management.git
cd task-management
_2) Запуск инфраструктуры (PostgreSQL + Redis)_
sh
docker-compose up -d
Это поднимет контейнеры с PostgreSQL (порт 5434) и Redis (порт 6379).
_3) Запуск приложения_
sh
./mvnw spring-boot:run
или
sh
mvn spring-boot:run
_4) После успешного запуска API доступно по адресу:_
http://localhost:8080/taskManagement/
Swagger UI:
http://localhost:8080/swagger-ui.html
