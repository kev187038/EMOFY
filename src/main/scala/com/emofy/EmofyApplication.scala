package com.emofy
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.SpringApplication

@SpringBootApplication
class EmofyApplication

object Application extends App {
    SpringApplication.run(classOf[EmofyApplication])
}