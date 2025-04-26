package com.example.creditCardManagement.validation;

import com.example.creditCardManagement.model.request.UpsertCardHolderRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.ObjectUtils;

public class CardHolderRequestValidValidator implements ConstraintValidator<CardHolderRequestValid, UpsertCardHolderRequest> {
    @Override
    public boolean isValid(UpsertCardHolderRequest value, ConstraintValidatorContext context) {
        if (ObjectUtils.anyNull(value.getEmail(), value.getUsername(), value.getRoles(), value.getPassword()))
            return false;
        return value.getEmail().matches("[A-z0-9.-]+@[A-z0-9.-]+\\.[A-z]{2,6}");
    }
}
