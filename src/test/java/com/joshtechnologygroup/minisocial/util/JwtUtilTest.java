package com.joshtechnologygroup.minisocial.util;

import com.joshtechnologygroup.minisocial.web.config.ApiConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtUtilTest {
    @Mock
    private ApiConfig apiConfig;

    private JwtUtil jwtUtil;

    private String token = "";
    private Date issuedAt;
    private final String TEST_MAIL = "test@abc.com";
    private final long TEST_ID = 1L;

    @BeforeEach
    void setUp() {
        // Mock the return value of getJwtKey() before initializing the class
        // Use a test key that is long enough for HMAC-SHA (at least 32 characters/256 bits)
        String TEST_KEY = "uOG/GhZwQ8xGxZJ/kpez1kivocjQAm6XY2D4px/VGqQ=";
        long TEST_EXPIRY = 1000 * 60 * 60 * 4; // 4 Hours
        when(apiConfig.getJwtKey()).thenReturn(TEST_KEY);
        when(apiConfig.getJwtExpiry()).thenReturn(TEST_EXPIRY);
        jwtUtil = new JwtUtil(apiConfig);
        token = jwtUtil.generateToken(TEST_MAIL, TEST_ID);
        issuedAt = new Date();
    }

    @Test
    void generateToken() {
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void extractUserIdFromToken() {
        long userId = jwtUtil.extractUserId(token);
        assertEquals(TEST_ID, userId);
    }

    @Test
    void extractEmailFromToken() {
        String email = jwtUtil.extractEmail(token);
        assertEquals(TEST_MAIL, email);
    }

    @Test
    void extractExpirationFromToken() {
        Date expiration = jwtUtil.extractExpiration(token);
        assertTrue(issuedAt.before(expiration));
    }

    @Test
    void isTokenValid() {
        assertTrue(jwtUtil.validateToken(token));
    }

    @Test
    void validateTamperedToken() {
        String tamperedToken = token + "modified";
        // This will throw a SignatureException when parsing
        assertThrows(Exception.class, () -> jwtUtil.validateToken(tamperedToken));
    }

    @Test
    void extractEmailDifferentValue() {
        String mail = "test2@gmail.com";
        String newToken = jwtUtil.generateToken(mail, 2L);
        assertEquals(mail, jwtUtil.extractEmail(newToken));
        assertEquals(2L, jwtUtil.extractUserId(newToken));
    }
}