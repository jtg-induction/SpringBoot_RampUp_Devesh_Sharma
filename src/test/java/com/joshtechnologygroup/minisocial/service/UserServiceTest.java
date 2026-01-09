package com.joshtechnologygroup.minisocial.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.joshtechnologygroup.minisocial.bean.*;
import com.joshtechnologygroup.minisocial.dao.OfficialDetailRepository;
import com.joshtechnologygroup.minisocial.dao.ResidentialDetailRepository;
import com.joshtechnologygroup.minisocial.dao.UserDetailRepository;
import com.joshtechnologygroup.minisocial.dao.UserRepository;
import com.joshtechnologygroup.minisocial.dto.officialDetail.OfficialDetailCreateRequest;
import com.joshtechnologygroup.minisocial.dto.officialDetail.OfficialDetailDTO;
import com.joshtechnologygroup.minisocial.dto.officialDetail.OfficialDetailMapper;
import com.joshtechnologygroup.minisocial.dto.residentialDetail.ResidentialDetailCreateRequest;
import com.joshtechnologygroup.minisocial.dto.residentialDetail.ResidentialDetailDTO;
import com.joshtechnologygroup.minisocial.dto.residentialDetail.ResidentialDetailMapper;
import com.joshtechnologygroup.minisocial.dto.user.*;
import com.joshtechnologygroup.minisocial.dto.userDetail.UserDetailCreateRequest;
import com.joshtechnologygroup.minisocial.dto.userDetail.UserDetailDTO;
import com.joshtechnologygroup.minisocial.dto.userDetail.UserDetailMapper;
import com.joshtechnologygroup.minisocial.exception.UserDoesNotExistException;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

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

    @InjectMocks
    private UserService userService;

    @Test
    void createUser_ShouldReturnFullUserDTO_WhenInputIsValid() {
        // Prepare Request
        UserCreateRequest request = mockCreateRequest();
        User user = new User();
        user.setId(1L);
        user.setEmail("test@josh.com");
        UserDetail userDetail = new UserDetail();
        ResidentialDetail resDetail = new ResidentialDetail();
        OfficialDetail offDetail = new OfficialDetail();

        UserDetailDTO detailDTO = UserDetailDTO.builder().userId(1L).build();
        UserDTO expectedResponse = UserDTO.builder()
            .id(1L)
            .email("test@josh.com")
            .userDetails(detailDTO)
            .build();

        // Mock Mapper behaviors
        when(userMapper.createDtoToUser(any())).thenReturn(user);
        when(userDetailMapper.toUserDetail(any())).thenReturn(userDetail);
        when(residentialDetailMapper.toResidentialDetail(any())).thenReturn(
            resDetail
        );
        when(officialDetailMapper.toOfficialDetail(any())).thenReturn(
            offDetail
        );

        // Mock Repository behaviors
        when(userRepository.save(any())).thenReturn(user);
        when(userDetailRepository.save(any())).thenReturn(userDetail);

        // Mock the Final DTO Mapping
        when(userDetailMapper.toDto(any(), any(), any())).thenReturn(detailDTO);
        when(userMapper.toDto(any(), any())).thenReturn(expectedResponse);

        // Execute
        UserDTO result = userService.createUser(request);

        // Verify
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("test@josh.com", result.email());
        assertNotNull(result.userDetails());

        verify(userRepository, times(1)).save(any());
        verify(userDetailRepository, times(1)).save(any());
        verify(residentialDetailRepository, times(1)).save(any());
        verify(officialDetailRepository, times(1)).save(any());
    }

    @Test
    void createUser_ShouldThrowException_WhenEmailAlreadyExists() {
        UserCreateRequest request = mockCreateRequest();
        when(userMapper.createDtoToUser(any())).thenReturn(new User());

        when(userRepository.save(any())).thenThrow(
            new DataIntegrityViolationException("Email exists")
        );

        assertThrows(DataIntegrityViolationException.class, () ->
            userService.createUser(request)
        );

        verifyNoInteractions(userDetailRepository);
        verifyNoInteractions(residentialDetailRepository);
    }

    @Test
    void createUser_ShouldHandleNullNestedDetails() {
        // Request with null officialDetails
        UserCreateRequest request = new UserCreateRequest(
            "test@josh.com",
            "pass",
            true,
            new UserDetailCreateRequest(
                "John",
                "Doe",
                25,
                Gender.MALE,
                MaritalStatus.SINGLE,
                mockResRequest(),
                null
            )
        ); // null OfficialDetails

        when(userMapper.createDtoToUser(any())).thenReturn(new User());
        when(userRepository.save(any())).thenReturn(new User());

        assertThrows(NullPointerException.class, () ->
            userService.createUser(request)
        );
    }

    @Test
    void getUser_ShouldReturnUserDTO_WhenUserExists() {
        // Setup
        Long userId = 1L;
        User userEntity = new User();
        userEntity.setId(userId);
        userEntity.setEmail("alex@example.com");

        UserDetail userDetail = new UserDetail();
        ResidentialDetail resDetail = new ResidentialDetail();
        OfficialDetail offDetail = new OfficialDetail();

        PopulatedUser mockPopulated = new PopulatedUser(
            userEntity,
            userDetail,
            resDetail,
            offDetail
        );

        UserDetailDTO mockDetailDTO = UserDetailDTO.builder()
            .firstName("Alex")
            .build();
        UserDTO expectedDTO = UserDTO.builder()
            .id(userId)
            .email("alex@example.com")
            .userDetails(mockDetailDTO)
            .build();

        // Mock behavior
        when(userRepository.findUserPopulated(userId)).thenReturn(
            Optional.of(mockPopulated)
        );
        when(
            userDetailMapper.toDto(userDetail, resDetail, offDetail)
        ).thenReturn(mockDetailDTO);
        when(userMapper.toDto(userEntity, mockDetailDTO)).thenReturn(
            expectedDTO
        );

        // Execute
        Optional<UserDTO> result = userService.getUser(userId);

        // Verify
        assertTrue(result.isPresent());
        assertEquals("alex@example.com", result.get().email());
        assertEquals("Alex", result.get().userDetails().firstName());

        verify(userRepository).findUserPopulated(userId);
        verify(userDetailMapper).toDto(any(), any(), any());
    }

    @Test
    void getUser_ShouldReturnEmpty_WhenUserDoesNotExist() {
        // Mock
        when(userRepository.findUserPopulated(99L)).thenReturn(
            Optional.empty()
        );

        // Execute
        Optional<UserDTO> result = userService.getUser(99L);

        // Verify
        assertTrue(result.isEmpty());
        verifyNoInteractions(userDetailMapper);
        verifyNoInteractions(userMapper);
    }

    private ResidentialDetailCreateRequest mockResRequest() {
        return ResidentialDetailCreateRequest.builder()
            .address("123 Street")
            .city("Delhi")
            .state("Delhi")
            .country("India")
            .contactNo1("+91 1234567890")
            .contactNo1("+91 1234567890")
            .build();
    }

    private UserCreateRequest mockCreateRequest() {
        ResidentialDetailCreateRequest residentialDetails = mockResRequest();
        OfficialDetailCreateRequest officialDetails =
            OfficialDetailCreateRequest.builder()
                .employeeCode("123C")
                .address("12 Avenue")
                .city("Gurugram")
                .state("Haryana")
                .country("India")
                .companyContactNo("+91 0987654321")
                .companyContactEmail("company@gmail.com")
                .companyName("Company123")
                .build();
        UserDetailCreateRequest userDetails = UserDetailCreateRequest.builder()
            .firstName("John")
            .lastName("Doe")
            .age(26)
            .gender(Gender.MALE)
            .maritalStatus(MaritalStatus.SINGLE)
            .residentialDetails(residentialDetails)
            .officialDetails(officialDetails)
            .build();

        return UserCreateRequest.builder()
            .email("test@example.com")
            .password("password123")
            .active(true)
            .userDetails(userDetails)
            .build();
    }

    @Test
    void updateUser_ShouldReturnUpdatedDTO_WhenUserExists() {
        // 1. Arrange
        Long existingId = 1L;
        UserUpdateRequest updateReq = createUpdateReq(
            existingId,
            "new-email@josh.com"
        );

        User updatedUser = new User();
        updatedUser.setId(existingId);
        updatedUser.setEmail("new-email@josh.com");

        UserDetail updatedDetail = new UserDetail();
        ResidentialDetail updatedRes = new ResidentialDetail();
        OfficialDetail updatedOff = new OfficialDetail();

        // Mocking Logic
        when(userRepository.findById(existingId)).thenReturn(
            Optional.of(new User())
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
        UserDetailDTO detailDTO = UserDetailDTO.builder()
            .userId(existingId)
            .build();
        UserDTO finalDTO = UserDTO.builder()
            .id(existingId)
            .email("new-email@josh.com")
            .userDetails(detailDTO)
            .build();

        when(userDetailMapper.toDto(any(), any(), any())).thenReturn(detailDTO);
        when(userMapper.toDto(any(), any())).thenReturn(finalDTO);

        // 2. Act
        UserDTO result = userService.updateUser(updateReq);

        // 3. Assert
        assertEquals("new-email@josh.com", result.email());
        verify(userRepository).save(updatedUser);
        verify(userDetailRepository).save(updatedDetail);
        verify(residentialDetailRepository).save(updatedRes);
        verify(officialDetailRepository).save(updatedOff);
    }

    @Test
    void updateUser_ShouldThrowException_WhenUserNotFound() {
        UserUpdateRequest req = createUpdateReq(999L, "ghost@test.com");
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(UserDoesNotExistException.class, () ->
            userService.updateUser(req)
        );
        verify(userRepository, never()).save(any());
    }

    private UserUpdateRequest createUpdateReq(Long id, String email) {
        ResidentialDetailDTO residentialDetails = ResidentialDetailDTO.builder()
            .userId(id)
            .address("Updated Address")
            .city("Updated City")
            .state("Updated State")
            .country("Updated Country")
            .contactNo1("+91 9876543210")
            .contactNo2("+91 9876543211")
            .build();

        OfficialDetailDTO officialDetails = OfficialDetailDTO.builder()
            .userId(id)
            .employeeCode("UPD123")
            .address("Updated Office Address")
            .city("Updated Office City")
            .state("Updated Office State")
            .country("Updated Office Country")
            .companyContactNo("+91 8765432109")
            .companyContactEmail("updated@company.com")
            .companyName("Updated Company")
            .build();

        UserDetailDTO userDetails = UserDetailDTO.builder()
            .userId(id)
            .firstName("UpdatedFirst")
            .lastName("UpdatedLast")
            .age(30)
            .gender(Gender.FEMALE)
            .maritalStatus(MaritalStatus.MARRIED)
            .residentialDetails(residentialDetails)
            .officialDetails(officialDetails)
            .build();

        return UserUpdateRequest.builder()
            .id(id)
            .email(email)
            .password("updatedPassword")
            .active(true)
            .lastModified(Instant.now())
            .userDetails(userDetails)
            .build();
    }
}
