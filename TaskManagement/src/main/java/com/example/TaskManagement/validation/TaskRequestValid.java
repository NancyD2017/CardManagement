package com.example.TaskManagement.validation;

import com.nimbusds.jose.Payload;
import jakarta.validation.Constraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = TaskRequestValidValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface TaskRequestValid {
    String message() default "Title, authorId and assigneeId should be specified!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
