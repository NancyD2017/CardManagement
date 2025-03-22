package com.example.task_management;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@OpenAPIDefinition(
        info = @Info(
                title = "Task Management API",
                version = "1.0",
                description = "API для управления задачами и пользователями."
        ),
        servers = {
                @Server(url = "http://localhost:8080", description = "Локальный сервер")
        }
)
@SpringBootApplication
@ComponentScan(basePackages = "com.example.task_management")
public class TaskManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskManagementApplication.class, args);
    }

}
