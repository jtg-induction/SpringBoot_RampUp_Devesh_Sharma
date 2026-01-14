package com.joshtechnologygroup.minisocial.service;

import com.joshtechnologygroup.minisocial.dto.user.*;
import com.joshtechnologygroup.minisocial.enums.MaritalStatus;
import com.joshtechnologygroup.minisocial.dto.user.UserDTO;
import com.joshtechnologygroup.minisocial.exception.UserDoesNotExistException;
import com.joshtechnologygroup.minisocial.factory.ResidentialDetailFactory;
import com.joshtechnologygroup.minisocial.factory.UserDetailFactory;
import com.joshtechnologygroup.minisocial.factory.UserFactory;
import com.joshtechnologygroup.minisocial.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
        UserCreateRequest mainRequest = UserFactory.defaultUserCreateRequest()
                .email("john.doe@example.com")
                .build();

        UserDTO result = userService.createUser(mainRequest);

        // Verify Results
        assertThat(result).isNotNull();
        assertThat(result.id()).isNotNull();
        assertThat(result.email()).isEqualTo("john.doe@example.com");

        assertThat(result.userDetails()).isNotNull();
        assertThat(result.userDetails()
                .firstName()).isNotNull();
        assertThat(result.userDetails()
                .gender()).isNotNull();

        assertThat(result.userDetails()
                .residentialDetails()).isNotNull();
        assertThat(result.userDetails()
                .residentialDetails()
                .city()).isNotNull();

        assertThat(result.userDetails()
                .officialDetails()).isNotNull();
        assertThat(result.userDetails()
                .officialDetails()
                .companyName()).isNotNull();

        assertThat(userRepository.count()).isEqualTo(1);
        assertThat(userRepository.findByEmail("john.doe@example.com")).isPresent();
    }

    @Test
    void getUser_Integration_ShouldFetchFullGraphFromDatabase() {
        UserCreateRequest request = UserFactory.defaultUserCreateRequest()
                .email("john.doe@example.com")
                .build();
        UserDTO savedUser = userService.createUser(request);
        Long id = savedUser.id();

        Optional<UserDTO> result = userService.getUser(id);

        assertThat(result).isPresent();
        UserDTO dto = result.orElseThrow();

        assertThat(dto.email()).isEqualTo("john.doe@example.com");
        assertThat(dto.userDetails()).isNotNull();

        assertThat(dto.userDetails()
                .residentialDetails()).isNotNull();
        assertThat(dto.userDetails()
                .officialDetails()).isNotNull();

        // Verify all nested data is properly loaded
        assertThat(dto.userDetails()
                .firstName()).isNotNull();
        assertThat(dto.userDetails()
                .residentialDetails()
                .city()).isNotNull();
        assertThat(dto.userDetails()
                .officialDetails()
                .companyName()).isNotNull();
    }

    @Test
    void getActiveUsers_Integration_ShouldOnlyReturnActiveUsers() {
        // Create users through service layer (more realistic integration test)
        UserCreateRequest activeUserReq = UserFactory.defaultUserCreateRequest()
                .email("active@company.com")
                .active(true)
                .build();
        userService.createUser(activeUserReq);

        UserCreateRequest inactiveUserReq = UserFactory.defaultUserCreateRequest()
                .email("inactive@company.com")
                .active(false)
                .build();
        userService.createUser(inactiveUserReq);

        // Execute
        List<UserDTO> result = userService.getAllUsers(UserFactory.activeUserQueryParams());

        // Verify
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0)).isNotNull();
        assertThat(result.get(0)
                .email()).isEqualTo("active@company.com");
    }

    @Test
    void updateUser_Integration_ShouldReflectChangesInDatabase() {
        // Create a user
        UserCreateRequest createReq = UserFactory.defaultUserCreateRequest()
                .build();
        UserDTO initialUser = userService.createUser(createReq);
        Long userId = initialUser.id();

        // Prepare Update Request with changes
        UserUpdateRequest updateReq = UserFactory.defaultUserUpdateRequest()
                .email("updated@test.com")
                .userDetails(UserDetailFactory.defaultUserDetailDTO(userId)
                        .maritalStatus(MaritalStatus.MARRIED)
                        .residentialDetails(ResidentialDetailFactory.defaultResidentialDetailDTO(userId)
                                .city("Mumbai")
                                .build())
                        .build())
                .build();

        // Execute
        UserDTO result = userService.updateUser(updateReq, initialUser.email(), userId);

        // Verify
        assertThat(result.email()).isEqualTo("updated@test.com");
        assertThat(result.userDetails()
                .maritalStatus()).isEqualTo(MaritalStatus.MARRIED);
        assertThat(result.userDetails()
                .residentialDetails()
                .city()).isEqualTo("Mumbai");

        Optional<UserDTO> fetched = userService.getUser(userId);
        assertThat(fetched).isPresent();
        assertThat(fetched.orElseThrow()
                .email()).isEqualTo("updated@test.com");
    }

    @Test
    void deleteUser_Integration_ShouldDeleteUserAndAllRelatedEntities() {
        // Create a user first
        UserCreateRequest createReq = UserFactory.defaultUserCreateRequest()
                .email("john.doe@example.com")
                .build();
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
        assertThat(deletedUser.userDetails()
                .firstName()).isNotNull();
        assertThat(deletedUser.userDetails()
                .residentialDetails()).isNotNull();
        assertThat(deletedUser.userDetails()
                .officialDetails()).isNotNull();

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
