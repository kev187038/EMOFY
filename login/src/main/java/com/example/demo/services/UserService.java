package com.example.demo.services;

import com.example.demo.models.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;


import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final JwtTokenService jwtTokenService;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);


    @Autowired
    public UserService(UserRepository userRepository, JwtTokenService jwtTokenService) {
        this.userRepository = userRepository;
        this.jwtTokenService = jwtTokenService;
    }

    public void registerUser(User user, HttpServletResponse response) {
        
        userRepository.save(user);
        loginUser(user.getEmail(), user.getPassword(), response);
    }

    public Optional<User> loginUser(String providedParam, String password, HttpServletResponse response) {
        // Cerca l'utente nel repository sia per username che per email
        Optional<User> userByUsername = userRepository.findByUsername(providedParam);
        Optional<User> userByEmail = userRepository.findByEmail(providedParam);

        // Verifica se l'utente è stato trovato
        Optional<User> userOptional = userByUsername.isPresent() ? userByUsername : userByEmail;

        // Verifica se la password corrisponde
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (user.getPassword().equals(password)) {
                setAuthenticationCookie(response, user.getId());
                logLogin(user);
                return userOptional;
            }
        }

        // Ritorna un Optional vuoto se l'autenticazione fallisce
        return Optional.empty();
    }


    private void setAuthenticationCookie(HttpServletResponse response, Long userId) {
      
        String sessionToken = jwtTokenService.generateToken(userId);
        
        Cookie cookie = new Cookie("SESSION_TOKEN", sessionToken);
        //cookie.setMaxAge(3600); // Set cookie expiration time (e.g., 1 hour)
        cookie.setHttpOnly( true); // Make cookie accessible only via HTTP (not JavaScript)
        cookie.setSecure(true); // Ensure cookie is sent only over HTTPS
        cookie.setPath("/"); // Set cookie path to root

        response.addCookie(cookie);
           
    }

    private void logLogin(User user) {
        logger.info("[EMOFY] User logged in: {}, id {}", user.getUsername(), user.getId()); // Example log message
    }
}
