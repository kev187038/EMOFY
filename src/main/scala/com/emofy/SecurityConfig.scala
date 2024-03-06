package com.emofy
/*
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter

@Configuration
@EnableWebSecurity
class SecurityConfig extends WebSecurityConfigurerAdapter {
  override def configure(http: HttpSecurity): Unit = {
    http.authorizeRequests.anyRequest.permitAll
  }
}
*/

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
