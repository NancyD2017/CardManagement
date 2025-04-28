package com.example.creditCardManagement.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "Ответ с данными о кредитной карте")
public class CreditCardResponse {
    @Schema(description = "Уникальный идентификатор карты", example = "1")
    private Long id;

    @Schema(description = "Номер карты (маскированный)", example = "123456789")
    private Long number;

    @Schema(description = "Текущий баланс карты", example = "1000.0")
    private Double balance;

    @Schema(description = "Статус карты", example = "ACTIVE")
    private String status;

    @Schema(description = "Срок службы карты", example = "2025-04-26T00:00:00")
    private LocalDateTime dueToDate;

    @Schema(description = "История транзакций", example = "2025-04-26T00:00:00 : Added limit 25000.0 per month")
    private List<String> transactionHistory;

    @Schema(description = "Владелец карты")
    private CardHolderResponse cardHolder;
}
