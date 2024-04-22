package com.example.eis;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
public class OpenApiConfig implements WebMvcConfigurer {
    @Bean(name = "publicApi")
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
            .group("public-api")
            .pathsToMatch("/api/images/**")
            .build();
    }

    @Bean(name = "privateApi")
    public GroupedOpenApi privateApi() {
        return GroupedOpenApi.builder()
            .group("private-api")
            .pathsToMatch("/api/model")
            .build();
    }
}
