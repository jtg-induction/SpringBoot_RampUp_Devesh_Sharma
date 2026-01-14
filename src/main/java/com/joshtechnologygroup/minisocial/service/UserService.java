package com.joshtechnologygroup.minisocial.service;

import com.joshtechnologygroup.minisocial.bean.User;
import com.joshtechnologygroup.minisocial.dto.UpdatePasswordRequest;
import com.joshtechnologygroup.minisocial.exception.InvalidUserCredentialsException;
import com.joshtechnologygroup.minisocial.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
    }

    public void updateUserPassword(UpdatePasswordRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.oldPassword()));

        Optional<User> user = userRepository.findByEmail(request.email());
        if (user.isEmpty()) throw new InvalidUserCredentialsException();

        user.get()
                .setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user.get());
        log.info("Successfully Updated password for user {}", request.email());
    }
}
