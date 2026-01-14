package com.joshtechnologygroup.minisocial.web.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "api.service")
@Data
public class ApiConfig {
    private String jwtKey;
    private long jwtExpiry;
}
