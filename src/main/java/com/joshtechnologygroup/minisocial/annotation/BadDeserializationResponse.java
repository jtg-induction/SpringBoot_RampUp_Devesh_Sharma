package com.joshtechnologygroup.minisocial.annotation;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ProblemDetail;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@ApiResponses(
    {
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request format - Deserialization failed",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ProblemDetail.class)
            )
        ),
    }
)
public @interface BadDeserializationResponse {}
