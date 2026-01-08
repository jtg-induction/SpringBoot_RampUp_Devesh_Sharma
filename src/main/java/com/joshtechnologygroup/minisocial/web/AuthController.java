package com.joshtechnologygroup.minisocial.web;

import com.joshtechnologygroup.minisocial.bean.User;
import com.joshtechnologygroup.minisocial.dao.UserRepository;
import com.joshtechnologygroup.minisocial.dto.UpdatePasswordRequest;
import com.joshtechnologygroup.minisocial.dto.UserLogin;
import com.joshtechnologygroup.minisocial.exception.InvalidUserCredentialsException;
import com.joshtechnologygroup.minisocial.service.UserDetailsServiceImpl;
import com.joshtechnologygroup.minisocial.service.UserService;
import com.joshtechnologygroup.minisocial.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@RestController
@RequestMapping("/api/user")
@Slf4j
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
    public ResponseEntity<String> authenticate(@RequestBody UserLogin user) {
        try {
            log.debug("Attempting to authenticate user {}", user.getEmail());
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));
            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
            Optional<User> dbUser = userRepository.findByEmail(userDetails.getUsername());
            if (dbUser.isEmpty()) throw new RuntimeException();
            String jwt = jwtUtil.generateToken(user.getEmail(), dbUser.get().getId());

            log.info("Successful login for user {}, JWT issued: {}", user.getEmail(), jwt);

            return new ResponseEntity<>(jwt, HttpStatus.OK);
        } catch (Exception e) {
            throw new InvalidUserCredentialsException();
        }
    }

    @PostMapping("/update-password")
    public ResponseEntity<String> updatePassword(@RequestBody UpdatePasswordRequest updatePasswordRequest) {
        try {
            log.debug("Password change requested for user: {}", updatePasswordRequest.getEmail());
            userService.updateUserPassword(updatePasswordRequest);
            return new ResponseEntity<>("Password changed successfully", HttpStatus.OK);
        } catch (BadCredentialsException e) {
            throw new InvalidUserCredentialsException();
        }
    }
}
