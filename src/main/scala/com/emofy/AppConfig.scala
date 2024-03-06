package com.emofy
/*
import springfox.documentation.swagger2.annotations.EnableSwagger2
import org.springframework.context.annotation.Configuration
import springfox.documentation.spring.web.plugins.Docket
import org.springframework.context.annotation.Bean
import springfox.documentation.spi.DocumentationType
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.builders.PathSelectors
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Configuration
import com.emofy.repository.UserRepository
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.context.annotation.ComponentScan

@Configuration
@EnableSwagger2
class SwaggerConfig {

  @Bean
  def api(): Docket = {
    new Docket(DocumentationType.SWAGGER_2)
      .select
      .apis(RequestHandlerSelectors.any)
      .paths(PathSelectors.any)
      .build
  }

}



@Configuration
@EnableJpaRepositories(basePackageClasses = Array(classOf[UserRepository]))
@ComponentScan(basePackages = Array("com.emofy")) // Scansione dei componenti di Spring nel package com.emofy
@EntityScan(basePackages = Array("com.emofy.models")) // Scansione delle entità nel package com.emofy.models
class AppConfig {
  // Puoi eventualmente definire altri bean e configurazioni qui
}
*/
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
