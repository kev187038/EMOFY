package com.example.demo.controller;

import com.example.demo.models.User;
import com.example.demo.services.UserService;
import com.example.demo.services.JwtTokenService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class LoginFormController {

    private final UserService userService;

    @Autowired
    public LoginFormController(UserService userService) {
        this.userService = userService;

    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "loginForm";
    }

    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<String> loginUser(@ModelAttribute User user, HttpServletResponse response) {
        try {
            
            // User can be authenticated either by email or username
       
            
            String paramProvided = user.getUsername() != null ? user.getUsername() : user.getEmail();
    
            Optional<User> authenticatedUser = userService.loginUser(paramProvided, user.getPassword(), response);
            if (authenticatedUser.isPresent()) {
                // User authentication successful
                response.sendRedirect("http://emofy-image-storage:8081"/*"http://localhost:8081" */);
                return ResponseEntity.status(HttpStatus.CREATED).body("Hi, " + authenticatedUser.get().getUsername() + ". Welcome back!");
            } else {
                // User authentication failed
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Incorrect credentials");
            }
        } catch (Exception e) {
            // Handle failure
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to login user");
        }
    }
}
