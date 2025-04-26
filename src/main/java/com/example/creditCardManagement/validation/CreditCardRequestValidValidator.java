package com.example.creditCardManagement.validation;

import com.example.creditCardManagement.model.request.UpsertCreditCardRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.ObjectUtils;

public class CreditCardRequestValidValidator implements ConstraintValidator<CreditCardRequestValid, UpsertCreditCardRequest> {
    @Override
    public boolean isValid(UpsertCreditCardRequest value, ConstraintValidatorContext context) {
        return !ObjectUtils.anyNull(value.getNumber(), value.getCardHolderId());
    }
}
