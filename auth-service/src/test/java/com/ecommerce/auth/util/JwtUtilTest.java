package com.ecommerce.auth.util;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtUtilTest {

    @InjectMocks
    private JwtUtil jwtUtil;

    private static final String TEST_SECRET = "testSecretKeyForJWTTokenGenerationAndValidation12345";
    private static final Long TEST_EXPIRATION = 3600000L; // 1 hour

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtUtil, "secret", TEST_SECRET);
        ReflectionTestUtils.setField(jwtUtil, "expiration", TEST_EXPIRATION);
    }

    @Test
    void testTokenGenerationAndExtraction() {
        // Test: Generate token, extract username, role, expiration
        String email = "test@example.com";
        String role = "ADMIN";
        String token = jwtUtil.generateToken(email, role);

        assertNotNull(token);
        assertEquals(3, token.split("\\.").length); // JWT has 3 parts
        assertEquals(email, jwtUtil.extractUsername(token));
        assertTrue(jwtUtil.extractExpiration(token).after(new Date()));

        Claims claims = jwtUtil.extractClaim(token, claims1 -> claims1);
        assertEquals(role, claims.get("role"));
        assertEquals(email, claims.getSubject());
    }

    @Test
    void testTokenValidation() {
        // Test: Valid token, invalid token, malformed token, wrong email, expired token
        String email = "test@example.com";
        String token = jwtUtil.generateToken(email, "USER");

        assertTrue(jwtUtil.validateToken(token));
        assertTrue(jwtUtil.validateToken(token, email));
        assertFalse(jwtUtil.validateToken(token, "wrong@example.com"));
        assertFalse(jwtUtil.validateToken("invalid.jwt.token"));
        assertFalse(jwtUtil.validateToken(""));

        // Test expired token
        ReflectionTestUtils.setField(jwtUtil, "expiration", -1000L);
        String expiredToken = jwtUtil.generateToken(email, "USER");
        ReflectionTestUtils.setField(jwtUtil, "expiration", TEST_EXPIRATION);
        assertFalse(jwtUtil.validateToken(expiredToken));
    }
}

