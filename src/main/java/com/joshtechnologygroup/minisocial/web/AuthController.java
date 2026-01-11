package com.joshtechnologygroup.minisocial.web;

import com.joshtechnologygroup.minisocial.annotation.StandardSecurityResponse;
import com.joshtechnologygroup.minisocial.bean.User;
import com.joshtechnologygroup.minisocial.dao.UserRepository;
import com.joshtechnologygroup.minisocial.dto.UpdatePasswordRequest;
import com.joshtechnologygroup.minisocial.dto.UserLogin;
import com.joshtechnologygroup.minisocial.exception.InvalidUserCredentialsException;
import com.joshtechnologygroup.minisocial.service.UserDetailsServiceImpl;
import com.joshtechnologygroup.minisocial.service.UserService;
import com.joshtechnologygroup.minisocial.util.JwtUtil;
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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/user")
@Slf4j
@Tag(name = "Authentication", description = "APIs for user authentication and password management")
class AuthController {
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;
    private final UserService userService;

    AuthController(AuthenticationManager authenticationManager, UserDetailsServiceImpl userDetailsService, JwtUtil jWTUtil, UserRepository userRepository, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jWTUtil;
        this.userRepository = userRepository;
        this.userService = userService;
    }


    @PostMapping("/authenticate")
    @Operation(description = "Authenticate user and issue JWT", summary = "User Authentication")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Authentication successful, JWT issued",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Validation failed",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))
            )
    })
    public ResponseEntity<String> authenticate(@Valid @RequestBody UserLogin user) {
        try {
            log.info("Attempting to authenticate user {}", user.email());
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.email(), user.password()));
            Optional<User> dbUser = userRepository.findByEmail(user.email());
            if (dbUser.isEmpty()) throw new RuntimeException();
            String jwt = jwtUtil.generateToken(user.email(), dbUser.get()
                    .getId());

            log.debug("Successful login for user {}, JWT issued: {}", user.email(), jwt);

            return new ResponseEntity<>(jwt, HttpStatus.OK);
        } catch (Exception e) {
            log.debug("Exception in authenticate(): {}", e.getMessage());
            throw new InvalidUserCredentialsException();
        }
    }

    @StandardSecurityResponse
    @PostMapping("/update-password")
    @Operation(description = "Update user password", summary = "Update Password")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Password changed successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
            ),
    })
    public ResponseEntity<String> updatePassword(@Valid @RequestBody UpdatePasswordRequest updatePasswordRequest) {
        try {
            log.debug("Password change requested for user: {}", updatePasswordRequest.email());
            userService.updateUserPassword(updatePasswordRequest);
            return new ResponseEntity<>("Password changed successfully", HttpStatus.OK);
        } catch (BadCredentialsException e) {
            throw new InvalidUserCredentialsException();
        }
    }
}
