package com.example.eventplanner.controller;

import com.example.eventplanner.model.User;
import com.example.eventplanner.model.enums.Role;
import com.example.eventplanner.service.AuthService;
import com.example.eventplanner.service.AuthService.TokenResponse;
import com.example.eventplanner.exception.GlobalExceptionHandler.UserAlreadyExistsException;
import com.example.eventplanner.exception.GlobalExceptionHandler.InvalidCredentialsException;
import com.example.eventplanner.exception.GlobalExceptionHandler.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")  // Ensure this matches your security configuration
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // Register User using @RequestBody (for JSON body)
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public User registerUser(@RequestBody User user) {  // Directly use User model
        try {
            // Set the role if not provided
            if (user.getRole() == null) {
                user.setRole(Role.USER); // Default role
            }
            return authService.registerUser(user.getEmail(), user.getUsername(), user.getPassword(), user.getRole());
        } catch (UserAlreadyExistsException e) {
            throw e;
        }
    }

    // Login User using @RequestBody (for JSON body)
    @PostMapping("/login")
    public TokenResponse loginUser(@RequestBody User user) {  // Directly use User model for login
        try {
            String token = authService.authenticateUser(user.getEmail(), user.getPassword());
            return new TokenResponse(token);
        } catch (UserNotFoundException | InvalidCredentialsException e) {
            throw e;
        }
    }
}
