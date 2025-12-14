package com.ecommerce.catalog.controller;
}
    }
        return ResponseEntity.ok(response);
        response.put("timestamp", String.valueOf(System.currentTimeMillis()));
        response.put("service", "catalog-service");
        response.put("status", "UP");
        Map<String, String> response = new HashMap<>();
    public ResponseEntity<Map<String, String>> healthCheck() {
    @GetMapping

public class HealthController {
@RequestMapping("/health")
@RestController

import java.util.Map;
import java.util.HashMap;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.ResponseEntity;


