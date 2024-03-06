package com.emofy

import org.springframework.context.annotation.{Bean, Configuration}
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain


@Configuration
@EnableWebSecurity
class SecurityConfig {

  @Bean
  def securityFilterChain(http: HttpSecurity): SecurityFilterChain = {
    http.authorizeRequests()
      .anyRequest().permitAll()
    http.build()
  }
}
