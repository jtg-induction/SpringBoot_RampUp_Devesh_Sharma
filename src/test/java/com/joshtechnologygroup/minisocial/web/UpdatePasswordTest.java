package com.joshtechnologygroup.minisocial.web;

import com.joshtechnologygroup.minisocial.bean.User;
import com.joshtechnologygroup.minisocial.dao.UserRepository;
import com.joshtechnologygroup.minisocial.dto.UpdatePasswordRequest;
import com.joshtechnologygroup.minisocial.dto.UserLogin;
import com.joshtechnologygroup.minisocial.util.JwtUtil;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UpdatePasswordTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final String TEST_EMAIL = "test@example.com";
    private String TEST_PASSWORD;
    private String SECOND_PASSWORD;
    private String authToken;

    @Autowired
    private JwtUtil jwtUtil;

    @BeforeEach
    void setup() throws Exception {
        userRepository.deleteAll();

        TEST_PASSWORD = "test-password";
        SECOND_PASSWORD = "second-password";

        // Create a test user
        User user = new User();
        user.setEmail(TEST_EMAIL);
        user.setPassword(passwordEncoder.encode(TEST_PASSWORD));
        user.setActive(true);
        userRepository.save(user);

        authToken = jwtUtil.generateToken(TEST_EMAIL, user.getId());
    }

    @Test
    void changePassword() throws Exception {
        UpdatePasswordRequest req = new UpdatePasswordRequest(TEST_EMAIL, TEST_PASSWORD, SECOND_PASSWORD);
        mockMvc.perform(post("/api/user/update-password")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    void changePasswordInvalidEmail() throws Exception {
        UpdatePasswordRequest req = new UpdatePasswordRequest("wrong-email", TEST_PASSWORD, SECOND_PASSWORD);
        mockMvc.perform(post("/api/user/update-password")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void changePasswordWrongPassword() throws Exception {
        UpdatePasswordRequest req = new UpdatePasswordRequest(TEST_EMAIL, "wrong-pass", SECOND_PASSWORD);
        mockMvc.perform(post("/api/user/update-password")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());
    }
}
