package com.emofy.controllers

import com.emofy.services.UserService
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.{GetMapping, PostMapping, RequestBody, ResponseBody, ModelAttribute}
import com.emofy.models.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.{HttpStatus, ResponseEntity}

@Controller
class LoginFormController @Autowired()(userService: UserService) {

  @GetMapping(Array("/login"))
  def showLoginForm(): String = "loginForm"

  @PostMapping(Array("/login"))
  @ResponseBody
  def loginUser(@ModelAttribute user: User): ResponseEntity[String] = {


    
    try {


      //user can be authenticated either by email or username
      val paramProvided: String = Option(user.username).getOrElse(user.email)


      userService.loginUser(paramProvided, user.password) match {
        case Some(user) =>
            // User authentication successful, you can access the user object here
            ResponseEntity.status(HttpStatus.CREATED).body("Hi, " + user.username + "!. Welcome back!")
            
        case None =>
            // User authentication failed
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Incorrect credentials") 
      }
      ResponseEntity.status(HttpStatus.CREATED).body("User "+ user.username + " logged successfully!")
    } catch {
      case e: Exception =>
        // Handle failure
        ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to login user")
    }
  }

}
