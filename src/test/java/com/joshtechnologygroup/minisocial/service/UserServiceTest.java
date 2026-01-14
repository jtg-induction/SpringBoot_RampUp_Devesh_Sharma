package com.joshtechnologygroup.minisocial.service;

import com.joshtechnologygroup.minisocial.bean.OfficialDetail;
import com.joshtechnologygroup.minisocial.bean.ResidentialDetail;
import com.joshtechnologygroup.minisocial.bean.User;
import com.joshtechnologygroup.minisocial.bean.UserDetail;
import com.joshtechnologygroup.minisocial.dto.officialDetail.OfficialDetailMapper;
import com.joshtechnologygroup.minisocial.dto.residentialDetail.ResidentialDetailMapper;
import com.joshtechnologygroup.minisocial.dto.user.*;
import com.joshtechnologygroup.minisocial.dto.userDetail.UserDetailCreateRequest;
import com.joshtechnologygroup.minisocial.dto.userDetail.UserDetailDTO;
import com.joshtechnologygroup.minisocial.dto.userDetail.UserDetailMapper;
import com.joshtechnologygroup.minisocial.exception.UserDoesNotExistException;
import com.joshtechnologygroup.minisocial.factory.OfficialDetailFactory;
import com.joshtechnologygroup.minisocial.factory.ResidentialDetailFactory;
import com.joshtechnologygroup.minisocial.factory.UserDetailFactory;
import com.joshtechnologygroup.minisocial.factory.UserFactory;
import com.joshtechnologygroup.minisocial.repository.OfficialDetailRepository;
import com.joshtechnologygroup.minisocial.repository.ResidentialDetailRepository;
import com.joshtechnologygroup.minisocial.repository.UserDetailRepository;
import com.joshtechnologygroup.minisocial.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

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
    private OfficialDetailRepository officialDetailRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserDetailMapper userDetailMapper;

    @Mock
    private ResidentialDetailMapper residentialDetailMapper;

    @Mock
    private OfficialDetailMapper officialDetailMapper;

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
        UserDetail userDetail = UserDetailFactory.defaultUserDetail(user.getId());
        ResidentialDetail resDetail = ResidentialDetailFactory.defaultResidentialDetail(user.getId());
        OfficialDetail offDetail = OfficialDetailFactory.defaultOfficialDetail(user.getId());

        UserDetailDTO detailDTO = UserDetailFactory.defaultUserDetailDTO(user.getId())
                .build();
        UserDTO expectedResponse = UserFactory.defaultUserDTO(user)
                .build();

        // Mock Mapper behaviors
        when(userMapper.createDtoToUser(any())).thenReturn(user);
        when(userDetailMapper.toUserDetail(any())).thenReturn(userDetail);
        when(residentialDetailMapper.toResidentialDetail(any())).thenReturn(resDetail);
        when(officialDetailMapper.toOfficialDetail(any())).thenReturn(offDetail);

        // Mock Repository behaviors
        when(userRepository.save(any())).thenReturn(user);

        // Mock the Final DTO Mapping
        when(userDetailMapper.toDto(any(), any(), any())).thenReturn(detailDTO);
        when(userMapper.toDto(any(), any())).thenReturn(expectedResponse);

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
        when(userDetailMapper.toUserDetail(any())).thenReturn(new UserDetail());
        when(residentialDetailMapper.toResidentialDetail(any())).thenReturn(new ResidentialDetail());
        when(officialDetailMapper.toOfficialDetail(any())).thenReturn(new OfficialDetail());

        assertThrows(DataIntegrityViolationException.class, () -> userService.createUser(request));

        verifyNoInteractions(userDetailRepository);
        verifyNoInteractions(residentialDetailRepository);
    }

    @Test
    void createUser_ShouldHandleNullNestedDetails() {
        // Request with null officialDetails
        UserDetailCreateRequest userDetailCreateRequest = UserDetailFactory.defaultUserDetailCreateRequest()
                .officialDetails(null)
                .build();
        UserCreateRequest request = UserFactory.defaultUserCreateRequest()
                .userDetails(userDetailCreateRequest)
                .build();

        when(userMapper.createDtoToUser(any())).thenReturn(new User());

        assertThrows(NullPointerException.class, () ->
                userService.createUser(request)
        );
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
        when(
                userDetailMapper.toDto(userDetail, resDetail, offDetail)
        ).thenReturn(mockDetailDTO);
        when(userMapper.toDto(userEntity, mockDetailDTO)).thenReturn(
                expectedDTO
        );

        // Execute
        Optional<UserDTO> result = userService.getUser(userEntity.getId());

        // Verify
        assertTrue(result.isPresent());
        assertEquals(userEntity.getEmail(), result.get()
                .email());
        assertEquals(mockDetailDTO.firstName(), result.get()
                .userDetails()
                .firstName());

        verify(userRepository).findById(userEntity.getId());
        verify(userDetailMapper).toDto(any(), any(), any());
    }

    @Test
    void getUser_ShouldReturnEmpty_WhenUserDoesNotExist() {
        // Mock
        when(userRepository.findById(99L)).thenReturn(
                Optional.empty()
        );

        // Execute
        Optional<UserDTO> result = userService.getUser(99L);

        // Verify
        assertTrue(result.isEmpty());
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

        UserDetail updatedDetail = UserDetailFactory.defaultUserDetail(user.getId());
        ResidentialDetail updatedRes = ResidentialDetailFactory.defaultResidentialDetail(user.getId());
        OfficialDetail updatedOff = OfficialDetailFactory.defaultOfficialDetail(user.getId());

        // Mocking Logic
        when(userRepository.findById(user.getId())).thenReturn(
                Optional.of(user)
        );
        when(userMapper.updateDtoToUser(updateReq)).thenReturn(updatedUser);
        when(userDetailMapper.dtoToUserDetail(any())).thenReturn(updatedDetail);
        when(residentialDetailMapper.dtoToResidentialDetail(any())).thenReturn(
                updatedRes
        );
        when(officialDetailMapper.dtoToOfficialDetail(any())).thenReturn(
                updatedOff
        );

        // Mocking the return DTO construction
        UserDetailDTO detailDTO = UserDetailFactory.defaultUserDetailDTO(user.getId())
                .build();
        UserDTO finalDTO = UserFactory.defaultUserDTO(user)
                .email("new-email@company.com")
                .build();

        when(userDetailMapper.toDto(any(), any(), any())).thenReturn(detailDTO);
        when(userMapper.toDto(any(), any())).thenReturn(finalDTO);

        // 2. Act
        UserDTO result = userService.updateUser(updateReq, user.getEmail(), user.getId());

        // 3. Assert
        assertEquals("new-email@company.com", result.email());
        verify(userRepository).save(updatedUser);
        verify(userDetailRepository).save(updatedDetail);
        verify(residentialDetailRepository).save(updatedRes);
        verify(officialDetailRepository).save(updatedOff);
    }

    @Test
    void updateUser_ShouldThrowException_WhenUserNotFound() {
        UserUpdateRequest req = UserFactory.defaultUserUpdateRequest()
                .email("ghost@test.com")
                .build();
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(UserDoesNotExistException.class, () ->
                userService.updateUser(req, "test@mail.com", 999L)
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
        when(userRepository.findById(userId)).thenReturn(
                Optional.of(userEntity)
        );
        when(
                userDetailMapper.toDto(userDetail, resDetail, offDetail)
        ).thenReturn(mockDetailDTO);
        when(userMapper.toDto(userEntity, mockDetailDTO)).thenReturn(
                expectedDTO
        );

        // Execute
        UserDTO result = userService.deleteUser(userId);

        // Verify
        assertNotNull(result);
        assertEquals(userId, result.id());
        assertEquals(userEntity.getEmail(), result.email());
        assertEquals(mockDetailDTO.firstName(), result.userDetails()
                .firstName());

        verify(userRepository).findById(userId);
        verify(userRepository).deleteById(userId);
        verify(userDetailMapper).toDto(userDetail, resDetail, offDetail);
        verify(userMapper).toDto(userEntity, mockDetailDTO);
    }

    @Test
    void deleteUser_ShouldThrowException_WhenUserDoesNotExist() {
        // Setup
        Long nonExistentId = 999L;
        when(userRepository.findById(nonExistentId)).thenReturn(
                Optional.empty()
        );

        // Execute & Verify
        assertThrows(UserDoesNotExistException.class, () ->
                userService.deleteUser(nonExistentId)
        );

        verify(userRepository).findById(nonExistentId);
        verify(userRepository, never()).deleteById(any());
        verifyNoInteractions(userDetailMapper);
        verifyNoInteractions(userMapper);
    }
}
