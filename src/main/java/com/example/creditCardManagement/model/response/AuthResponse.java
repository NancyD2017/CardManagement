package com.example.creditCardManagement.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(description = "Ответ с данными аутентификации")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthResponse {
    @Schema(description = "Идентификатор пользователя", example = "1")
    private Long id;

    @Schema(description = "JWT токен доступа", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String token;

    @Schema(description = "Токен обновления", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String refreshToken;

    @Schema(description = "Имя пользователя", example = "user1")
    private String username;

    @Schema(description = "Роли пользователя", example = "[\"ROLE_USER\"]")
    private List<String> roles;
}