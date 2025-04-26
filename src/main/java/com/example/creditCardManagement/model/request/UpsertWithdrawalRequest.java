package com.example.creditCardManagement.model.request;

import com.example.creditCardManagement.validation.CreditCardRequestValid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@CreditCardRequestValid
public class UpsertWithdrawalRequest {
    private Long fromId;
    private Double amount;
}