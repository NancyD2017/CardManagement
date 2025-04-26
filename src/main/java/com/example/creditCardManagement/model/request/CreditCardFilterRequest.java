package com.example.creditCardManagement.model.request;

import com.example.creditCardManagement.validation.CreditCardFilterValid;
import lombok.Data;

@Data
@CreditCardFilterValid
public class CreditCardFilterRequest {
    private Long cardHolderId;
    private String transactionHistory;
    private Integer pageSize = 10;
    private Integer pageNumber = 0;
}
