package com.example.creditCardManagement.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Запрос для обновления токена")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshTokenRequest {
    @Schema(description = "Токен обновления", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String refreshToken;
}