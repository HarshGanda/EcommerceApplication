package com.ecommerce.cart.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Test
    void testHandleRuntimeException() {
        // Test: RuntimeException returns 400 BAD_REQUEST with error details
        RuntimeException exception = new RuntimeException("Invalid cart operation");

        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleRuntimeException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invalid cart operation", response.getBody().get("message"));
        assertEquals(400, response.getBody().get("status"));
        assertEquals("Bad Request", response.getBody().get("error"));
        assertNotNull(response.getBody().get("timestamp"));
    }

    @Test
    void testHandleGenericException() {
        // Test: Generic Exception returns 500 INTERNAL_SERVER_ERROR
        Exception exception = new Exception("Unexpected error occurred");

        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleGenericException(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Unexpected error occurred", response.getBody().get("message"));
        assertEquals(500, response.getBody().get("status"));
        assertEquals("Internal Server Error", response.getBody().get("error"));
        assertNotNull(response.getBody().get("timestamp"));
    }

    @Test
    void testHandleRuntimeExceptionWithNullMessage() {
        // Test: RuntimeException with null message
        RuntimeException exception = new RuntimeException();

        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleRuntimeException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
    }
}

