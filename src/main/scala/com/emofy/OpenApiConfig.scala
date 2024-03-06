package com.emofy

import org.springframework.context.annotation.{Bean, Configuration}
import org.springdoc.core.GroupedOpenApi

@Configuration
class OpenApiConfig {

  @Bean
  def publicApi(): GroupedOpenApi = {
    GroupedOpenApi.builder()
      .group("public-api")
      .pathsToMatch("/public/**")
      .build()
  }

  @Bean
  def privateApi(): GroupedOpenApi = {
    GroupedOpenApi.builder()
      .group("private-api")
      .pathsToMatch("/private/**")
      .build()
  }
}
