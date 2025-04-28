package com.example.creditCardManagement.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Schema(description = "Ответ со списком историй транзакций карт")
@Data
public class CreditCardTransactionHistoryListResponse {
    @Schema(description = "Список историй транзакций карт")
    private List<CreditCardTransactionHistoryResponse> creditCards = new ArrayList<>();
}