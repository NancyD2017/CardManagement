package com.example.creditCardManagement.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Ответ с обновленными токенами")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefreshTokenResponse {
    @Schema(description = "Новый токен доступа", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String accessToken;

    @Schema(description = "Новый токен обновления", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String refreshToken;
}
