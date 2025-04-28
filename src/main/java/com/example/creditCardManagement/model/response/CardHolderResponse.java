package com.example.creditCardManagement.model.response;

import com.example.creditCardManagement.model.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Set;

@Schema(description = "Ответ с данными о владельце карты")
@Data
public class CardHolderResponse {
    @Schema(description = "Идентификатор владельца карты", example = "1")
    private Long id;

    @Schema(description = "Имя пользователя", example = "user1")
    private String username;

    @Schema(description = "Электронная почта", example = "user@example.com")
    private String email;

    @Schema(description = "Роли владельца", example = "[\"ROLE_USER\"]")
    private Set<Role> roles;
}