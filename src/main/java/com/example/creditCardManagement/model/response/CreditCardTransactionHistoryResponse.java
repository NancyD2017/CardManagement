package com.example.creditCardManagement.model.response;

import lombok.Data;

import java.util.List;

@Data
public class CreditCardTransactionHistoryResponse {
    private Long id;
    private Long number;
    private List<String> transactionHistory;
}
