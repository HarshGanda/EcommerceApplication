package com.ecommerce.auth.service;

import com.ecommerce.auth.dto.UserDto;

public interface IAuthService {
    String login(String email, String password);
    Boolean validateToken(String token);
    UserDto register(UserDto userDto);
    UserDto getUserById(Long id);
}

