package com.joshtechnologygroup.minisocial;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.shell.core.ShellRunner;

import java.util.Arrays;

@SpringBootApplication
public class MinisocialApplication {

    public static void main(String[] args) throws Exception {
        boolean isShellMode = Arrays.asList(args)
                .contains("--shell");

        if(isShellMode) {
            SpringApplication app = new SpringApplicationBuilder(MinisocialApplication.class)
                    .web(WebApplicationType.NONE)
                    .build();
            ApplicationContext context = app.run(args);
            ShellRunner runner = context.getBean(ShellRunner.class);
            runner.run(args);
        } else {
            SpringApplication.run(MinisocialApplication.class, args);
        }
    }
}
