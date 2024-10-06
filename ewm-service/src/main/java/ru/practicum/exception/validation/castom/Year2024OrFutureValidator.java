package ru.practicum.exception.validation.castom;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDateTime;

public class Year2024OrFutureValidator implements ConstraintValidator<Year2024OrFuture, LocalDateTime> {
    private static final LocalDateTime START_OF_2024 = LocalDateTime.of(2024, 1, 1, 0, 0);

    @Override
    public boolean isValid(LocalDateTime date, ConstraintValidatorContext context) {
        if (date == null) {
            return true;
        }
        return !date.isBefore(START_OF_2024);
    }
}

