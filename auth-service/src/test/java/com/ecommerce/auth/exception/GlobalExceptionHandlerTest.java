package com.ecommerce.auth.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Mock
    private WebRequest webRequest;

    @BeforeEach
    void setUp() {
        when(webRequest.getDescription(false)).thenReturn("uri=/test");
    }

    @Test
    void testHandleUserNotFoundException() {
        // Test: UserNotFoundException returns 404 with proper error response
        UserNotFoundException exception = new UserNotFoundException("User not found");

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleUserNotFound(exception, webRequest);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("User not found", response.getBody().getMessage());
        assertEquals(404, response.getBody().getStatus());
        assertNotNull(response.getBody().getTimestamp());
        assertEquals("uri=/test", response.getBody().getPath());
    }

    @Test
    void testHandleUserAlreadyExistsException() {
        // Test: UserAlreadyExistsException returns 409 CONFLICT
        UserAlreadyExistsException exception = new UserAlreadyExistsException("User already exists");

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleUserAlreadyExists(exception, webRequest);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("User already exists with email: User already exists", response.getBody().getMessage());
        assertEquals(409, response.getBody().getStatus());
    }

    @Test
    void testHandleInvalidCredentialsException() {
        // Test: InvalidCredentialsException returns 401 UNAUTHORIZED
        InvalidCredentialsException exception = new InvalidCredentialsException("Invalid credentials");

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleInvalidCredentials(exception, webRequest);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invalid credentials", response.getBody().getMessage());
        assertEquals(401, response.getBody().getStatus());
    }

    @Test
    void testHandleInvalidTokenException() {
        // Test: InvalidTokenException returns 401 UNAUTHORIZED
        InvalidTokenException exception = new InvalidTokenException("Invalid token");

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleInvalidToken(exception, webRequest);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invalid token", response.getBody().getMessage());
        assertEquals(401, response.getBody().getStatus());
    }

    @Test
    void testHandleGlobalException() {
        // Test: Generic Exception returns 500 INTERNAL_SERVER_ERROR
        Exception exception = new Exception("Unexpected error");

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleGlobalException(exception, webRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("An unexpected error occurred: Unexpected error", response.getBody().getMessage());
        assertEquals(500, response.getBody().getStatus());
    }
}

