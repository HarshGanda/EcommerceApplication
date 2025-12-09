package com.ecommerce.auth.controller;

import com.ecommerce.auth.dto.TokenValidationRequest;
import com.ecommerce.auth.dto.UserDto;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody UserDto userDto) {
        // Implement JWT generation logic
        Map<String, String> response = new HashMap<>();
        response.put("token", "jwt_token_here");
        return response;
    }

    @PostMapping("/validate")
    public Map<String, Boolean> validate(@RequestBody TokenValidationRequest request) {
        // Implement JWT validation logic
        Map<String, Boolean> response = new HashMap<>();
        response.put("valid", true);
        return response;
    }

    @PostMapping("/logout")
    public Map<String, String> logout() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Logged out successfully");
        return response;
    }
}