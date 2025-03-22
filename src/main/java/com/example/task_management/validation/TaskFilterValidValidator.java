package com.example.task_management.validation;

import com.example.task_management.model.request.TaskFilterRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.ObjectUtils;

public class TaskFilterValidValidator implements ConstraintValidator<TaskFilterValid, TaskFilterRequest> {
    @Override
    public boolean isValid(TaskFilterRequest value, ConstraintValidatorContext context) {
        if (ObjectUtils.anyNull(value.getPageNumber(), value.getPageSize())) return false;
        return value.getAuthorId() != null || value.getAssigneeId() != null;
    }
}
