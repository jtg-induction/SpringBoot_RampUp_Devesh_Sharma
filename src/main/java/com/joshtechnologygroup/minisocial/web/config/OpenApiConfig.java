package com.joshtechnologygroup.minisocial.web.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Mini Social Network API")
                        .version("1.0")
                        .description("API documentation for Mini Social Network application."))
                .components(new Components()
                        .addSecuritySchemes("BearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }

//    @Bean
//    public ModelResolver modelResolver(ObjectMapper objectMapper) {
//        // This ensures SpringDoc uses the same ObjectMapper as your Spring Boot app
//        return new ModelResolver(new com.fasterxml.jackson.databind.ObjectMapper(objectMapper));
//    }
}
