package com.joshtechnologygroup.minisocial.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
@Tag(name = "Health Check", description = "API for checking application health status")
public class HealthController {
    @GetMapping
    @Operation(summary = "Check application health status", description = "Returns 'Online' if the application is running")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Application is online",
                    content = @Content(mediaType = "text/plain"))
    })
    public String health() {
        return "Online";
    }
}
