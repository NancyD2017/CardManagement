package com.example.task_management.validation;

import com.nimbusds.jose.Payload;
import jakarta.validation.Constraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = TaskFilterValidValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface TaskFilterValid {
    String message() default "Fields of pagination should be specified!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
