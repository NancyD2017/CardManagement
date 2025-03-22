package com.example.task_management.validation;

import com.nimbusds.jose.Payload;
import jakarta.validation.Constraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = LoginRequestValidValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface LoginRequestValid {
    String message() default "Email and password should be specified!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
