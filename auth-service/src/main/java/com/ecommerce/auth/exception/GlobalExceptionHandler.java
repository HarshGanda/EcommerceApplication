package com.ecommerce.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse(
            ex.getMessage(),
            HttpStatus.NOT_FOUND.value(),
            java.time.LocalDateTime.now(),
            request.getDescription(false)
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExists(UserAlreadyExistsException ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse(
            ex.getMessage(),
            HttpStatus.CONFLICT.value(),
            java.time.LocalDateTime.now(),
            request.getDescription(false)
        );
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentials(InvalidCredentialsException ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse(
            ex.getMessage(),
            HttpStatus.UNAUTHORIZED.value(),
            java.time.LocalDateTime.now(),
            request.getDescription(false)
        );
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidToken(InvalidTokenException ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse(
            ex.getMessage(),
            HttpStatus.UNAUTHORIZED.value(),
            java.time.LocalDateTime.now(),
            request.getDescription(false)
        );
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse(
            "An unexpected error occurred: " + ex.getMessage(),
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            java.time.LocalDateTime.now(),
            request.getDescription(false)
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

