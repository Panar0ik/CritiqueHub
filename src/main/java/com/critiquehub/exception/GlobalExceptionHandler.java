package com.critiquehub.exception;

import com.critiquehub.dto.ErrorResponseDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleNotFound(final EntityNotFoundException ex) {
        log.warn("Resource not found: {}", ex.getMessage());
        return buildResponseEntity(HttpStatus.NOT_FOUND, ex.getMessage(), Collections.emptyList());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidation(final MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();

        log.warn("Validation failed: {}", errors);
        return buildResponseEntity(HttpStatus.BAD_REQUEST, "Validation error", errors);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGlobalException(final Exception ex) {
        log.error("Internal Server Error: ", ex);
        String detail = ex.getMessage() != null ? ex.getMessage() : "No message available";
        return buildResponseEntity(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred",
                List.of(detail)
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDto> handleBadRequest(final IllegalArgumentException ex) {
        log.warn("Bad request: {}", ex.getMessage());
        return buildResponseEntity(HttpStatus.BAD_REQUEST, ex.getMessage(), Collections.emptyList());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponseDto> handleConflict(final IllegalStateException ex) {
        log.warn("Conflict: {}", ex.getMessage());
        return buildResponseEntity(HttpStatus.CONFLICT, ex.getMessage(), Collections.emptyList());
    }

    private ResponseEntity<ErrorResponseDto> buildResponseEntity(
            final HttpStatus status,
            final String message,
            final List<String> details
    ) {
        ErrorResponseDto errorResponse = new ErrorResponseDto(
                status.value(),
                status.getReasonPhrase(),
                message,
                System.currentTimeMillis(),
                details
        );
        return new ResponseEntity<>(errorResponse, status);
    }
}
