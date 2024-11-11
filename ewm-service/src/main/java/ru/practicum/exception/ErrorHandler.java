package ru.practicum.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.exception.validation.ConflictException;
import ru.practicum.exception.validation.DateException;
import ru.practicum.exception.validation.ResourceNotFoundException;

import java.sql.SQLException;
import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    //409
    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError fullLimitHandle(ConflictException exception) {
        log.error("409 {}", exception.getMessage(), exception);
        return new ApiError(
                HttpStatus.CONFLICT,
                "Integrity constraint has been violated.",
                exception.getMessage(),
                LocalDateTime.now()
        );
    }


    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError fullLimitHandle(ConstraintViolationException exception) {
        log.error("409 {}", exception.getMessage(), exception);
        return new ApiError(
                HttpStatus.CONFLICT,
                "Integrity constraint has been violated.",
                exception.getMessage(),
                LocalDateTime.now()
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError conflictHandle(IllegalArgumentException exception) {
        log.error("409 {}", exception.getMessage(), exception);
        return new ApiError(
                HttpStatus.CONFLICT,
                "Integrity constraint has been violated.",
                exception.getMessage(),
                LocalDateTime.now()
        );
    }

    @ExceptionHandler(SQLException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError psqlValidationHandle(SQLException exception) {
        log.error("409 {}", exception.getMessage(), exception);
        return new ApiError(
                HttpStatus.CONFLICT,
                "Integrity constraint has been violated.",
                exception.getMessage(),
                LocalDateTime.now()
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError psqlValidationHandle(DataIntegrityViolationException exception) {
        log.error("409 {}", exception.getMessage(), exception);
        return new ApiError(
                HttpStatus.CONFLICT,
                "Integrity constraint has been violated.",
                exception.getMessage(),
                LocalDateTime.now()
        );
    }

    //404
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError notFoundHandle(ResourceNotFoundException exception) {
        log.error("404 {}", exception.getMessage(), exception);
        return new ApiError(
                HttpStatus.NOT_FOUND,
                "The required object was not found.",
                exception.getMessage(),
                LocalDateTime.now()
        );
    }


    //400
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError validationHandle(MethodArgumentNotValidException exception) {
        log.error("400 {}", exception.getMessage(), exception);
        return new ApiError(
                HttpStatus.BAD_REQUEST,
                "Incorrectly made request.",
                exception.getMessage(),
                LocalDateTime.now()
        );
    }

    @ExceptionHandler(DateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError validationHandle(final DateException exception) {
        log.error("400 {}", exception.getMessage(), exception);
        return new ApiError(
                HttpStatus.BAD_REQUEST,
                "Incorrectly made request.",
                exception.getMessage(),
                LocalDateTime.now()
        );
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError badRequestHandle(BadRequestException exception) {
        log.error("400 {}", exception.getMessage(), exception);
        return new ApiError(
                HttpStatus.BAD_REQUEST,
                "Incorrectly made request.",
                exception.getMessage(),
                LocalDateTime.now()
        );
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError missingServletRequestParameterHandle(MissingServletRequestParameterException exception) {
        log.error("400 {}", exception.getMessage(), exception);
        return new ApiError(
                HttpStatus.BAD_REQUEST,
                "Incorrectly made request.",
                exception.getMessage(),
                LocalDateTime.now()
        );
    }

    //500
    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError mainExceptionHandle(Throwable throwable) {
        log.error("500 {}", throwable.getMessage(), throwable);
        return new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Oops, something went wrong...",
                throwable.getMessage(),
                LocalDateTime.now()
        );
    }
}
