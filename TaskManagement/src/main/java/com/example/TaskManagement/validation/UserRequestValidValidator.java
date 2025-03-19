package com.example.TaskManagement.validation;

import com.example.TaskManagement.model.request.UpsertUserRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.ObjectUtils;

public class UserRequestValidValidator implements ConstraintValidator<UserRequestValid, UpsertUserRequest> {
    @Override
    public boolean isValid(UpsertUserRequest value, ConstraintValidatorContext context) {
        if (ObjectUtils.anyNull(value.getEmail(), value.getUsername(), value.getRoles(), value.getPassword()))
            return false;
        return value.getEmail().matches("[A-z0-9.-]+@[A-z0-9.-]+\\.[A-z]{2,6}");
    }
}
