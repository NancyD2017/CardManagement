package com.example.creditCardManagement.model.request;

import com.example.creditCardManagement.model.entity.LimitDuration;
import com.example.creditCardManagement.validation.CreditCardRequestValid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@CreditCardRequestValid
public class UpsertLimitRequest {
    private LimitDuration limitDuration;
    private Double limit;
}