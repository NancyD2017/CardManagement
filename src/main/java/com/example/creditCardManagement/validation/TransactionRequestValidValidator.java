package com.example.creditCardManagement.validation;

import com.example.creditCardManagement.model.request.UpsertTransactionRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.ObjectUtils;

public class TransactionRequestValidValidator implements ConstraintValidator<TransactionRequestValid, UpsertTransactionRequest> {
    @Override
    public boolean isValid(UpsertTransactionRequest value, ConstraintValidatorContext context) {
        return !ObjectUtils.anyNull(value.getToId(), value.getFromId(), value.getAmount());
    }
}
