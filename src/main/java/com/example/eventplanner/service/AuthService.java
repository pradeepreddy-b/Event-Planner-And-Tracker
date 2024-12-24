package com.example.eventplanner.service;

import com.example.eventplanner.model.User;
import com.example.eventplanner.model.enums.Role;
import com.example.eventplanner.repository.UserRepository;
import com.example.eventplanner.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.eventplanner.exception.GlobalExceptionHandler.UserAlreadyExistsException;
import com.example.eventplanner.exception.GlobalExceptionHandler.UserNotFoundException;
import com.example.eventplanner.exception.GlobalExceptionHandler.InvalidCredentialsException;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    private static final Logger logger = Logger.getLogger(AuthService.class.getName());

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    public User registerUser(String email, String username, String password, Role role) {
        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            throw new UserAlreadyExistsException(email);
        }

        User user = new User();
        user.setEmail(email);
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));  // Hash the password
        user.setRole(parseRole(role != null ? role.name() : null));  // Default to Role.USER if null

        return userRepository.save(user);
    }

    public String authenticateUser(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new InvalidCredentialsException();
        }
//
//        return jwtUtils.generateToken(user.getEmail());
        return jwtUtils.generateToken(user.getEmail(), user.getRole().name()); 
    }

    public static class TokenResponse {
        private String token;

        public TokenResponse(String token) {
            this.token = token;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }

    private Role parseRole(String role) {
        if (role == null) {
            return Role.USER; // Default role
        }
        try {
            return Role.valueOf(role.toUpperCase());  // Convert to enum (case-insensitive)
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Invalid role provided: {0}", role);
            return Role.USER;  // Fallback to USER if the role is invalid
        }
    }
    
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email)); // You can use your custom exception or return null
    }
    
    
    public JwtUtils getJwtUtils() {
        return jwtUtils;  // Add this method to get JwtUtils instance if necessary
    }
    
    
}
