package com.joshtechnologygroup.minisocial.service;

import com.joshtechnologygroup.minisocial.bean.User;
import com.joshtechnologygroup.minisocial.dto.auth.UpdatePasswordRequest;
import com.joshtechnologygroup.minisocial.dto.auth.UserLogin;
import com.joshtechnologygroup.minisocial.exception.InvalidUserCredentialsException;
import com.joshtechnologygroup.minisocial.exception.UserDoesNotExistException;
import com.joshtechnologygroup.minisocial.repository.UserRepository;
import com.joshtechnologygroup.minisocial.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class AuthService {
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    public AuthService(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public String authenticate(UserLogin user) {
        try {
            log.debug("Attempting to authenticate user {}", user.email());
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.email(), user.password()));
            Optional<User> dbUser = userRepository.findByEmail(user.email());
            if (dbUser.isEmpty()) throw new UserDoesNotExistException();
            String jwt = jwtUtil.generateToken(user.email(), dbUser.get()
                    .getId());

            log.info("Successful login for user {}", user.email());

            return jwt;
        } catch (Exception e) {
            log.debug("Exception in authenticate(): {}", e.getMessage());
            throw new InvalidUserCredentialsException();
        }
    }

    public void updatePassword(UpdatePasswordRequest request, String email) {
        try {
            log.debug("Password change requested for user: {}", email);
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, request.oldPassword()));

            Optional<User> user = userRepository.findByEmail(email);
            if (user.isEmpty()) throw new InvalidUserCredentialsException();

            user.get()
                    .setPassword(passwordEncoder.encode(request.newPassword()));
            userRepository.save(user.get());
            log.info("Successfully Updated password for user {}", email);
        } catch (BadCredentialsException e) {
            throw new InvalidUserCredentialsException();
        }
    }
}
