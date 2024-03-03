package com.emofy.services

import com.emofy.models.User
import com.emofy.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UserService @Autowired()(userRepository: UserRepository) {
  def registerUser(user: User): Unit = {
    // Perform any validation or business logic here
    // For simplicity, you can directly save the user
    userRepository.save(user)
  }
}

