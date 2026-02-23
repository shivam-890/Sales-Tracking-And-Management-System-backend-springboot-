package com.company.salestracker.validation.annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.company.salestracker.validation.validator.PermissionValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ElementType.FIELD , ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PermissionValidator.class)
public @interface ValidPermissions {

	String message() default "Invalid permissions, try again permissions cannot empty or maybe doesnt exist ";
	Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
