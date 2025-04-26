package com.example.creditCardManagement;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@OpenAPIDefinition(
        info = @Info(
                title = "Credit Card Management API",
                version = "1.0",
                description = "API для управления банковскими картами."
        ),
        servers = {
                @Server(url = "http://localhost:8080", description = "Локальный сервер")
        }
)
@SpringBootApplication
@EnableJpaRepositories
public class CreditCardManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(CreditCardManagementApplication.class, args);
    }

}
