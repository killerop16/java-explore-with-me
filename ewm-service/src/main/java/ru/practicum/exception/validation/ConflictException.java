package ru.practicum.exception.validation;

public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}

