package com.example.creditCardManagement.validation;

import com.nimbusds.jose.Payload;
import jakarta.validation.Constraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = CardHolderRequestValidValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CardHolderRequestValid {
    String message() default "All fields (username, password, roles, email) should be specified!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
