package com.example.task_management.validation;

import com.example.task_management.model.request.UpsertTaskRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.ObjectUtils;

public class TaskRequestValidValidator implements ConstraintValidator<TaskRequestValid, UpsertTaskRequest> {
    @Override
    public boolean isValid(UpsertTaskRequest value, ConstraintValidatorContext context) {
        return !ObjectUtils.anyNull(value.getTitle(), value.getAuthorId(), value.getAssigneeId());
    }
}
