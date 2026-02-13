package com.example.todoapp.controller;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import com.example.todoapp.entity.User;
import com.example.todoapp.dto.SignupRequest;
import com.example.todoapp.dto.UserResponse;
import com.example.todoapp.dto.LoginRequest;
import com.example.todoapp.dto.LoginResponse;
import com.example.todoapp.service.UserService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService){
        this.userService = userService;
    }

    @PostMapping("/register")
    public UserResponse register(@Valid @RequestBody SignupRequest request) {
        User user = userService.signup(request.getEmail(), request.getPassword());
        return new UserResponse(user);
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        String accessToken = userService.login(request.getEmail(), request.getPassword());
        return new LoginResponse(accessToken, "Bearer");
    }
}
