package com.example.demo.services;

import com.example.demo.models.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public User createUser(User user) {
        // Qui puoi aggiungere logica aggiuntiva come la validazione dei dati prima di salvare l'utente nel repository
        return userRepository.save(user);
    }

    public User updateUser(Long id, User userDetails) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setUsername(userDetails.getUsername());
            user.setRole(userDetails.getRole());
            user.setEmail(userDetails.getEmail());
            user.setPassword(userDetails.getPassword());
            return userRepository.save(user);
        } else {
            return null; // Utente non trovato
        }
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public void registerUser(User user) {
        // Perform any validation or business logic here
        // For simplicity, you can directly save the user
        createUser(user);
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
