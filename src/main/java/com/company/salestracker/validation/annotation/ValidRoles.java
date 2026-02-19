package com.company.salestracker.validation.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.company.salestracker.validation.validator.RoleValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = RoleValidator.class)
public @interface ValidRoles {

    String message() default "Invalid roles provided,either roles is empty or roles is not exist";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

