package com.example.creditCardManagement.validation;

import com.nimbusds.jose.Payload;
import jakarta.validation.Constraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = CreditCardFilterValidValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CreditCardFilterValid {
    String message() default "Fields of pagination should be specified!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
