package ru.practicum.exception.validation.castom;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CurrentYearOrFutureValidator.class)
@Documented
public @interface CurrentYearOrFuture {
    String message() default "Date must be in the future or from the start of 2024";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

