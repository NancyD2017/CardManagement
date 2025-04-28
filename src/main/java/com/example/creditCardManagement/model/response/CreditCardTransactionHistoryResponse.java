package com.example.creditCardManagement.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "Ответ с историей транзакций карты")
@Data
public class CreditCardTransactionHistoryResponse {
    @Schema(description = "Идентификатор карты", example = "1")
    private Long id;

    @Schema(description = "Номер карты (маскированный)", example = "123456789")
    private Long number;

    @Schema(description = "Список транзакций", example = "[\"2025-04-26: Transfer 200.0\"]")
    private List<String> transactionHistory;
}