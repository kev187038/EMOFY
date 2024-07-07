package com.example.eis.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class LogoutController {

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        // Create a cookie with the same name and set its expiration date in the past to delete it
        Cookie jwtCookie = new Cookie("SESSION_TOKEN", null);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(0); // Set the max age to 0 to delete the cookie
        jwtCookie.setSecure(true);
        jwtCookie.setHttpOnly(true);
        
        // Add the cookie to the response
        response.addCookie(jwtCookie);

        return ResponseEntity.ok().build();
    }
}
