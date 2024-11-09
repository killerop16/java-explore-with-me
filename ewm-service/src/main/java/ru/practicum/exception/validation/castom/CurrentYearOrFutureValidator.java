package ru.practicum.exception.validation.castom;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDateTime;
import java.time.Year;

public class CurrentYearOrFutureValidator implements ConstraintValidator<CurrentYearOrFuture, LocalDateTime> {
    private static final LocalDateTime START_OF_YEAR = LocalDateTime.of(Year.now().getValue(), 1, 1, 0, 0);

    @Override
    public boolean isValid(LocalDateTime date, ConstraintValidatorContext context) {
        if (date == null) {
            return true;
        }
        return !date.isBefore(START_OF_YEAR);
    }
}

