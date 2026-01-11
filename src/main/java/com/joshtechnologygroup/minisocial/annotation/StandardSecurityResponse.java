package com.joshtechnologygroup.minisocial.annotation;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ProblemDetail;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@SecurityRequirement(name = "BearerAuth")
@ApiResponses({
        @ApiResponse(
                responseCode = "403",
                description = "Authorization token is missing or invalid",
                content = @Content
        ),
        @ApiResponse(
                responseCode = "422",
                description = "Validation failed",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))
        )
})
public @interface StandardSecurityResponse {
}
