package com.joshtechnologygroup.minisocial.web;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Sql(scripts = "/user-data.sql")
class FollowerControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "john.doe@test.com")
    void getFollowers_shouldReturnFollowerIds_whenUserExists() throws Exception {
        String res = mockMvc.perform(MockMvcRequestBuilders.get("/api/followers")).andReturn().getResponse().getContentAsString();
        List<Long> followerIds = objectMapper.readerForListOf(Long.class).readValue(res);
        assertEquals(2, followerIds.size());
        assertTrue(followerIds.contains(2L));
        assertTrue(followerIds.contains(3L));
    }

    @Test
    @WithMockUser(username = "fake@test.com")
    void getFollowers_shouldReturn404_whenUserDoesNotExist() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/followers"))
                .andExpect(result -> assertEquals(404, result.getResponse().getStatus()));
    }

    @Test
    @WithMockUser(username = "john.doe@test.com")
    void getFollowed_shouldReturnList_whenUserExists() throws Exception {
        String res = mockMvc.perform(MockMvcRequestBuilders.get("/api/following")).andReturn().getResponse().getContentAsString();
        List<Long> followedIds = objectMapper.readerForListOf(Long.class).readValue(res);
        assertEquals(2, followedIds.size());
        assertTrue(followedIds.contains(2L));
        assertTrue(followedIds.contains(3L));
    }

    @Test
    @WithMockUser(username = "fake@test.com")
    void getFollowed_shouldReturn404_whenUserDoesNotExist() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/following"))
                .andExpect(result -> assertEquals(404, result.getResponse().getStatus()));
    }

    @Test
    @WithMockUser(username = "fake@test.com")
    void updateFollowers_shouldReturn404_whenUserDoesNotExist() throws Exception {
        String requestBody = objectMapper.writeValueAsString(List.of(2L, 3L));
        mockMvc.perform(MockMvcRequestBuilders.post("/api/following")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(result -> assertEquals(404, result.getResponse().getStatus()));
    }

    @Test
    @WithMockUser(username = "john.doe@test.com")
    void updateFollowers_shouldReturn422_whenFollowerDoesNotExist() throws Exception {
        String requestBody = objectMapper.writeValueAsString(List.of(2L, 999L));
        mockMvc.perform(MockMvcRequestBuilders.post("/api/following")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(result -> assertEquals(422, result.getResponse().getStatus()));
    }

    @Test
    @WithMockUser(username = "john.doe@test.com")
    void updateFollowers_shouldUpdateFollowers_whenValidRequest() throws Exception {
        String requestBody = objectMapper.writeValueAsString(List.of(2L));
        mockMvc.perform(MockMvcRequestBuilders.post("/api/following")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(result -> assertEquals(200, result.getResponse().getStatus()));

        String res = mockMvc.perform(MockMvcRequestBuilders.get("/api/following")).andReturn().getResponse().getContentAsString();
        List<Long> followerIds = objectMapper.readerForListOf(Long.class).readValue(res);
        assertEquals(1, followerIds.size());
        assertTrue(followerIds.contains(2L));
    }
}
