package com.example.creditCardManagement.model.entity;

import com.example.creditCardManagement.configuration.AesEncryptor;
import com.fasterxml.jackson.annotation.JsonFormat;
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
@Schema(description = "Модель банковской карты")
public class CreditCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Идентификатор карты", example = "1")
    private Long id;

    @Column(unique = true)
    @Convert(converter = AesEncryptor.class)
    @Schema(description = "Номер карты", example = "123456789")
    private Long number;

    @JsonFormat(pattern = "yyyy-MM-dd'T'00:00:00")
    @Schema(description = "Срок действия карты", example = "2029-04-26T00:00:00")
    private LocalDateTime dueToDate;

    @Enumerated(EnumType.STRING)
    @Schema(description = "Статус карты", example = "ACTIVE")
    private CardStatus status;

    @Schema(description = "Баланс карты", example = "1000.0")
    private Double balance;

    @Schema(description = "Дата действия лимита", example = "2025-05-26T00:00:00")
    private LocalDateTime limitDate;

    @Column(name = "credit_limit")
    @Schema(description = "Кредитный лимит", example = "500.0")
    private Double limit;

    @Column(name = "remaining_balance")
    @Schema(description = "Оставшийся баланс", example = "500.0")
    private Double left;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_holder_id")
    @Schema(description = "Владелец карты")
    private CardHolder cardHolder;

    @ElementCollection
    @Column(name = "transaction_history")
    @Schema(description = "История транзакций", example = "[\"2025-04-26T10:00:00: Transfer to card 2 200.0 roubles\"]")
    private List<String> transactionHistory = new ArrayList<>();

    public void addTransaction(String transaction) {
        if (transactionHistory == null) {
            transactionHistory = new ArrayList<>();
        }
        transactionHistory.add(transaction);
    }
}