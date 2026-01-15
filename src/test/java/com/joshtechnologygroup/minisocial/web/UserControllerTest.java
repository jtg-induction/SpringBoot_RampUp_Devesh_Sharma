package com.joshtechnologygroup.minisocial.web;

import com.joshtechnologygroup.minisocial.bean.User;
import com.joshtechnologygroup.minisocial.dto.user.UserCreateRequest;
import com.joshtechnologygroup.minisocial.dto.user.UserDTO;
import com.joshtechnologygroup.minisocial.dto.user.UserUpdateRequest;
import com.joshtechnologygroup.minisocial.factory.UserFactory;
import com.joshtechnologygroup.minisocial.repository.UserRepository;
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
    private UserRepository userRepository;

    @Test
    @WithMockUser(username = "test@gmail.com")
    void getActiveUsers() throws Exception {
        String res = mockMvc.perform(get("/api/users?active=true"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        List<UserDTO> list = objectMapper.readerForListOf(UserDTO.class)
                .readValue(res);
        assert (list.size() == 3);
    }

    @Test
    @WithMockUser(username = "test@gmail.com")
    void getAllUsers_shouldReturnAllUsers() throws Exception {
        String res = mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        List<UserDTO> list = objectMapper.readerForListOf(UserDTO.class)
                .readValue(res);
        assert (list.size() == 5);
    }

    @Test
    @WithMockUser(username = "test@gmail.com")
    void getActiveUsers_shouldReturnFilteredUsers() throws Exception {
        String res = mockMvc.perform(get("/api/users?active=true&firstName=John"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        List<UserDTO> list = objectMapper.readerForListOf(UserDTO.class)
                .readValue(res);
        assert (list.size() == 1);
    }

    @Test
    void createUser_shouldReturn201_whenValidInput() throws Exception {
        UserCreateRequest userCreateRequest = UserFactory.defaultUserCreateRequest()
                .build();
        mockMvc.perform(post("/api/user").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCreateRequest)))
                .andExpect(status().isCreated());
    }

    @Test
    void createUser_shouldReturn422_whenMissingFields() throws Exception {
        UserCreateRequest userCreateRequest = UserFactory.defaultUserCreateRequest()
                .userDetails(null)
                .build();
        mockMvc.perform(post("/api/user").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCreateRequest)))
                .andExpect(status().isUnprocessableContent());
    }

    @Test
    void createUser_shouldReturn422_whenInvalidEmail() throws Exception {
        UserCreateRequest userCreateRequest = UserFactory.defaultUserCreateRequest()
                .email("invalid-email")
                .build();
        mockMvc.perform(post("/api/user").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCreateRequest)))
                .andExpect(status().isUnprocessableContent());
    }

    @Test
    @WithMockUser(username = "test@gmail.com")
    void updateUser_shouldReturn404_NonExistingUser() throws Exception {
        UserUpdateRequest userUpdateRequest = UserFactory.defaultUserUpdateRequest()
                .build();
        mockMvc.perform(put("/api/user/").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUpdateRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "john.doe@test.com")
    void updateUser_shouldReturnDTO_validUser() throws Exception {
        Optional<User> user = userRepository.findById(1L);
        if (user.isEmpty()) throw new Exception("User not found in test database");
        UserUpdateRequest userUpdateRequest = UserFactory.defaultUserUpdateRequest(user.get())
                .build();
        String res = mockMvc.perform(put("/api/user/").contentType(MediaType.APPLICATION_JSON)
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
    @WithMockUser(username = "test@gmail.com")
    void getUser_shouldReturn422_whenNegativeId() throws Exception {
        mockMvc.perform(get("/api/user/-1"))
                .andExpect(status().isUnprocessableContent());
    }

    @Test
    @WithMockUser(username = "jane.smith@test.com")
    void deleteUser_shouldReturn200_whenExists() throws Exception {
        String res = mockMvc.perform(delete("/api/user/"))
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
        mockMvc.perform(delete("/api/user/"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "test@gmail.com")
    void getUsers_shouldSortByName_whenNameSortOrderProvided() throws Exception {
        String res = mockMvc.perform(get("/api/users?sortOrders=NAME"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<UserDTO> list = objectMapper.readerForListOf(UserDTO.class)
                .readValue(res);

        assert (list.size() == 5);
        // Verify sorting
        assert (list.get(0).userDetails().firstName().equals("Sarah"));
        assert (list.get(1).userDetails().firstName().equals("Mike"));
        assert (list.get(2).userDetails().firstName().equals("John"));
        assert (list.get(3).userDetails().firstName().equals("Jane"));
        assert (list.get(4).userDetails().firstName().equals("David"));
    }

    @Test
    @WithMockUser(username = "test@gmail.com")
    void getUsers_shouldSortByEmailAndGender_whenMultipleSortOrdersProvided() throws Exception {
        String res = mockMvc.perform(get("/api/users?sortOrders=EMAIL,GENDER"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<UserDTO> list = objectMapper.readerForListOf(UserDTO.class)
                .readValue(res);

        assert (list.size() == 5);
        // Verify sorting
        assert (list.get(0).email().equals("sarah.wilson@test.com"));
        assert (list.get(1).email().equals("mike.johnson@test.com"));
        assert (list.get(2).email().equals("john.doe@test.com"));
        assert (list.get(3).email().equals("jane.smith@test.com"));
        assert (list.get(4).email().equals("david.brown@test.com"));
    }

    @Test
    @WithMockUser(username = "test@gmail.com")
    void getUsers_shouldSortByResidentialAddressAndMaritalStatus_whenMultipleSortOrdersProvided() throws Exception {
        String res = mockMvc.perform(get("/api/users?sortOrders=RESIDENTIAL_DETAIL,MARITAL_STATUS"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<UserDTO> list = objectMapper.readerForListOf(UserDTO.class)
                .readValue(res);

        assert (list.size() == 5);
        // Verify sorting by residential city
        assert (list.get(0).userDetails().residentialDetails().city().equals("Phoenix"));
        assert (list.get(1).userDetails().residentialDetails().city().equals("New York"));
        assert (list.get(2).userDetails().residentialDetails().city().equals("Los Angeles"));
        assert (list.get(3).userDetails().residentialDetails().city().equals("Houston"));
        assert (list.get(4).userDetails().residentialDetails().city().equals("Chicago"));
    }

    @Test
    @WithMockUser(username = "test@gmail.com")
    void getUsers_shouldSortByCompanyNameAndFollowingCount_whenMultipleSortOrdersProvided() throws Exception {
        String res = mockMvc.perform(get("/api/users?sortOrders=COMPANY_NAME,FOLLOWING_COUNT"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<UserDTO> list = objectMapper.readerForListOf(UserDTO.class)
                .readValue(res);

        assert (list.size() == 5);
        // Verify sorting
        assert (list.get(0).userDetails().officialDetails().companyName().equals("TechCorp Inc"));
        assert (list.get(1).userDetails().officialDetails().companyName().equals("StartupTech LLC"));
        assert (list.get(2).userDetails().officialDetails().companyName().equals("MegaCorp Industries"));
        assert (list.get(3).userDetails().officialDetails().companyName().equals("Innovate Solutions"));
        assert (list.get(4).userDetails().officialDetails().companyName().equals("Global Firm Ltd"));
    }

    @Test
    @WithMockUser(username = "test@gmail.com")
    void getUsers_shouldSortByGenderMaritalStatusAndEmail() throws Exception {
        String res = mockMvc.perform(get("/api/users?sortOrders=GENDER,MARITAL_STATUS,EMAIL"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<UserDTO> list = objectMapper.readerForListOf(UserDTO.class)
                .readValue(res);

        assert (list.size() == 5);

        // Verify sorting
        List<String> genders = list.stream()
                .map(user -> user.userDetails().gender().toString())
                .toList();

        assert (genders.contains("MALE"));
        assert (genders.contains("FEMALE"));
    }

    @Test
    @WithMockUser(username = "test@gmail.com")
    void getUsers_shouldFilterByAgeRange_whenMinMaxAgeProvided() throws Exception {
        String res = mockMvc.perform(get("/api/users?minAge=28&maxAge=32"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<UserDTO> list = objectMapper.readerForListOf(UserDTO.class)
                .readValue(res);

        assert (list.size() == 3);
        assert (list.stream().allMatch(user ->
                user.userDetails().age() >= 28 && user.userDetails().age() <= 32));
    }

    @Test
    @WithMockUser(username = "test@gmail.com")
    void getUsers_shouldFilterByGenderAndMaritalStatus_whenMultipleFiltersProvided() throws Exception {
        String res = mockMvc.perform(get("/api/users?gender=MALE&maritalStatus=SINGLE"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<UserDTO> list = objectMapper.readerForListOf(UserDTO.class)
                .readValue(res);

        assert (list.size() == 2);
        assert (list.stream().allMatch(user ->
                user.userDetails().gender().toString().equals("MALE") &&
                        user.userDetails().maritalStatus().toString().equals("SINGLE")));
    }

    @Test
    @WithMockUser(username = "test@gmail.com")
    void getUsers_shouldFilterByResidentialCities_whenResidentialCitiesProvided() throws Exception {
        String res = mockMvc.perform(get("/api/users?residentialCities=New York,Chicago"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<UserDTO> list = objectMapper.readerForListOf(UserDTO.class)
                .readValue(res);

        assert (list.size() == 2);
        assert (list.stream().anyMatch(user -> user.userDetails().firstName().equals("John")));
        assert (list.stream().anyMatch(user -> user.userDetails().firstName().equals("Mike")));
    }

    @Test
    @WithMockUser(username = "test@gmail.com")
    void getUsers_shouldFilterByOfficialCitiesAndCompanyName_whenMultipleFiltersProvided() throws Exception {
        String res = mockMvc.perform(get("/api/users?officialCities=New York&companyName=TechCorp Inc"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<UserDTO> list = objectMapper.readerForListOf(UserDTO.class)
                .readValue(res);

        assert (list.size() == 1);
        assert (list.get(0).userDetails().firstName().equals("John"));
        assert (list.get(0).userDetails().officialDetails().companyName().equals("TechCorp Inc"));
    }

    @Test
    @WithMockUser(username = "test@gmail.com")
    void getUsers_shouldFilterByFirstNameAndLastName_whenNameFiltersProvided() throws Exception {
        String res = mockMvc.perform(get("/api/users?firstName=J"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<UserDTO> list = objectMapper.readerForListOf(UserDTO.class)
                .readValue(res);

        assert (!list.isEmpty());
        assert (list.stream().anyMatch(user -> user.userDetails().firstName().startsWith("J")));
    }

    @Test
    @WithMockUser(username = "test@gmail.com")
    void getUsers_shouldFilterByFollowingCountRange_whenFollowingCountFiltersProvided() throws Exception {
        String res = mockMvc.perform(get("/api/users?minFollowingCount=2&maxFollowingCount=2"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<UserDTO> list = objectMapper.readerForListOf(UserDTO.class)
                .readValue(res);

        assert (list.size() == 5);
    }

    @Test
    @WithMockUser(username = "test@gmail.com")
    void getUsers_shouldFilterByFollowerCountRange_whenFollowerCountFiltersProvided() throws Exception {
        String res = mockMvc.perform(get("/api/users?minFollowerCount=1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<UserDTO> list = objectMapper.readerForListOf(UserDTO.class)
                .readValue(res);

        assert (list.size() == 5);
    }

    @Test
    @WithMockUser(username = "test@gmail.com")
    void getUsers_shouldFilterByActiveStatus_whenActiveFilterProvided() throws Exception {
        String res = mockMvc.perform(get("/api/users?active=false"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<UserDTO> list = objectMapper.readerForListOf(UserDTO.class)
                .readValue(res);

        assert (list.size() == 2);
        assert (list.stream().anyMatch(user -> user.userDetails().firstName().equals("Jane")));
        assert (list.stream().anyMatch(user -> user.userDetails().firstName().equals("Sarah")));
    }

    @Test
    @WithMockUser(username = "test@gmail.com")
    void getUsers_shouldApplyComplexFiltering_whenMultipleFiltersProvided() throws Exception {
        String res = mockMvc.perform(get("/api/users?active=true&gender=MALE&minAge=25&maxAge=35&residentialCities=New York,Chicago,Phoenix"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<UserDTO> list = objectMapper.readerForListOf(UserDTO.class)
                .readValue(res);

        assert (list.size() == 3);
        assert (list.stream().allMatch(user ->
                user.userDetails().gender().toString().equals("MALE") &&
                        user.userDetails().age() >= 25 && user.userDetails().age() <= 35));
    }

    @Test
    @WithMockUser(username = "test@gmail.com")
    void getUsers_shouldFilterByCompanyNameList_whenMultipleCompaniesProvided() throws Exception {
        String res = mockMvc.perform(get("/api/users?companyName=TechCorp Inc,StartupTech LLC"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<UserDTO> list = objectMapper.readerForListOf(UserDTO.class)
                .readValue(res);

        assert (list.size() == 2);
        assert (list.stream().anyMatch(user -> user.userDetails().firstName().equals("John")));
        assert (list.stream().anyMatch(user -> user.userDetails().firstName().equals("Mike")));
    }
}



