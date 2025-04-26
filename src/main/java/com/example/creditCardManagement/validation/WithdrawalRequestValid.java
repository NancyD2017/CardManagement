package com.example.creditCardManagement.validation;

import com.nimbusds.jose.Payload;
import jakarta.validation.Constraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = WithdrawalRequestValidValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface WithdrawalRequestValid {
    String message() default "fromId and amount should be specified!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
