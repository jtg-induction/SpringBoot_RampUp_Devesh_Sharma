package com.joshtechnologygroup.minisocial.service;

import com.joshtechnologygroup.minisocial.bean.OfficialDetail;
import com.joshtechnologygroup.minisocial.bean.ResidentialDetail;
import com.joshtechnologygroup.minisocial.bean.User;
import com.joshtechnologygroup.minisocial.bean.UserDetail;
import com.joshtechnologygroup.minisocial.dto.user.*;
import com.joshtechnologygroup.minisocial.dto.userDetail.UserDetailDTO;
import com.joshtechnologygroup.minisocial.dto.userDetail.UserDetailMapper;
import com.joshtechnologygroup.minisocial.enums.UserSortOrder;
import com.joshtechnologygroup.minisocial.exception.UserDoesNotExistException;
import com.joshtechnologygroup.minisocial.factory.OfficialDetailFactory;
import com.joshtechnologygroup.minisocial.factory.ResidentialDetailFactory;
import com.joshtechnologygroup.minisocial.factory.UserDetailFactory;
import com.joshtechnologygroup.minisocial.factory.UserFactory;
import com.joshtechnologygroup.minisocial.repository.ResidentialDetailRepository;
import com.joshtechnologygroup.minisocial.repository.UserDetailRepository;
import com.joshtechnologygroup.minisocial.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.domain.Specification;
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
    private UserDetailRepository userDetailRepository;

    @Mock
    private ResidentialDetailRepository residentialDetailRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserDetailMapper userDetailMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void createUser_ShouldReturnFullUserDTO_WhenInputIsValid() {
        // Prepare Request
        UserCreateRequest request = UserFactory.defaultUserCreateRequest()
                .build();
        User user = UserFactory.defaultUser();
        UserDTO expectedResponse = UserFactory.defaultUserDTO(user)
                .build();

        // Mock Mapper behaviors
        when(userMapper.createDtoToUser(any())).thenReturn(user);

        // Mock Repository behaviors
        when(userRepository.save(any())).thenReturn(user);

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

        verify(userRepository, times(1)).save(any());
    }

    @Test
    void createUser_ShouldThrowException_WhenEmailAlreadyExists() {
        UserCreateRequest request = UserFactory.defaultUserCreateRequest()
                .build();

        when(userMapper.createDtoToUser(any())).thenReturn(new User());
        when(userRepository.save(any())).thenThrow(new DataIntegrityViolationException("Email exists"));
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");

        assertThrows(DataIntegrityViolationException.class, () -> userService.createUser(request));

        verifyNoInteractions(userDetailRepository);
        verifyNoInteractions(residentialDetailRepository);
    }

    @Test
    void getUser_ShouldReturnUserDTO_WhenUserExists() {
        // Setup
        User userEntity = UserFactory.defaultUser();

        UserDetail userDetail = UserDetailFactory.defaultUserDetail(userEntity.getId());
        ResidentialDetail resDetail = ResidentialDetailFactory.defaultResidentialDetail(userEntity.getId());
        OfficialDetail offDetail = OfficialDetailFactory.defaultOfficialDetail(userEntity.getId());
        userEntity.setUserDetail(userDetail);
        userEntity.setResidentialDetail(resDetail);
        userEntity.setOfficialDetail(offDetail);

        // Create DTO that matches the entity data
        UserDetailDTO mockDetailDTO = UserDetailFactory.defaultUserDetailDTO(userDetail)
                .build();
        UserDTO expectedDTO = UserFactory.defaultUserDTO(userEntity)
                .userDetails(mockDetailDTO)
                .build();

        // Mock behavior
        when(userRepository.findById(userEntity.getId())).thenReturn(
                Optional.of(userEntity)
        );
        when(userMapper.toDto(userEntity)).thenReturn(
                expectedDTO
        );

        // Execute
        UserDTO result = userService.getUser(userEntity.getId());

        // Verify
        assertNotNull(result);
        assertEquals(userEntity.getEmail(), result
                .email());
        assertEquals(mockDetailDTO.firstName(), result
                .userDetails()
                .firstName());

        verify(userRepository).findById(userEntity.getId());
    }

    @Test
    void getUser_ShouldReturnEmpty_WhenUserDoesNotExist() {
        // Mock
        when(userRepository.findById(99L)).thenReturn(
                Optional.empty()
        );

        // Execute
        assertThrows(UserDoesNotExistException.class, () ->
                userService.getUser(99L)
        );

        // Verify
        verifyNoInteractions(userDetailMapper);
        verifyNoInteractions(userMapper);
    }

    @Test
    void updateUser_ShouldReturnUpdatedDTO_WhenUserExists() {
        // 1. Arrange
        User user = UserFactory.defaultUser();
        UserUpdateRequest updateReq = UserFactory.defaultUserUpdateRequest(user)
                .build();

        User updatedUser = UserFactory.defaultUser();
        updatedUser.setId(user.getId());
        updatedUser.setEmail("new-email@company.com");

        // Mocking Logic
        when(userRepository.findByEmail(user.getEmail())).thenReturn(
                Optional.of(user)
        );
        when(userMapper.updateDtoToUser(updateReq)).thenReturn(updatedUser);

        // Mocking the return DTO construction
        UserDTO finalDTO = UserFactory.defaultUserDTO(user)
                .email("new-email@company.com")
                .build();

        when(userMapper.toDto(any())).thenReturn(finalDTO);

        // 2. Act
        UserDTO result = userService.updateUser(updateReq, user.getEmail());

        // 3. Assert
        assertEquals("new-email@company.com", result.email());
        verify(userRepository).save(updatedUser);
    }

    @Test
    void updateUser_ShouldThrowException_WhenUserNotFound() {
        UserUpdateRequest req = UserFactory.defaultUserUpdateRequest()
                .email("ghost@test.com")
                .build();
        when(userRepository.findByEmail("test@mail.com")).thenReturn(Optional.empty());

        assertThrows(UserDoesNotExistException.class, () ->
                userService.updateUser(req, "test@mail.com")
        );
        verify(userRepository, never()).save(any());
    }

    @Test
    void deleteUser_ShouldReturnUserDTO_WhenUserExists() {
        // Setup
        User userEntity = UserFactory.defaultUser();
        Long userId = userEntity.getId();

        UserDetail userDetail = UserDetailFactory.defaultUserDetail(userId);
        ResidentialDetail resDetail = ResidentialDetailFactory.defaultResidentialDetail(userId);
        OfficialDetail offDetail = OfficialDetailFactory.defaultOfficialDetail(userId);
        userEntity.setUserDetail(userDetail);
        userEntity.setResidentialDetail(resDetail);
        userEntity.setOfficialDetail(offDetail);

        // Create DTO that matches the entity data
        UserDetailDTO mockDetailDTO = UserDetailFactory.defaultUserDetailDTO(userDetail)
                .build();
        UserDTO expectedDTO = UserFactory.defaultUserDTO(userEntity)
                .userDetails(mockDetailDTO)
                .build();

        // Mock behavior
        when(userRepository.findByEmail(userEntity.getEmail())).thenReturn(
                Optional.of(userEntity)
        );
        when(userMapper.toDto(userEntity)).thenReturn(
                expectedDTO
        );

        // Execute
        UserDTO result = userService.deleteUser(userEntity.getEmail());

        // Verify
        assertNotNull(result);
        assertEquals(userId, result.id());
        assertEquals(userEntity.getEmail(), result.email());
        assertEquals(mockDetailDTO.firstName(), result.userDetails()
                .firstName());

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
        verifyNoInteractions(userDetailMapper);
        verifyNoInteractions(userMapper);
    }

    @Test
    void getAllUsers_ShouldApplySingleSortOrder_WhenSingleSortOrderProvided() {
        // Arrange
        List<UserSortOrder> sortOrders = List.of(UserSortOrder.EMAIL);
        UserQueryParams queryParams = UserFactory.userQueryParamsWithSort(sortOrders);

        User user1 = UserFactory.defaultUser();
        user1.setEmail("b@test.com");
        User user2 = UserFactory.defaultUser();
        user2.setEmail("a@test.com");
        List<User> users = List.of(user1, user2);

        // Create UserDetailDTOs
        UserDetailDTO detailDTO1 = UserDetailFactory.defaultUserDetailDTO(user1.getId())
                .build();
        UserDetailDTO detailDTO2 = UserDetailFactory.defaultUserDetailDTO(user2.getId())
                .build();
        UserDTO userDTO1 = UserFactory.defaultUserDTO(user1)
                .userDetails(detailDTO1)
                .build();
        UserDTO userDTO2 = UserFactory.defaultUserDTO(user2)
                .userDetails(detailDTO2)
                .build();

        when(userRepository.findAll(any(Specification.class))).thenReturn(users);
        when(userMapper.toDto(any(User.class)))
                .thenReturn(userDTO1, userDTO2);

        // Execute
        List<UserDTO> result = userService.getAllUsers(queryParams);

        // Verify
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userRepository).findAll(any(Specification.class));
    }

    @Test
    void getAllUsers_ShouldApplyMultipleSortOrders_WhenMultipleSortOrdersProvided() {
        // Arrange
        List<UserSortOrder> sortOrders = List.of(UserSortOrder.NAME, UserSortOrder.EMAIL, UserSortOrder.GENDER);
        UserQueryParams queryParams = UserFactory.userQueryParamsWithSort(sortOrders);

        User user1 = UserFactory.defaultUser();
        user1.setEmail("user1@test.com");
        User user2 = UserFactory.defaultUser();
        user2.setEmail("user2@test.com");
        User user3 = UserFactory.defaultUser();
        user3.setEmail("user3@test.com");
        List<User> users = List.of(user1, user2, user3);

        // Create UserDetailDTOs
        UserDetailDTO detailDTO1 = UserDetailFactory.defaultUserDetailDTO(user1.getId())
                .build();
        UserDetailDTO detailDTO2 = UserDetailFactory.defaultUserDetailDTO(user2.getId())
                .build();
        UserDetailDTO detailDTO3 = UserDetailFactory.defaultUserDetailDTO(user3.getId())
                .build();
        UserDTO userDTO1 = UserFactory.defaultUserDTO(user1)
                .userDetails(detailDTO1)
                .build();
        UserDTO userDTO2 = UserFactory.defaultUserDTO(user2)
                .userDetails(detailDTO2)
                .build();
        UserDTO userDTO3 = UserFactory.defaultUserDTO(user3)
                .userDetails(detailDTO3)
                .build();

        // Use lenient stubbing to avoid strict argument matching issues with random data
        when(userRepository.findAll(any(Specification.class))).thenReturn(users);
        when(userMapper.toDto(any(User.class)))
                .thenReturn(userDTO1, userDTO2, userDTO3);

        // Execute
        List<UserDTO> result = userService.getAllUsers(queryParams);

        // Verify
        assertNotNull(result);
        assertEquals(3, result.size());
        verify(userRepository).findAll(any(Specification.class));
    }
}
