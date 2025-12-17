package com.ecommerce.catalog.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Test
    void testHandleIllegalArgumentException() {
        // Test: IllegalArgumentException returns 400 BAD_REQUEST
        IllegalArgumentException exception = new IllegalArgumentException("Invalid product ID");

        ResponseEntity<String> response = globalExceptionHandler.handleIllegalArgument(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid product ID", response.getBody());
    }

    @Test
    void testHandleIllegalArgumentExceptionWithEmptyMessage() {
        // Test: IllegalArgumentException with empty message
        IllegalArgumentException exception = new IllegalArgumentException("");

        ResponseEntity<String> response = globalExceptionHandler.handleIllegalArgument(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("", response.getBody());
    }

    @Test
    void testHandleIllegalArgumentExceptionWithNullMessage() {
        // Test: IllegalArgumentException with null message
        IllegalArgumentException exception = new IllegalArgumentException();

        ResponseEntity<String> response = globalExceptionHandler.handleIllegalArgument(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}

