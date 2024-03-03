package com.emofy.controllers

import com.emofy.models
import com.emofy.controllers
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation._
import javax.sql.DataSource
import org.springframework.http.HttpStatus
import org.springframework.http.HttpHeaders
import org.springframework.security.core.context.SecurityContextHolder


@RestController
class HomepageController {
  @GetMapping(Array("/"))
  def getHome(): String = "Welcome to  EMOFY! Please register! <p> Registrati, fallito: <a href='/register'>Register Now</a></p>"

}
