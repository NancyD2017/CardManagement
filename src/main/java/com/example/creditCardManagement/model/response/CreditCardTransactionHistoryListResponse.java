package com.example.creditCardManagement.model.response;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CreditCardTransactionHistoryListResponse {
    private List<CreditCardTransactionHistoryResponse> creditCards = new ArrayList<>();
}
