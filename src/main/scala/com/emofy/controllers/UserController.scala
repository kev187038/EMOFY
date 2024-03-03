package com.emofy.controllers

import com.emofy.models.User
import com.emofy.services.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.{HttpStatus, ResponseEntity}
import org.springframework.web.bind.annotation._

@RestController
@RequestMapping(Array("/users"))
class UserController @Autowired()(userService: UserService) {

  
}
