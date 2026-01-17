package com.joshtechnologygroup.minisocial.web;

import com.joshtechnologygroup.minisocial.bean.User;
import com.joshtechnologygroup.minisocial.dto.auth.UserLogin;
import com.joshtechnologygroup.minisocial.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthenticationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final String TEST_EMAIL = "test@example.com";
    private final String TEST_PASSWORD = "password123";

    @BeforeEach
    void setup() {
        userRepository.deleteAll();

        // Create a test user
        User user = new User();
        user.setEmail(TEST_EMAIL);
        user.setPassword(passwordEncoder.encode(TEST_PASSWORD));
        user.setActive(true);
        userRepository.save(user);
    }

    @Test
    void authenticateUser_Success() throws Exception {
        UserLogin loginRequest = new UserLogin(TEST_EMAIL, TEST_PASSWORD);

        String response = mockMvc.perform(post("/api/users/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertNotNull(response);
        // Check if response is there
        assertFalse(response.isEmpty());
    }

    @Test
    void authenticateUser_WrongPassword() throws Exception {
        UserLogin loginRequest = new UserLogin(TEST_EMAIL, "wrong-password");

        mockMvc.perform(post("/api/users/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void authenticateUser_WrongEmail() throws Exception {
        UserLogin loginRequest = new UserLogin("nonexistent@example.com", TEST_PASSWORD);

        mockMvc.perform(post("/api/users/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void authenticateUser_InvalidEmail() throws Exception {
        UserLogin loginRequest = new UserLogin("invalid-email", TEST_PASSWORD);

        mockMvc.perform(post("/api/users/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnprocessableContent());
    }

    @Test
    void authenticateUser_BadJson() throws Exception {
        String badJson = "{ email: bad-email }";
        mockMvc.perform(post("/api/users/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(badJson))
                .andExpect(status().isBadRequest());
    }
}
