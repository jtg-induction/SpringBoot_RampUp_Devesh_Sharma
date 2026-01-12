package com.joshtechnologygroup.minisocial.web;

import com.joshtechnologygroup.minisocial.dto.UpdatePasswordRequest;
import com.joshtechnologygroup.minisocial.dto.UserLogin;
import com.joshtechnologygroup.minisocial.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api/user")
@Slf4j
class AuthController {
    private final AuthService authService;

    AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<String> authenticate(@RequestBody UserLogin user) {
        String jwt = authService.authenticate(user);
        return new ResponseEntity<>(jwt, HttpStatus.OK);
    }

    @PostMapping("/update-password")
    public ResponseEntity<String> updatePassword(@RequestBody UpdatePasswordRequest updatePasswordRequest) {
        authService.updatePassword(updatePasswordRequest);
        return new ResponseEntity<>("Password changed successfully", HttpStatus.OK);
    }
}
