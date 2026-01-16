package com.joshtechnologygroup.minisocial.web;

import com.joshtechnologygroup.minisocial.annotation.BadDeserializationResponse;
import com.joshtechnologygroup.minisocial.annotation.StandardSecurityResponse;
import com.joshtechnologygroup.minisocial.dto.auth.AuthTokenResponse;
import com.joshtechnologygroup.minisocial.dto.auth.UpdatePasswordRequest;
import com.joshtechnologygroup.minisocial.dto.auth.UserLogin;
import com.joshtechnologygroup.minisocial.error.ValidationProblemDetail;
import com.joshtechnologygroup.minisocial.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@Slf4j
@Tag(name = "Authentication", description = "APIs for user authentication and password management")
class AuthController {
    private final AuthService authService;

    AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/authenticate")
    @BadDeserializationResponse
    @Operation(description = "Authenticate user and issue JWT", summary = "User Authentication")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Authentication successful, JWT issued",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthTokenResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid Username or Password",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Validation failed",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ValidationProblemDetail.class))
            )
    })
    public ResponseEntity<AuthTokenResponse> authenticate(@Valid @RequestBody UserLogin user) {
        String jwt = authService.authenticate(user);
        return new ResponseEntity<>(new AuthTokenResponse(jwt), HttpStatus.OK);
    }

    @StandardSecurityResponse
    @BadDeserializationResponse
    @PostMapping("/update-password")
    @Operation(description = "Update user password", summary = "Update Password")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Password changed successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema)
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid Username or Password",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))
            ),
    })
    public ResponseEntity<Void> updatePassword(@Valid @RequestBody UpdatePasswordRequest updatePasswordRequest, @AuthenticationPrincipal UserDetails userDetails) {
        authService.updatePassword(updatePasswordRequest, userDetails.getUsername());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
