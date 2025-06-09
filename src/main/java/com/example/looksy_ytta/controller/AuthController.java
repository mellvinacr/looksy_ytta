package com.example.looksy_ytta.controller;

import com.example.looksy_ytta.model.User;
import com.example.looksy_ytta.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        try {
            User registeredUser = userService.registerUser(user);
            return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // You would typically handle login via Spring Security's form login or an OAuth2 flow.
    // This endpoint is just for demonstrating basic registration.
    // For actual login, Spring Security handles the authentication process.
    // A successful login would result in a session or JWT token.
    @PostMapping("/login")
    public ResponseEntity<String> login() {
        // This endpoint will be handled by Spring Security's authentication filter.
        // If authentication is successful, Spring Security will redirect or return success.
        // For REST APIs, you'd typically return a JWT here after successful authentication.
        return ResponseEntity.ok("Login successful (handled by Spring Security)");
    }
}