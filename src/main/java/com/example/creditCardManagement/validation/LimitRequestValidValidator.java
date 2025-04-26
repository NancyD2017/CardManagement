package com.example.creditCardManagement.validation;

import com.example.creditCardManagement.model.request.UpsertLimitRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.ObjectUtils;

public class LimitRequestValidValidator implements ConstraintValidator<LimitRequestValid, UpsertLimitRequest> {
    @Override
    public boolean isValid(UpsertLimitRequest value, ConstraintValidatorContext context) {
        return !ObjectUtils.anyNull(value.getLimit(), value.getLimitDuration());
    }
}
