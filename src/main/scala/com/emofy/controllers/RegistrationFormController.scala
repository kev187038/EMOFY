package com.emofy.controllers

import com.emofy.services.UserService
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.{GetMapping, PostMapping, RequestBody, ResponseBody, ModelAttribute}
import com.emofy.models.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.{HttpStatus, ResponseEntity}

@Controller
class RegistrationFormController @Autowired()(userService: UserService) {

  @GetMapping(Array("/register"))
  def showRegistrationForm(): String = "registrationForm"


  @PostMapping(Array("/register"))
  @ResponseBody
  def registerUser(@ModelAttribute user: User): ResponseEntity[String] = {
    // Perform validation 
    println("Received:"+ user + user.username + user.password + user.email)
    if (user.username.isEmpty || user.password.isEmpty || user.email.isEmpty) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid user data")
    }

    // Invoke the service layer to register the user
    try {
      userService.registerUser(user)
      ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully")
    } catch {
      case e: Exception =>
        // Handle registration failure
        ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to register user")
    }
  }
  
}
