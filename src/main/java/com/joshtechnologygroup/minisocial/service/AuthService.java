package com.joshtechnologygroup.minisocial.service;

import com.joshtechnologygroup.minisocial.bean.User;
import com.joshtechnologygroup.minisocial.repository.UserRepository;
import com.joshtechnologygroup.minisocial.dto.UpdatePasswordRequest;
import com.joshtechnologygroup.minisocial.dto.UserLogin;
import com.joshtechnologygroup.minisocial.exception.InvalidUserCredentialsException;
import com.joshtechnologygroup.minisocial.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class AuthService {
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    public AuthService(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserRepository userRepository, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    public String authenticate(@Valid UserLogin user) {
        try {
            log.debug("Attempting to authenticate user {}", user.email());
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.email(), user.password()));
            Optional<User> dbUser = userRepository.findByEmail(user.email());
            if (dbUser.isEmpty()) throw new RuntimeException();
            String jwt = jwtUtil.generateToken(user.email(), dbUser.get().getId());

            log.info("Successful login for user {}, JWT issued: {}", user.email(), jwt);

            return jwt;
        } catch (Exception e) {
            log.debug("Exception in authenticate(): {}", e.getMessage());
            throw new InvalidUserCredentialsException();
        }
    }

    public void updatePassword(@Valid UpdatePasswordRequest updatePasswordRequest) {
        try {
            log.debug("Password change requested for user: {}", updatePasswordRequest.email());
            userService.updateUserPassword(updatePasswordRequest);
        } catch (BadCredentialsException e) {
            throw new InvalidUserCredentialsException();
        }
    }
}
