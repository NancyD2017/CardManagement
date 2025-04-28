package com.example.creditCardManagement.model.request;

import com.example.creditCardManagement.model.entity.Role;
import com.example.creditCardManagement.validation.CardHolderRequestValid;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Schema(description = "Запрос для создания или обновления владельца карты")
@Data
@AllArgsConstructor
@NoArgsConstructor
@CardHolderRequestValid
public class UpsertCardHolderRequest {
    @Schema(description = "Имя пользователя", example = "user1")
    private String username;

    @Schema(description = "Электронная почта", example = "user@example.com")
    private String email;

    @Schema(description = "Роли пользователя", example = "[\"ROLE_USER\"]")
    private Set<Role> roles;

    @Schema(description = "Пароль пользователя", example = "password123")
    private String password;
}