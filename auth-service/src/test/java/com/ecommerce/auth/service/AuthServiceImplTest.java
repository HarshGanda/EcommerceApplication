package com.ecommerce.auth.service;

import com.ecommerce.auth.dto.UserDto;
import com.ecommerce.auth.entity.User;
import com.ecommerce.auth.exception.InvalidCredentialsException;
import com.ecommerce.auth.exception.UserAlreadyExistsException;
import com.ecommerce.auth.exception.UserNotFoundException;
import com.ecommerce.auth.repository.UserRepository;
import com.ecommerce.auth.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @InjectMocks
    private AuthServiceImpl authService;

    private User testUser;
    private UserDto testUserDto;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setRole("USER");

        testUserDto = new UserDto();
        testUserDto.setEmail("test@example.com");
        testUserDto.setPassword("password123");
        testUserDto.setRole("USER");
    }

    @Test
    void testLoginAndValidation() {
        // Test: Valid login, invalid email, invalid password, token validation
        String email = "test@example.com";
        String password = "password123";
        String token = "jwt.token.here";

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(password, testUser.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken(email, testUser.getRole())).thenReturn(token);
        when(jwtUtil.validateToken(token)).thenReturn(true);
        when(jwtUtil.validateToken("invalid")).thenReturn(false);

        assertEquals(token, authService.login(email, password));
        assertTrue(authService.validateToken(token));
        assertFalse(authService.validateToken("invalid"));

        when(userRepository.findByEmail("wrong@example.com")).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> authService.login("wrong@example.com", password));

        when(passwordEncoder.matches("wrongPassword", testUser.getPassword())).thenReturn(false);
        assertThrows(InvalidCredentialsException.class, () -> authService.login(email, "wrongPassword"));
    }

    @Test
    void testRegister() {
        // Test: New user, existing user, null role defaults to USER, password encoding, Kafka notification
        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setEmail(testUserDto.getEmail());
        savedUser.setPassword("encodedPassword");
        savedUser.setRole("USER");

        when(userRepository.findByEmail(testUserDto.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(testUserDto.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserDto result = authService.register(testUserDto);
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(testUserDto.getEmail(), result.getEmail());
        assertEquals("USER", result.getRole());
        assertNull(result.getPassword());
        verify(kafkaTemplate, times(1)).send(eq("notification-events"), contains(testUserDto.getEmail()));

        when(userRepository.findByEmail(testUserDto.getEmail())).thenReturn(Optional.of(testUser));
        assertThrows(UserAlreadyExistsException.class, () -> authService.register(testUserDto));
    }

    @Test
    void testGetUserById() {
        // Test: Existing user, non-existing user, password not returned
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        UserDto result = authService.getUserById(1L);
        assertEquals(1L, result.getId());
        assertEquals(testUser.getEmail(), result.getEmail());
        assertNull(result.getPassword());

        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> authService.getUserById(999L));
    }
}

