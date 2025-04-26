package com.example.creditCardManagement.model.response;

import com.example.creditCardManagement.model.entity.CardStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CreditCardResponse {
    private Long id;
    private Long number;
    private LocalDateTime dueToDate;
    private CardStatus status;
    private Double balance;
    private List<String> transactionHistory;
    private CardHolderResponse cardHolder;
}
