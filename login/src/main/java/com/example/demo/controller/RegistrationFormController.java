package com.example.demo.controller;

import com.example.demo.models.User;
import com.example.demo.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletResponse;

@Controller
public class RegistrationFormController {

    private final UserService userService;

    @Autowired
    public RegistrationFormController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String showRegistrationForm() {
        return "registrationForm";
    }

    @PostMapping("/register")
    @ResponseBody
    public ResponseEntity<String> registerUser(@ModelAttribute User user, HttpServletResponse response) {
        // Perform validation
        if (user.getUsername().isEmpty() || user.getPassword().isEmpty() || user.getEmail().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid user data");
        }

        // Invoke the service layer to register the user
        try {
            userService.registerUser(user, response);

            return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
        } catch (Exception e) {
            // Handle registration failure
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to register user");
        }
    }
}
