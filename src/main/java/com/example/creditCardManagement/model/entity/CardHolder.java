package com.example.creditCardManagement.model.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Schema(description = "Сущность владельца карты")
@Entity
@Table(name = "card_holders")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardHolder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Уникальный идентификатор владельца карты", example = "1")
    private Long id;

    @Schema(description = "Имя пользователя", example = "user1")
    private String username;

    @Schema(description = "Электронная почта", example = "user@example.com")
    private String email;

    @Schema(description = "Пароль (зашифрованный)", example = "encrypted_password")
    private String password;

    @OneToMany(mappedBy = "cardHolder")
    @Schema(description = "Список кредитных карт владельца")
    private List<CreditCard> creditCards;

    @ElementCollection
    @Schema(description = "Список ролей пользователя", example = "[\"ROLE_USER\"]")
    private Set<Role> roles;

    public void addCard(CreditCard c) {
        if (creditCards == null) {
            creditCards = new ArrayList<>();
        }
        creditCards.add(c);
        c.setCardHolder(this);
    }
}