package com.joshtechnologygroup.minisocial.annotation;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

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
                description = "Authorization token is missing or invalid, or illegal action attempted",
                content = @Content
        )
})
public @interface StandardSecurityResponse {
}
