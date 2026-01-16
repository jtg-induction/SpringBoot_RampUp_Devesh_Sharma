package com.joshtechnologygroup.minisocial.web;

import com.joshtechnologygroup.minisocial.bean.User;
import com.joshtechnologygroup.minisocial.dto.UpdatePasswordRequest;
import com.joshtechnologygroup.minisocial.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
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

    @BeforeEach
    void setup() {
        userRepository.deleteAll();

        TEST_PASSWORD = "test-password";
        SECOND_PASSWORD = "second-password";

        // Create a test user
        User user = new User();
        user.setEmail(TEST_EMAIL);
        user.setPassword(passwordEncoder.encode(TEST_PASSWORD));
        user.setActive(true);
        userRepository.save(user);
    }

    @Test
    @WithMockUser(username = TEST_EMAIL)
    void changePassword() throws Exception {
        UpdatePasswordRequest req = new UpdatePasswordRequest(TEST_PASSWORD, SECOND_PASSWORD);

        mockMvc.perform(post("/api/user/update-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = TEST_EMAIL)
    void changePasswordShortPassword() throws Exception {
        UpdatePasswordRequest req = new UpdatePasswordRequest(TEST_PASSWORD, "short");
        mockMvc.perform(post("/api/user/update-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnprocessableContent());
    }

    @Test
    @WithMockUser(username = TEST_EMAIL)
    void changePasswordWrongPassword() throws Exception {
        UpdatePasswordRequest req = new UpdatePasswordRequest("wrong-pass", SECOND_PASSWORD);
        mockMvc.perform(post("/api/user/update-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = TEST_EMAIL)
    void changePasswordSameAsOld() throws Exception {
        UpdatePasswordRequest req = new UpdatePasswordRequest(TEST_PASSWORD, TEST_PASSWORD);
        mockMvc.perform(post("/api/user/update-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnprocessableContent());
    }
}
