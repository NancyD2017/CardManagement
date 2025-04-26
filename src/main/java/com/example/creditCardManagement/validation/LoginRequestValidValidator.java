package com.example.creditCardManagement.validation;

import com.example.creditCardManagement.model.request.LoginRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.ObjectUtils;

public class LoginRequestValidValidator implements ConstraintValidator<LoginRequestValid, LoginRequest> {
    @Override
    public boolean isValid(LoginRequest value, ConstraintValidatorContext context) {
        return !ObjectUtils.anyNull(value.getEmail(), value.getPassword());
    }
}
