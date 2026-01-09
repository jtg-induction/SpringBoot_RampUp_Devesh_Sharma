package com.joshtechnologygroup.minisocial.service;

import com.joshtechnologygroup.minisocial.bean.Gender;
import com.joshtechnologygroup.minisocial.bean.MaritalStatus;
import com.joshtechnologygroup.minisocial.dao.UserRepository;
import com.joshtechnologygroup.minisocial.dto.officialDetail.OfficialDetailCreateRequest;
import com.joshtechnologygroup.minisocial.dto.residentialDetail.ResidentialDetailCreateRequest;
import com.joshtechnologygroup.minisocial.dto.user.UserCreateRequest;
import com.joshtechnologygroup.minisocial.dto.user.UserDTO;
import com.joshtechnologygroup.minisocial.dto.userDetail.UserDetailCreateRequest;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
    }

    @Test
    void createUser_ShouldPersistAllEntitiesAndReturnFullDto() {
        UserCreateRequest mainRequest = getUserCreateRequest();

        UserDTO result = userService.createUser(mainRequest);

        // Verify Results
        assertThat(result).isNotNull();
        assertThat(result.id()).isNotNull();
        assertThat(result.email()).isEqualTo("john.doe@example.com");

        assertThat(result.userDetails()).isNotNull();
        assertThat(result.userDetails()
                .firstName()).isEqualTo("John");
        assertThat(result.userDetails()
                .gender()).isEqualTo(Gender.MALE);

        assertThat(result.userDetails()
                .residentialDetails()).isNotNull();
        assertThat(result.userDetails()
                .residentialDetails()
                .city()).isEqualTo("Delhi");

        assertThat(result.userDetails()
                .officialDetails()).isNotNull();
        assertThat(result.userDetails()
                .officialDetails()
                .companyName()).isEqualTo("Company");

        assertThat(userRepository.count()).isEqualTo(1);
        assertThat(userRepository.findByEmail("john.doe@example.com")).isPresent();
    }

    private static @NonNull UserCreateRequest getUserCreateRequest() {
        OfficialDetailCreateRequest offReq = new OfficialDetailCreateRequest(
                "EMP001", "Office 101", "Gurgaon", "Haryana", "India", "999", "hr@company.com", "Company");

        ResidentialDetailCreateRequest resReq = new ResidentialDetailCreateRequest(
                "House 1", "Delhi", "Delhi", "India", "111", "222");

        UserDetailCreateRequest detailReq = new UserDetailCreateRequest(
                "John", "Doe", 25, Gender.MALE, MaritalStatus.SINGLE, resReq, offReq);

        return new UserCreateRequest("john.doe@example.com", "securePass123", true, detailReq);
    }
}
