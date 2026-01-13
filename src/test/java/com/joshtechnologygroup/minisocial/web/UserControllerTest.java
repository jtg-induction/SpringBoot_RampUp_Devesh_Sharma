package com.joshtechnologygroup.minisocial.web;

import com.joshtechnologygroup.minisocial.bean.User;
import com.joshtechnologygroup.minisocial.repository.UserRepository;
import com.joshtechnologygroup.minisocial.dto.user.ActiveUserDTO;
import com.joshtechnologygroup.minisocial.dto.user.UserCreateRequest;
import com.joshtechnologygroup.minisocial.dto.user.UserDTO;
import com.joshtechnologygroup.minisocial.dto.user.UserUpdateRequest;
import com.joshtechnologygroup.minisocial.factory.UserFactory;
import com.joshtechnologygroup.minisocial.service.UserService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Sql(scripts = "/user-data.sql")
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @Test
    @WithMockUser(username = "test@gmail.com")
    void getActiveUsers() throws Exception {
        String res = mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        List<ActiveUserDTO> list = objectMapper.readerForListOf(ActiveUserDTO.class)
                .readValue(res);
        assert (list.size() == 3);
    }

    @Test
    @WithMockUser(username = "test@gmail.com")
    void createUser_shouldReturn201_whenValidInput() throws Exception {
        UserCreateRequest userCreateRequest = UserFactory.defaultUserCreateRequest()
                .build();
        mockMvc.perform(post("/api/user").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCreateRequest)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "test@gmail.com")
    void createUser_shouldReturn400_whenMissingFields() throws Exception {
        UserCreateRequest userCreateRequest = UserFactory.defaultUserCreateRequest()
                .userDetails(null)
                .build();
        mockMvc.perform(post("/api/user").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCreateRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "test@gmail.com")
    void createUser_shouldReturn400_whenInvalidEmail() throws Exception {
        UserCreateRequest userCreateRequest = UserFactory.defaultUserCreateRequest()
                .email("invalid-email")
                .build();
        mockMvc.perform(post("/api/user").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCreateRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "test@gmail.com")
    void updateUser_shouldReturn404_NonExistingUser() throws Exception {
        UserUpdateRequest userUpdateRequest = UserFactory.defaultUserUpdateRequest()
                .id(999L)
                .build();
        mockMvc.perform(put("/api/user").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUpdateRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "john.doe@test.com")
    void updateUser_shouldReturnDTO_validUser() throws Exception {
        Optional<User> user = userRepository.findById(1L);
        if(user.isEmpty()) throw new Exception("User not found in test database");
        UserUpdateRequest userUpdateRequest = UserFactory.defaultUserUpdateRequest(user.get())
                .build();
        String res = mockMvc.perform(put("/api/user").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUpdateRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        UserDTO dto = objectMapper.readerFor(UserDTO.class)
                .readValue(res);

        assert (dto.id() == 1);
    }

    @Test
    @WithMockUser(username = "john.doe@test.com")
    void updateUser_shouldReturn403_whenWrongUser() throws Exception {
        UserUpdateRequest userUpdateRequest = UserFactory.defaultUserUpdateRequest()
                .id(5L)
                .build();
        mockMvc.perform(put("/api/user").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUpdateRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "test@gmail.com")
    void getUser_shouldReturnDTO_whenExists() throws Exception {
        String res = mockMvc.perform(get("/api/user/1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        UserDTO dto = objectMapper.readerFor(UserDTO.class)
                .readValue(res);

        assert (dto.id() == 1);
    }

    @Test
    @WithMockUser(username = "test@gmail.com")
    void deleteUser_shouldReturn200_whenExists() throws Exception {
        String res = mockMvc.perform(delete("/api/user/2"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        UserDTO dto = objectMapper.readerFor(UserDTO.class)
                .readValue(res);

        assert (dto.id() == 2);
    }

    @Test
    @WithMockUser(username = "test@gmail.com")
    void deleteUser_shouldReturn404_whenNotExists() throws Exception {
        mockMvc.perform(delete("/api/user/999"))
                .andExpect(status().isNotFound());
    }
}
