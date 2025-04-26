package com.example.creditCardManagement.validation;

import com.example.creditCardManagement.model.request.CreditCardFilterRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.ObjectUtils;

public class CreditCardFilterValidValidator implements ConstraintValidator<CreditCardFilterValid, CreditCardFilterRequest> {
    @Override
    public boolean isValid(CreditCardFilterRequest value, ConstraintValidatorContext context) {
        if (ObjectUtils.anyNull(value.getPageNumber(), value.getPageSize())) return false;
        return value.getCardHolderId() != null || value.getTransactionHistory() != null;
    }
}
