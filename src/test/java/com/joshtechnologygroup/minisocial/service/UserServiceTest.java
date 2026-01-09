package com.joshtechnologygroup.minisocial.service;

import com.joshtechnologygroup.minisocial.bean.*;
import com.joshtechnologygroup.minisocial.dao.OfficialDetailRepository;
import com.joshtechnologygroup.minisocial.dao.ResidentialDetailRepository;
import com.joshtechnologygroup.minisocial.dao.UserDetailRepository;
import com.joshtechnologygroup.minisocial.dao.UserRepository;
import com.joshtechnologygroup.minisocial.dto.officialDetail.OfficialDetailCreateRequest;
import com.joshtechnologygroup.minisocial.dto.officialDetail.OfficialDetailMapper;
import com.joshtechnologygroup.minisocial.dto.residentialDetail.ResidentialDetailCreateRequest;
import com.joshtechnologygroup.minisocial.dto.residentialDetail.ResidentialDetailMapper;
import com.joshtechnologygroup.minisocial.dto.user.UserCreateRequest;
import com.joshtechnologygroup.minisocial.dto.user.UserDTO;
import com.joshtechnologygroup.minisocial.dto.user.UserMapper;
import com.joshtechnologygroup.minisocial.dto.userDetail.UserDetailCreateRequest;
import com.joshtechnologygroup.minisocial.dto.userDetail.UserDetailDTO;
import com.joshtechnologygroup.minisocial.dto.userDetail.UserDetailMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

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

    @InjectMocks
    private UserService userService;

    @Test
    void createUser_ShouldReturnFullUserDTO_WhenInputIsValid() {
        // Prepare Request
        UserCreateRequest request = createMockRequest();
        User user = new User();
        user.setId(1L);
        user.setEmail("test@josh.com");
        UserDetail userDetail = new UserDetail();
        ResidentialDetail resDetail = new ResidentialDetail();
        OfficialDetail offDetail = new OfficialDetail();

        UserDetailDTO detailDTO = UserDetailDTO.builder()
                .userId(1L)
                .build();
        UserDTO expectedResponse = UserDTO.builder()
                .id(1L)
                .email("test@josh.com")
                .userDetails(detailDTO)
                .build();

        // Mock Mapper behaviors
        when(userMapper.toUser(any())).thenReturn(user);
        when(userDetailMapper.toUserDetail(any())).thenReturn(userDetail);
        when(residentialDetailMapper.toResidentialDetail(any())).thenReturn(resDetail);
        when(officialDetailMapper.toOfficialDetail(any())).thenReturn(offDetail);

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
        UserCreateRequest request = createMockRequest();
        when(userMapper.toUser(any())).thenReturn(new User());

        when(userRepository.save(any())).thenThrow(new DataIntegrityViolationException("Email exists"));

        assertThrows(DataIntegrityViolationException.class, () -> userService.createUser(request));

        verifyNoInteractions(userDetailRepository);
        verifyNoInteractions(residentialDetailRepository);
    }

    @Test
    void createUser_ShouldHandleNullNestedDetails() {
        // Request with null officialDetails
        UserCreateRequest request = new UserCreateRequest("test@josh.com", "pass", true,
                new UserDetailCreateRequest("John", "Doe", 25, Gender.MALE, MaritalStatus.SINGLE,
                        mockResRequest(), null)); // null OfficialDetails

        when(userMapper.toUser(any())).thenReturn(new User());
        when(userRepository.save(any())).thenReturn(new User());

        assertThrows(NullPointerException.class, () -> userService.createUser(request));
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

    private UserCreateRequest createMockRequest() {
        ResidentialDetailCreateRequest residentialDetails = mockResRequest();
        OfficialDetailCreateRequest officialDetails = OfficialDetailCreateRequest.builder()
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
}
