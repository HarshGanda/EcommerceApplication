package com.ecommerce.auth.controller;

import com.ecommerce.auth.dto.TokenValidationRequest;
import com.ecommerce.auth.dto.UserDto;
import com.ecommerce.auth.exception.GlobalExceptionHandler;
import com.ecommerce.auth.exception.InvalidCredentialsException;
import com.ecommerce.auth.exception.UserNotFoundException;
import com.ecommerce.auth.service.IAuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private IAuthService authService;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testLoginAndLogout() throws Exception {
        // Test: Valid login, invalid credentials, non-existent user, logout
        UserDto validUser = new UserDto();
        validUser.setEmail("test@example.com");
        validUser.setPassword("password123");
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test.token";

        when(authService.login("test@example.com", "password123")).thenReturn(token);

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(token))
                .andExpect(jsonPath("$.message").value("Login successful"));

        UserDto invalidUser = new UserDto();
        invalidUser.setEmail("test@example.com");
        invalidUser.setPassword("wrongpassword");
        when(authService.login("test@example.com", "wrongpassword"))
                .thenThrow(new InvalidCredentialsException());

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").exists());

        UserDto nonExistentUser = new UserDto();
        nonExistentUser.setEmail("notfound@example.com");
        nonExistentUser.setPassword("password123");
        when(authService.login("notfound@example.com", "password123"))
                .thenThrow(new InvalidCredentialsException());

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nonExistentUser)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").exists());

        mockMvc.perform(post("/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Logged out successfully"));
    }

    @Test
    void testValidateToken() throws Exception {
        // Test: Valid token, invalid token, expired token
        TokenValidationRequest request = new TokenValidationRequest();
        request.setToken("valid.jwt.token");

        when(authService.validateToken(anyString())).thenReturn(true);

        mockMvc.perform(post("/auth/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true));

        request.setToken("invalid.jwt.token");
        when(authService.validateToken("invalid.jwt.token")).thenReturn(false);

        mockMvc.perform(post("/auth/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(false));

        request.setToken("expired.jwt.token");
        when(authService.validateToken("expired.jwt.token")).thenReturn(false);

        mockMvc.perform(post("/auth/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(false));
    }
}

