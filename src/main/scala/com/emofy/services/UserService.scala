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


  def loginUser(providedParam: String, password: String): Option[User] = {
    // Retrieve user from the database based on the provided username or email
    val userOptF: Option[User] = userRepository.findByUsername(providedParam)

    val userOpt: Option[User] = userOptF.orElse(userRepository.findByEmail(providedParam))
  
    

    
    userOpt match {
      case Some(user) =>
        // Check if the password matches
        if (user.password == password) {
          // Password matches, return the user
          Some(user)
        } else {
          // Password doesn't match
          None
        }
      case None =>
        // User not found
        None
    }
  }


}

