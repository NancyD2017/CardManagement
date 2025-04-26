package com.example.creditCardManagement.validation;

import com.example.creditCardManagement.model.request.UpsertWithdrawalRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.ObjectUtils;

public class WithdrawalRequestValidValidator implements ConstraintValidator<WithdrawalRequestValid, UpsertWithdrawalRequest> {
    @Override
    public boolean isValid(UpsertWithdrawalRequest value, ConstraintValidatorContext context) {
        return !ObjectUtils.anyNull(value.getFromId(), value.getAmount());
    }
}
