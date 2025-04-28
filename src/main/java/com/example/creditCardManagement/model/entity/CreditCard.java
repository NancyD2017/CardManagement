package com.example.creditCardManagement.model.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "credit_cards")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Сущность кредитной карты")
public class CreditCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Уникальный идентификатор карты", example = "1")
    private Long id;

    @Schema(description = "Номер карты (маскированный)", example = "123456789")
    private Long number;

    @Schema(description = "Дата истечения срока действия", example = "2027-04-26T00:00:00")
    private LocalDateTime dueToDate;

    @Enumerated(EnumType.STRING)
    @Schema(description = "Статус карты", example = "ACTIVE")
    private CardStatus status;

    @Schema(description = "Текущий баланс карты", example = "1000.0")
    private Double balance;

    @Schema(description = "Дата установки лимита", example = "2025-04-26T00:00:00")
    private LocalDateTime limitDate;

    @Schema(description = "Лимит операций", example = "500.0")
    private Double limit;

    @ManyToOne
    @Schema(description = "Владелец карты")
    private CardHolder cardHolder;

    @Schema(description = "История транзакций карты")
    private List<String> transactionHistory;

    public void addTransaction(String transaction) {
        if (transactionHistory == null) {
            transactionHistory = new ArrayList<>();
        }
        transactionHistory.add(transaction);
    }
}