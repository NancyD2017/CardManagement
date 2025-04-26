package com.example.creditCardManagement.validation;

import com.nimbusds.jose.Payload;
import jakarta.validation.Constraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = LimitRequestValidValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface LimitRequestValid {
    String message() default "limit and limitDuration should be specified!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
