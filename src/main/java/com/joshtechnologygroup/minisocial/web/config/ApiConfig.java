package com.joshtechnologygroup.minisocial.web.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "api.service")
@Getter
@Setter
public class ApiConfig {
    private String jwtKey;
    private long jwtExpiry;
}
