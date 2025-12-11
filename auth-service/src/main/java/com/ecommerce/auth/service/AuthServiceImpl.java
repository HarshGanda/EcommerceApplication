package com.ecommerce.auth.service;

import com.ecommerce.auth.dto.UserDto;
import com.ecommerce.auth.entity.User;
import com.ecommerce.auth.exception.InvalidCredentialsException;
import com.ecommerce.auth.exception.UserAlreadyExistsException;
import com.ecommerce.auth.exception.UserNotFoundException;
import com.ecommerce.auth.repository.UserRepository;
import com.ecommerce.auth.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class AuthServiceImpl implements IAuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public String login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        return jwtUtil.generateToken(user.getEmail(), user.getRole());
    }

    @Override
    public Boolean validateToken(String token) {
        return jwtUtil.validateToken(token);
    }

    @Override
    public UserDto register(UserDto userDto) {
        // Check if user already exists
        if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException(userDto.getEmail());
        }

        User user = new User();
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setRole(userDto.getRole() != null ? userDto.getRole() : "USER");
        User savedUser = userRepository.save(user);

        // Send notification via Kafka
        String message = String.format("{\"to\":\"%s\",\"subject\":\"Welcome to ECommerce\",\"body\":\"Thank you for registering!\"}",
                savedUser.getEmail());
        kafkaTemplate.send("notification-events", message);

        UserDto response = new UserDto();
        response.setId(savedUser.getId());
        response.setEmail(savedUser.getEmail());
        response.setRole(savedUser.getRole());
        return response;
    }

    @Override
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        return dto;
    }
}
