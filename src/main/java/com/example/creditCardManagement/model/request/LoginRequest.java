package com.example.creditCardManagement.model.request;

import com.example.creditCardManagement.validation.LoginRequestValid;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Запрос для аутентификации пользователя")
@Data
@NoArgsConstructor
@AllArgsConstructor
@LoginRequestValid
public class LoginRequest {
    @Schema(description = "Электронная почта пользователя", example = "user@example.com")
    private String email;

    @Schema(description = "Пароль пользователя", example = "password123")
    private String password;
}