package com.joshtechnologygroup.minisocial.service;

import com.joshtechnologygroup.minisocial.bean.OfficialDetail;
import com.joshtechnologygroup.minisocial.bean.ResidentialDetail;
import com.joshtechnologygroup.minisocial.bean.User;
import com.joshtechnologygroup.minisocial.bean.UserDetail;
import com.joshtechnologygroup.minisocial.dto.user.*;
import com.joshtechnologygroup.minisocial.dto.userDetail.UserDetailDTO;
import com.joshtechnologygroup.minisocial.exception.UserDoesNotExistException;
import com.joshtechnologygroup.minisocial.exception.ValueConflictException;
import com.joshtechnologygroup.minisocial.factory.OfficialDetailFactory;
import com.joshtechnologygroup.minisocial.factory.ResidentialDetailFactory;
import com.joshtechnologygroup.minisocial.factory.UserDetailFactory;
import com.joshtechnologygroup.minisocial.factory.UserFactory;
import com.joshtechnologygroup.minisocial.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void getActiveUsers_ShouldReturnPagedResults() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<ActiveUserDTO> activeUsers = List.of(
            new ActiveUserDTO(1L, "user1@test.com"),
            new ActiveUserDTO(2L, "user2@test.com")
        );
        Page<ActiveUserDTO> expectedPage = new PageImpl<>(
            activeUsers,
            pageable,
            2
        );

        when(userRepository.findActiveUsers(pageable)).thenReturn(expectedPage);

        // Act
        Page<ActiveUserDTO> result = userService.getActiveUsers(pageable);

        // Assert
        assertEquals(2, result.getContent().size());
        assertEquals(2, result.getTotalElements());
        assertEquals(0, result.getNumber());
        assertEquals(10, result.getSize());
        verify(userRepository).findActiveUsers(pageable);
    }

    @Test
    void createUser_ShouldReturnFullUserDTO_WhenInputIsValid() {
        // Prepare Request
        UserCreateRequest request =
            UserFactory.defaultUserCreateRequest().build();
        User user = UserFactory.defaultUser();
        UserDTO expectedResponse = UserFactory.defaultUserDTO(user).build();

        // Mock Mapper behaviors
        when(userMapper.createDtoToUser(any())).thenReturn(user);

        // Mock Repository behaviors
        when(userRepository.saveAndFlush(any())).thenReturn(user);

        // Mock the Final DTO Mapping
        when(userMapper.toDto(any())).thenReturn(expectedResponse);

        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");

        // Execute
        UserDTO result = userService.createUser(request);

        // Verify
        assertNotNull(result);
        assertEquals(user.getId(), result.id());
        assertEquals(user.getEmail(), result.email());
        assertNotNull(result.userDetails());

        verify(userRepository, times(1)).saveAndFlush(any());
    }

    @Test
    void createUser_ShouldThrowException_WhenEmailAlreadyExists() {
        UserCreateRequest request =
            UserFactory.defaultUserCreateRequest().build();

        when(userRepository.findByEmail(request.email())).thenReturn(
            Optional.of(new User())
        );

        assertThrows(ValueConflictException.class, () ->
            userService.createUser(request)
        );
    }

    @Test
    void getUser_ShouldReturnUserDTO_WhenUserExists() {
        // Setup
        User userEntity = UserFactory.defaultUser();

        UserDetail userDetail = UserDetailFactory.defaultUserDetail(
            userEntity.getId()
        );
        ResidentialDetail resDetail =
            ResidentialDetailFactory.defaultResidentialDetail(
                userEntity.getId()
            );
        OfficialDetail offDetail = OfficialDetailFactory.defaultOfficialDetail(
            userEntity.getId()
        );
        userEntity.setUserDetail(userDetail);
        userEntity.setResidentialDetail(resDetail);
        userEntity.setOfficialDetail(offDetail);

        // Create DTO that matches the entity data
        UserDetailDTO mockDetailDTO = UserDetailFactory.defaultUserDetailDTO(
            userDetail
        ).build();
        UserDTO expectedDTO = UserFactory.defaultUserDTO(userEntity)
            .userDetails(mockDetailDTO)
            .build();

        // Mock behavior
        when(userRepository.findById(userEntity.getId())).thenReturn(
            Optional.of(userEntity)
        );
        when(userMapper.toDto(userEntity)).thenReturn(expectedDTO);

        // Execute
        UserDTO result = userService.getUser(userEntity.getId());

        // Verify
        assertNotNull(result);
        assertEquals(userEntity.getEmail(), result.email());
        assertEquals(
            mockDetailDTO.firstName(),
            result.userDetails().firstName()
        );

        verify(userRepository).findById(userEntity.getId());
    }

    @Test
    void getUser_ShouldReturnEmpty_WhenUserDoesNotExist() {
        // Mock
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // Execute
        assertThrows(UserDoesNotExistException.class, () ->
            userService.getUser(99L)
        );

        // Verify
        verifyNoInteractions(userMapper);
    }

    @Test
    void updateUser_ShouldReturnUpdatedDTO_WhenUserExists() {
        // 1. Arrange
        User user = UserFactory.defaultUser();
        UserUpdateRequest updateReq = UserFactory.defaultUserUpdateRequest(
            user
        ).build();

        User updatedUser = UserFactory.defaultUser();
        updatedUser.setId(user.getId());
        updatedUser.setEmail("new-email@company.com");

        // Mocking Logic
        when(userRepository.findByEmail(user.getEmail())).thenReturn(
            Optional.of(user)
        );

        // Mocking the return DTO construction
        UserDTO finalDTO = UserFactory.defaultUserDTO(user)
            .email("new-email@company.com")
            .build();

        when(userMapper.toDto(any())).thenReturn(finalDTO);

        // 2. Act
        UserDTO result = userService.updateUser(updateReq, user.getEmail());

        // 3. Assert
        assertEquals("new-email@company.com", result.email());
        verify(userRepository).saveAndFlush(user);
        verify(userMapper).updateUserFromDto(updateReq, user);
    }

    @Test
    void updateUser_ShouldThrowException_WhenUserNotFound() {
        UserUpdateRequest req = UserFactory.defaultUserUpdateRequest().build();
        when(userRepository.findByEmail("test@mail.com")).thenReturn(
            Optional.empty()
        );

        assertThrows(UserDoesNotExistException.class, () ->
            userService.updateUser(req, "test@mail.com")
        );
        verify(userRepository, never()).saveAndFlush(any());
    }

    @Test
    void deleteUser_ShouldReturnUserDTO_WhenUserExists() {
        // Setup
        User userEntity = UserFactory.defaultUser();
        Long userId = userEntity.getId();

        UserDetail userDetail = UserDetailFactory.defaultUserDetail(userId);
        ResidentialDetail resDetail =
            ResidentialDetailFactory.defaultResidentialDetail(userId);
        OfficialDetail offDetail = OfficialDetailFactory.defaultOfficialDetail(
            userId
        );
        userEntity.setUserDetail(userDetail);
        userEntity.setResidentialDetail(resDetail);
        userEntity.setOfficialDetail(offDetail);

        // Create DTO that matches the entity data
        UserDetailDTO mockDetailDTO = UserDetailFactory.defaultUserDetailDTO(
            userDetail
        ).build();
        UserDTO expectedDTO = UserFactory.defaultUserDTO(userEntity)
            .userDetails(mockDetailDTO)
            .build();

        // Mock behavior
        when(userRepository.findByEmail(userEntity.getEmail())).thenReturn(
            Optional.of(userEntity)
        );
        when(userMapper.toDto(userEntity)).thenReturn(expectedDTO);

        // Execute
        UserDTO result = userService.deleteUser(userEntity.getEmail());

        // Verify
        assertNotNull(result);
        assertEquals(userId, result.id());
        assertEquals(userEntity.getEmail(), result.email());
        assertEquals(
            mockDetailDTO.firstName(),
            result.userDetails().firstName()
        );

        verify(userRepository).findByEmail(userEntity.getEmail());
        verify(userRepository).deleteById(userId);
        verify(userMapper).toDto(userEntity);
    }

    @Test
    void deleteUser_ShouldThrowException_WhenUserDoesNotExist() {
        // Setup
        String nonExistentEmail = "fake@mail.com";
        when(userRepository.findByEmail(nonExistentEmail)).thenReturn(
            Optional.empty()
        );

        // Execute & Verify
        assertThrows(UserDoesNotExistException.class, () ->
            userService.deleteUser(nonExistentEmail)
        );

        verify(userRepository).findByEmail(nonExistentEmail);
        verify(userRepository, never()).deleteById(any());
        verifyNoInteractions(userMapper);
    }
}
