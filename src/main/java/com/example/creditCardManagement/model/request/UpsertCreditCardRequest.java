package com.example.creditCardManagement.model.request;

import com.example.creditCardManagement.model.entity.CardStatus;
import com.example.creditCardManagement.validation.CreditCardRequestValid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@CreditCardRequestValid
public class UpsertCreditCardRequest {
    private Long number;
    private Double balance;
    private CardStatus status;
    private Long cardHolderId;
}