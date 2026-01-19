package com.joshtechnologygroup.minisocial.annotation;

import com.joshtechnologygroup.minisocial.error.ValidationProblemDetail;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ApiResponses({
        @ApiResponse(
                responseCode = "422",
                description = "Validation failed for the request",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ValidationProblemDetail.class))
        )
})
public @interface ValidationErrorResponse {
}
