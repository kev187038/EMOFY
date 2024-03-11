package com.example.demo.services;

import com.example.demo.models.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void registerUser(User user) {
        // Perform any validation or business logic here
        // For simplicity, you can directly save the user
        userRepository.save(user);
    }

    public Optional<User> loginUser(String providedParam, String password) {
        // Cerca l'utente nel repository sia per username che per email
        Optional<User> userByUsername = userRepository.findByUsername(providedParam);
        Optional<User> userByEmail = userRepository.findByEmail(providedParam);

        // Verifica se l'utente è stato trovato
        Optional<User> userOptional = userByUsername.isPresent() ? userByUsername : userByEmail;

        // Verifica se la password corrisponde
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (user.getPassword().equals(password)) {
                return userOptional;
            }
        }

        // Ritorna un Optional vuoto se l'autenticazione fallisce
        return Optional.empty();
    }

}
