package com.joshtechnologygroup.minisocial.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.joshtechnologygroup.minisocial.bean.User;
import com.joshtechnologygroup.minisocial.dto.user.ActiveUserDTO;
import com.joshtechnologygroup.minisocial.dto.user.UserCreateRequest;
import com.joshtechnologygroup.minisocial.dto.user.UserDTO;
import com.joshtechnologygroup.minisocial.dto.user.UserUpdateRequest;
import com.joshtechnologygroup.minisocial.enums.MaritalStatus;
import com.joshtechnologygroup.minisocial.exception.UserDoesNotExistException;
import com.joshtechnologygroup.minisocial.factory.ResidentialDetailFactory;
import com.joshtechnologygroup.minisocial.factory.UserDetailFactory;
import com.joshtechnologygroup.minisocial.factory.UserFactory;
import com.joshtechnologygroup.minisocial.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
        UserCreateRequest mainRequest = UserFactory.defaultUserCreateRequest()
            .email("john.doe@example.com")
            .build();

        UserDTO result = userService.createUser(mainRequest);

        // Verify Results
        assertThat(result).isNotNull();
        assertThat(result.id()).isNotNull();
        assertThat(result.email()).isEqualTo("john.doe@example.com");

        assertThat(result.userDetails()).isNotNull();
        assertThat(result.userDetails().firstName()).isNotNull();
        assertThat(result.userDetails().gender()).isNotNull();

        assertThat(result.userDetails().residentialDetails()).isNotNull();
        assertThat(
            result.userDetails().residentialDetails().city()
        ).isNotNull();

        assertThat(result.userDetails().officialDetails()).isNotNull();
        assertThat(
            result.userDetails().officialDetails().companyName()
        ).isNotNull();

        assertThat(userRepository.count()).isEqualTo(1);
        assertThat(
            userRepository.findByEmail("john.doe@example.com")
        ).isPresent();
    }

    @Test
    void getUser_Integration_ShouldFetchFullGraphFromDatabase() {
        UserCreateRequest request = UserFactory.defaultUserCreateRequest()
            .email("john.doe@example.com")
            .build();
        UserDTO savedUser = userService.createUser(request);
        Long id = savedUser.id();

        UserDTO dto = userService.getUser(id);

        assertThat(dto).isNotNull();

        assertThat(dto.email()).isEqualTo("john.doe@example.com");
        assertThat(dto.userDetails()).isNotNull();

        assertThat(dto.userDetails().residentialDetails()).isNotNull();
        assertThat(dto.userDetails().officialDetails()).isNotNull();

        // Verify all nested data is properly loaded
        assertThat(dto.userDetails().firstName()).isNotNull();
        assertThat(dto.userDetails().residentialDetails().city()).isNotNull();
        assertThat(
            dto.userDetails().officialDetails().companyName()
        ).isNotNull();
    }

    @Test
    void getActiveUsers_Integration_ShouldOnlyReturnActiveUsers() {
        // Create users through service layer (more realistic integration test)
        UserCreateRequest activeUserReq =
            UserFactory.defaultUserCreateRequest().build();
        userService.createUser(activeUserReq);

        UserCreateRequest inactiveUserReq =
            UserFactory.defaultUserCreateRequest().build();
        userService.createUser(inactiveUserReq);
        User user = userRepository.findByEmail(inactiveUserReq.email()).get();
        user.setActive(false);
        userRepository.save(user);

        // Execute
        Pageable pageable = PageRequest.of(0, 10);
        Page<ActiveUserDTO> result = userService.getActiveUsers(pageable);

        // Verify
        assertThat(result.getContent().size()).isEqualTo(1);
        assertThat(result.getContent().get(0)).isNotNull();
        assertThat(result.getContent().get(0).email()).isEqualTo(
            activeUserReq.email()
        );
    }

    @Test
    void updateUser_Integration_ShouldReflectChangesInDatabase() {
        // Create a user
        UserCreateRequest createReq =
            UserFactory.defaultUserCreateRequest().build();
        UserDTO initialUser = userService.createUser(createReq);
        Long userId = initialUser.id();

        // Prepare Update Request with changes
        UserUpdateRequest updateReq = UserFactory.defaultUserUpdateRequest()
            .userDetails(
                UserDetailFactory.defaultUserDetailUpdateRequest(userId)
                    .maritalStatus(MaritalStatus.MARRIED)
                    .residentialDetails(
                        ResidentialDetailFactory.defaultResidentialDetailUpdateRequest(
                            userId
                        )
                            .city("Mumbai")
                            .build()
                    )
                    .build()
            )
            .build();

        // Execute
        UserDTO result = userService.updateUser(updateReq, initialUser.email());

        // Verify
        assertThat(result.userDetails().maritalStatus()).isEqualTo(
            MaritalStatus.MARRIED
        );
        assertThat(result.userDetails().residentialDetails().city()).isEqualTo(
            "Mumbai"
        );

        UserDTO fetched = userService.getUser(userId);
        assertThat(fetched).isNotNull();
    }

    @Test
    void deleteUser_Integration_ShouldDeleteUserAndAllRelatedEntities() {
        // Create a user first
        UserCreateRequest createReq = UserFactory.defaultUserCreateRequest()
            .email("john.doe@example.com")
            .build();
        UserDTO createdUser = userService.createUser(createReq);
        String userEmail = createdUser.email();

        // Verify user exists
        assertThat(userRepository.findByEmail(userEmail)).isPresent();
        assertThat(userService.getUser(createdUser.id())).isNotNull();

        // Execute delete
        UserDTO deletedUser = userService.deleteUser(userEmail);

        // Verify the returned DTO contains the correct data
        assertThat(deletedUser).isNotNull();
        assertThat(deletedUser.id()).isEqualTo(createdUser.id());
        assertThat(deletedUser.email()).isEqualTo("john.doe@example.com");
        assertThat(deletedUser.userDetails()).isNotNull();
        assertThat(deletedUser.userDetails().firstName()).isNotNull();
        assertThat(deletedUser.userDetails().residentialDetails()).isNotNull();
        assertThat(deletedUser.userDetails().officialDetails()).isNotNull();

        // Verify user is actually deleted from database
        assertThat(userRepository.findByEmail(userEmail)).isEmpty();
        assertThrows(UserDoesNotExistException.class, () ->
            userService.getUser(createdUser.id())
        );

        // Verify total user count is 0
        assertThat(userRepository.count()).isEqualTo(0);
    }

    @Test
    void deleteUser_Integration_ShouldThrowException_WhenUserDoesNotExist() {
        String nonExistentEmail = "test@company.com";

        // Verify user doesn't exist
        assertThat(userRepository.findByEmail(nonExistentEmail)).isEmpty();

        // Execute and verify exception
        assertThrows(UserDoesNotExistException.class, () ->
            userService.deleteUser(nonExistentEmail)
        );
    }
}
