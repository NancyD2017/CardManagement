package com.example.creditCardManagement.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Setter
@Getter
@Schema(description = "Запрос для обновления токена")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshTokenRequest {
    @Schema(description = "Токен обновления", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String refreshToken;

}