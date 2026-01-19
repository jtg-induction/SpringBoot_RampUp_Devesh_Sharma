package com.joshtechnologygroup.minisocial.web;

import com.joshtechnologygroup.minisocial.bean.User;
import com.joshtechnologygroup.minisocial.dto.user.UserCreateRequest;
import com.joshtechnologygroup.minisocial.dto.user.UserDTO;
import com.joshtechnologygroup.minisocial.dto.user.UserUpdateRequest;
import com.joshtechnologygroup.minisocial.factory.UserFactory;
import com.joshtechnologygroup.minisocial.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
        String res = mockMvc
                .perform(get("/api/users?active=true"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        // Parse paginated response
        var page = objectMapper.readTree(res);
        var content = page.get("content");
        assertEquals(3, content.size());
    }

    @Test
    @WithMockUser(username = "test@gmail.com")
    void getAllUsers_shouldReturnAllUsers() throws Exception {
        mockMvc
                .perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(5)));
    }

    @Test
    @WithMockUser(username = "test@gmail.com")
    void getActiveUsers_shouldReturnFilteredUsers() throws Exception {
        mockMvc
                .perform(get("/api/users?active=true&firstName=John"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));
    }

    @Test
    void createUser_shouldReturn201_whenValidInput() throws Exception {
        UserCreateRequest userCreateRequest =
                UserFactory.defaultUserCreateRequest()
                        .build();
        mockMvc
                .perform(
                        post("/api/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userCreateRequest))
                )
                .andExpect(status().isCreated());
    }

    @Test
    void createUser_shouldReturn422_whenMissingFields() throws Exception {
        UserCreateRequest userCreateRequest =
                UserFactory.defaultUserCreateRequest()
                        .password(null)
                        .build();
        mockMvc
                .perform(
                        post("/api/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userCreateRequest))
                )
                .andExpect(status().isUnprocessableContent());
    }

    @Test
    void createUser_shouldReturn422_whenInvalidEmail() throws Exception {
        UserCreateRequest userCreateRequest =
                UserFactory.defaultUserCreateRequest()
                        .email("invalid-email")
                        .build();
        mockMvc
                .perform(
                        post("/api/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userCreateRequest))
                )
                .andExpect(status().isUnprocessableContent());
    }

    @Test
    void createUser_shouldReturn422_whenDuplicateEmail() throws Exception {
        UserCreateRequest userCreateRequest =
                UserFactory.defaultUserCreateRequest()
                        .email("john.doe@test.com")
                        .build();

        mockMvc
                .perform(
                        post("/api/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userCreateRequest))
                )
                .andExpect(status().isUnprocessableContent());
    }

    @Test
    void createUser_shouldReturn400_whenBadJson() throws Exception {
        String badJson = "{ email: test@gmail.com }";

        mockMvc
                .perform(
                        post("/api/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(badJson)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "test@gmail.com")
    void updateUser_shouldReturn404_NonExistingUser() throws Exception {
        UserUpdateRequest userUpdateRequest =
                UserFactory.defaultUserUpdateRequest()
                        .build();
        mockMvc
                .perform(
                        put("/api/users/profile")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userUpdateRequest))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "john.doe@test.com")
    void updateUser_shouldReturnDTO_validUser() throws Exception {
        Optional<User> user = userRepository.findById(1L);
        if (user.isEmpty()) throw new Exception(
                "User not found in test database"
        );
        UserUpdateRequest userUpdateRequest =
                UserFactory.defaultUserUpdateRequest(user.get())
                        .build();
        String res = mockMvc
                .perform(
                        put("/api/users/profile")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userUpdateRequest))
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        UserDTO dto = objectMapper.readerFor(UserDTO.class)
                .readValue(res);

        assert (dto.id() == 1);
    }

    @Test
    @WithMockUser(username = "nonexistent.user@test.com")
    void partiallyUpdateUser_shouldReturn404_NonExistingUser()
            throws Exception {
        UserUpdateRequest userUpdateRequest = UserUpdateRequest.builder()
                .build();
        mockMvc
                .perform(
                        patch("/api/users/profile")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userUpdateRequest))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "john.doe@test.com")
    void partiallyUpdateUser_shouldReturnDTO_validUser() throws Exception {
        Optional<User> user = userRepository.findById(1L);
        if (user.isEmpty()) throw new Exception(
                "User not found in test database"
        );
        UserUpdateRequest userUpdateRequest = UserFactory.defaultUserUpdateRequest(user.get())
                .build();
        String res = mockMvc
                .perform(
                        patch("/api/users/profile")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userUpdateRequest))
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        UserDTO dto = objectMapper.readerFor(UserDTO.class)
                .readValue(res);

        assertEquals(1, dto.id());
        assertEquals(userUpdateRequest.userDetails()
                .firstName(), dto.userDetails()
                .firstName());
    }

    @Test
    @WithMockUser(username = "test@gmail.com")
    void getUser_shouldReturn422_whenNegativeId() throws Exception {
        mockMvc
                .perform(get("/api/users/-1"))
                .andExpect(status().isUnprocessableContent());
    }

    @Test
    @WithMockUser(username = "jane.smith@test.com")
    void deleteUser_shouldReturn200_whenExists() throws Exception {
        String res = mockMvc
                .perform(delete("/api/users/profile"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        UserDTO dto = objectMapper.readerFor(UserDTO.class)
                .readValue(res);

        assertEquals(2, dto.id());
    }

    @Test
    @WithMockUser(username = "test@gmail.com")
    void deleteUser_shouldReturn404_whenNotExists() throws Exception {
        mockMvc.perform(delete("/api/users/profile"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "test@gmail.com")
    void getUsers_shouldSortByName_whenNameSortOrderProvided()
            throws Exception {
        mockMvc
                .perform(get("/api/users?sortOrders=NAME"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(5)))
                .andExpect(jsonPath("$.content[0].user_details.first_name").value("Sarah"))
                .andExpect(jsonPath("$.content[1].user_details.first_name").value("Mike"))
                .andExpect(jsonPath("$.content[2].user_details.first_name").value("John"))
                .andExpect(jsonPath("$.content[3].user_details.first_name").value("Jane"))
                .andExpect(jsonPath("$.content[4].user_details.first_name").value("David"));
    }

    @Test
    @WithMockUser(username = "test@gmail.com")
    void getUsers_shouldSortByEmailAndGender_whenMultipleSortOrdersProvided()
            throws Exception {
        mockMvc
                .perform(get("/api/users?sortOrders=EMAIL,GENDER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(5)))
                .andExpect(jsonPath("$.content[0].email").value("sarah.wilson@test.com"))
                .andExpect(jsonPath("$.content[1].email").value("mike.johnson@test.com"))
                .andExpect(jsonPath("$.content[2].email").value("john.doe@test.com"))
                .andExpect(jsonPath("$.content[3].email").value("jane.smith@test.com"))
                .andExpect(jsonPath("$.content[4].email").value("david.brown@test.com"));
    }

    @Test
    @WithMockUser(username = "test@gmail.com")
    void getUsers_shouldSortByResidentialAddressAndMaritalStatus_whenMultipleSortOrdersProvided()
            throws Exception {
        mockMvc
                .perform(
                        get("/api/users?sortOrders=RESIDENTIAL_DETAIL,MARITAL_STATUS")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(5)))
                .andExpect(jsonPath("$.content[0].user_details.residential_details.city").value("Phoenix"))
                .andExpect(jsonPath("$.content[1].user_details.residential_details.city").value("New York"))
                .andExpect(jsonPath("$.content[2].user_details.residential_details.city").value("Los Angeles"))
                .andExpect(jsonPath("$.content[3].user_details.residential_details.city").value("Houston"))
                .andExpect(jsonPath("$.content[4].user_details.residential_details.city").value("Chicago"));
    }

    @Test
    @WithMockUser(username = "test@gmail.com")
    void getUsers_shouldSortByCompanyNameAndFollowingCount_whenMultipleSortOrdersProvided()
            throws Exception {
        mockMvc
                .perform(get("/api/users?sortOrders=COMPANY_NAME,FOLLOWING_COUNT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(5)))
                .andExpect(jsonPath("$.content[0].user_details.official_details.company_name").value("TechCorp Inc"))
                .andExpect(jsonPath("$.content[1].user_details.official_details.company_name").value("StartupTech LLC"))
                .andExpect(jsonPath("$.content[2].user_details.official_details.company_name").value("MegaCorp Industries"))
                .andExpect(jsonPath("$.content[3].user_details.official_details.company_name").value("Innovate Solutions"))
                .andExpect(jsonPath("$.content[4].user_details.official_details.company_name").value("Global Firm Ltd"));
    }

    @Test
    @WithMockUser(username = "test@gmail.com")
    void getUsers_shouldSortByGenderMaritalStatusAndEmail() throws Exception {
        mockMvc
                .perform(get("/api/users?sortOrders=GENDER,MARITAL_STATUS,EMAIL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(5)))
                .andExpect(jsonPath("$.content[0].user_details.gender").value("Male"))
                .andExpect(jsonPath("$.content[0].user_details.marital_status").value("Single"))
                .andExpect(jsonPath("$.content[0].email").value("mike.johnson@test.com"))
                .andExpect(jsonPath("$.content[1].user_details.gender").value("Male"))
                .andExpect(jsonPath("$.content[1].user_details.marital_status").value("Single"))
                .andExpect(jsonPath("$.content[1].email").value("john.doe@test.com"))
                .andExpect(jsonPath("$.content[2].user_details.gender").value("Male"))
                .andExpect(jsonPath("$.content[2].user_details.marital_status").value("Married"))
                .andExpect(jsonPath("$.content[3].user_details.gender").value("Female"))
                .andExpect(jsonPath("$.content[3].user_details.marital_status").value("Married"))
                .andExpect(jsonPath("$.content[4].user_details.gender").value("Female"))
                .andExpect(jsonPath("$.content[4].user_details.marital_status").value("Divorced"));
    }

    @Test
    @WithMockUser(username = "test@gmail.com")
    void getUsers_shouldFilterByAgeRange_whenMinMaxAgeProvided()
            throws Exception {
        mockMvc
                .perform(get("/api/users?minAge=28&maxAge=32"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(3)))
                .andExpect(jsonPath("$.content[*].age", everyItem(Matchers.greaterThanOrEqualTo(28))))
                .andExpect(jsonPath("$.content[*].age", everyItem(Matchers.lessThanOrEqualTo(32))));
    }

    @Test
    @WithMockUser(username = "test@gmail.com")
    void getUsers_shouldFilterByGenderAndMaritalStatus_whenMultipleFiltersProvided()
            throws Exception {
        mockMvc
                .perform(get("/api/users?gender=MALE&maritalStatus=SINGLE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[*].gender", everyItem(hasValue("Male"))))
                .andExpect(jsonPath("$.content[*].marital_status", everyItem(hasValue("Single"))));
    }

    @Test
    @WithMockUser(username = "test@gmail.com")
    void getUsers_shouldFilterByResidentialCities_whenResidentialCitiesProvided()
            throws Exception {
        mockMvc
                .perform(get("/api/users?residentialCities=New York,Chicago"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].user_details.first_name").exists())
                .andExpect(jsonPath("$.content[1].user_details.first_name").exists());
    }

    @Test
    @WithMockUser(username = "test@gmail.com")
    void getUsers_shouldFilterByOfficialCitiesAndCompanyName_whenMultipleFiltersProvided()
            throws Exception {
        mockMvc
                .perform(
                        get(
                                "/api/users?officialCities=New York&companyName=TechCorp Inc"
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].user_details.first_name").value("John"))
                .andExpect(jsonPath("$.content[0].user_details.official_details.company_name").value("TechCorp Inc"));
    }

    @Test
    @WithMockUser(username = "test@gmail.com")
    void getUsers_shouldFilterByFirstNameAndLastName_whenNameFiltersProvided()
            throws Exception {
        mockMvc
                .perform(get("/api/users?firstName=J"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].user_details.first_name").value("John"))
                .andExpect(jsonPath("$.content[1].user_details.first_name").value("Jane"));
    }

    @Test
    @WithMockUser(username = "test@gmail.com")
    void getUsers_shouldFilterByFollowingCountRange_whenFollowingCountFiltersProvided()
            throws Exception {
        mockMvc
                .perform(get("/api/users?minFollowingCount=2&maxFollowingCount=2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(5)));
    }

    @Test
    @WithMockUser(username = "test@gmail.com")
    void getUsers_shouldFilterByFollowerCountRange_whenFollowerCountFiltersProvided()
            throws Exception {
        mockMvc
                .perform(get("/api/users?minFollowerCount=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(5)));
    }

    @Test
    @WithMockUser(username = "test@gmail.com")
    void getUsers_shouldFilterByActiveStatus_whenActiveFilterProvided()
            throws Exception {
        mockMvc
                .perform(get("/api/users?active=false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].user_details.first_name").value("Jane"))
                .andExpect(jsonPath("$.content[1].user_details.first_name").value("Sarah"));
    }

    @Test
    @WithMockUser(username = "test@gmail.com")
    void getUsers_shouldApplyComplexFiltering_whenMultipleFiltersProvided()
            throws Exception {
        mockMvc
                .perform(
                        get(
                                "/api/users?active=true&gender=MALE&minAge=25&maxAge=35&residentialCities=New York,Chicago,Phoenix"
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(3)))
                .andExpect(jsonPath("$.content[0].user_details.gender").value("Male"))
                .andExpect(jsonPath("$.content[1].user_details.gender").value("Male"))
                .andExpect(jsonPath("$.content[2].user_details.gender").value("Male"))
                .andExpect(
                        jsonPath(
                                "$.content[?(@.user_details.age >= 25 && @.user_details.age <= 35)]"
                                , hasSize(3)
                        ));
    }

    @Test
    @WithMockUser(username = "test@gmail.com")
    void getUsers_shouldFilterByCompanyNameList_whenMultipleCompaniesProvided()
            throws Exception {
        mockMvc
                .perform(get("/api/users?companyName=TechCorp Inc,StartupTech LLC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].user_details.first_name").value("John"))
                .andExpect(jsonPath("$.content[1].user_details.first_name").value("Mike"));
    }
}
