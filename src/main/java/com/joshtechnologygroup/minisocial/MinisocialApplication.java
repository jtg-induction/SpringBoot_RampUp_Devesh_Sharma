package com.joshtechnologygroup.minisocial;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MinisocialApplication {

    public static void main(String[] args) {
        SpringApplication.run(MinisocialApplication.class, args);
    }
}
