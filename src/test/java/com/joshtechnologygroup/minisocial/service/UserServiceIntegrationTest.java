package com.joshtechnologygroup.minisocial.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.joshtechnologygroup.minisocial.bean.Gender;
import com.joshtechnologygroup.minisocial.bean.MaritalStatus;
import com.joshtechnologygroup.minisocial.bean.User;
import com.joshtechnologygroup.minisocial.dao.UserRepository;
import com.joshtechnologygroup.minisocial.dto.officialDetail.OfficialDetailCreateRequest;
import com.joshtechnologygroup.minisocial.dto.officialDetail.OfficialDetailDTO;
import com.joshtechnologygroup.minisocial.dto.residentialDetail.ResidentialDetailCreateRequest;
import com.joshtechnologygroup.minisocial.dto.residentialDetail.ResidentialDetailDTO;
import com.joshtechnologygroup.minisocial.dto.user.ActiveUserDTO;
import com.joshtechnologygroup.minisocial.dto.user.UserCreateRequest;
import com.joshtechnologygroup.minisocial.dto.user.UserDTO;
import com.joshtechnologygroup.minisocial.dto.user.UserUpdateRequest;
import com.joshtechnologygroup.minisocial.dto.userDetail.UserDetailCreateRequest;
import com.joshtechnologygroup.minisocial.dto.userDetail.UserDetailDTO;
import com.joshtechnologygroup.minisocial.exception.UserDoesNotExistException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

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
        assertThat(result.userDetails().firstName()).isEqualTo("John");
        assertThat(result.userDetails().gender()).isEqualTo(Gender.MALE);

        assertThat(result.userDetails().residentialDetails()).isNotNull();
        assertThat(result.userDetails().residentialDetails().city()).isEqualTo(
            "Delhi"
        );

        assertThat(result.userDetails().officialDetails()).isNotNull();
        assertThat(
            result.userDetails().officialDetails().companyName()
        ).isEqualTo("Company");

        assertThat(userRepository.count()).isEqualTo(1);
        assertThat(
            userRepository.findByEmail("john.doe@example.com")
        ).isPresent();
    }

    @Test
    void getUser_Integration_ShouldFetchFullGraphFromDatabase() {
        UserCreateRequest request = getUserCreateRequest();
        UserDTO savedUser = userService.createUser(request);
        Long id = savedUser.id();

        Optional<UserDTO> result = userService.getUser(id);

        assertThat(result).isPresent();
        UserDTO dto = result.get();

        assertThat(dto.email()).isEqualTo("john.doe@example.com");
        assertThat(dto.userDetails()).isNotNull();

        assertThat(dto.userDetails().residentialDetails()).isNotNull();
        assertThat(dto.userDetails().officialDetails()).isNotNull();

        assertThat(dto.userDetails().residentialDetails().city()).isEqualTo(
            "Delhi"
        );
        assertThat(dto.userDetails().officialDetails().companyName()).isEqualTo(
            "Company"
        );
    }

    private static @NonNull UserCreateRequest getUserCreateRequest() {
        OfficialDetailCreateRequest offReq = new OfficialDetailCreateRequest(
            "EMP001",
            "Office 101",
            "Gurgaon",
            "Haryana",
            "India",
            "999",
            "hr@company.com",
            "Company"
        );

        ResidentialDetailCreateRequest resReq =
            new ResidentialDetailCreateRequest(
                "House 1",
                "Delhi",
                "Delhi",
                "India",
                "111",
                "222"
            );

        UserDetailCreateRequest detailReq = new UserDetailCreateRequest(
            "John",
            "Doe",
            25,
            Gender.MALE,
            MaritalStatus.SINGLE,
            resReq,
            offReq
        );

        return new UserCreateRequest(
            "john.doe@example.com",
            "securePass123",
            true,
            detailReq
        );
    }

    @Test
    void getActiveUsers_Integration_ShouldOnlyReturnActiveUsers() {
        // Add users
        User activeUser = new User();
        activeUser.setEmail("active@company.com");
        activeUser.setActive(true);
        activeUser.setPassword("1234");
        userRepository.save(activeUser);

        User inactiveUser = new User();
        inactiveUser.setEmail("inactive@company.com");
        inactiveUser.setActive(false);
        inactiveUser.setPassword("4567");
        userRepository.save(inactiveUser);

        // Execute
        List<ActiveUserDTO> result = userService.getActiveUsers();

        // Verify
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0)).isNotNull();
        assertThat(result.get(0).email()).isEqualTo("active@company.com");
    }

    @Test
    void updateUser_Integration_ShouldReflectChangesInDatabase() {
        // Create a user
        UserCreateRequest createReq = getUserCreateRequest();
        UserDTO initialUser = userService.createUser(createReq);
        Long userId = initialUser.id();

        // Prepare Request
        // Change the city in residential details and the email
        UserUpdateRequest updateReq = getUserUpdateRequest(createReq, userId);

        // Execute
        UserDTO result = userService.updateUser(updateReq);

        // Verify
        assertThat(result.email()).isEqualTo("updated@test.com");
        assertThat(result.userDetails().maritalStatus()).isEqualTo(
            MaritalStatus.MARRIED
        );
        assertThat(result.userDetails().residentialDetails().city()).isEqualTo(
            "Mumbai"
        );

        Optional<UserDTO> fetched = userService.getUser(userId);
        assertThat(fetched).isPresent();
        assertThat(fetched.get().email()).isEqualTo("updated@test.com");
    }

    private static @NonNull UserUpdateRequest getUserUpdateRequest(
        UserCreateRequest createReq,
        Long userId
    ) {
        OfficialDetailDTO offDet = new OfficialDetailDTO(
            userId,
            "E100",
            "1234 St",
            "Ludhiana",
            "Punjab",
            "India",
            "+91 1234567890",
            "company@mail.com",
            "company"
        );
        ResidentialDetailDTO resUpdate = new ResidentialDetailDTO(
            userId,
            "New Address",
            "Mumbai",
            "Maharashtra",
            "India",
            "900",
            "901"
        );

        UserDetailDTO detailUpdate = new UserDetailDTO(
            userId,
            "John",
            "Doe",
            26,
            Gender.MALE,
            MaritalStatus.MARRIED,
            resUpdate,
            offDet
        );

        return new UserUpdateRequest(
            userId,
            "updated@test.com",
            "123434",
            true,
            Instant.now(),
            detailUpdate
        );
    }

    @Test
    void deleteUser_Integration_ShouldDeleteUserAndAllRelatedEntities() {
        // Create a user first
        UserCreateRequest createReq = getUserCreateRequest();
        UserDTO createdUser = userService.createUser(createReq);
        Long userId = createdUser.id();

        // Verify user exists
        assertThat(userRepository.findById(userId)).isPresent();
        assertThat(userService.getUser(userId)).isPresent();

        // Execute delete
        UserDTO deletedUser = userService.deleteUser(userId);

        // Verify the returned DTO contains the correct data
        assertThat(deletedUser).isNotNull();
        assertThat(deletedUser.id()).isEqualTo(userId);
        assertThat(deletedUser.email()).isEqualTo("john.doe@example.com");
        assertThat(deletedUser.userDetails()).isNotNull();
        assertThat(deletedUser.userDetails().firstName()).isEqualTo("John");
        assertThat(deletedUser.userDetails().residentialDetails()).isNotNull();
        assertThat(deletedUser.userDetails().officialDetails()).isNotNull();

        // Verify user is actually deleted from database
        assertThat(userRepository.findById(userId)).isEmpty();
        assertThat(userService.getUser(userId)).isEmpty();

        // Verify total user count is 0
        assertThat(userRepository.count()).isEqualTo(0);
    }

    @Test
    void deleteUser_Integration_ShouldThrowException_WhenUserDoesNotExist() {
        Long nonExistentId = 999L;

        // Verify user doesn't exist
        assertThat(userRepository.findById(nonExistentId)).isEmpty();

        // Execute and verify exception
        assertThrows(UserDoesNotExistException.class, () ->
            userService.deleteUser(nonExistentId)
        );
    }
}
