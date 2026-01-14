package com.joshtechnologygroup.minisocial.service;

import com.joshtechnologygroup.minisocial.bean.OfficialDetail;
import com.joshtechnologygroup.minisocial.bean.ResidentialDetail;
import com.joshtechnologygroup.minisocial.bean.User;
import com.joshtechnologygroup.minisocial.bean.UserDetail;
import com.joshtechnologygroup.minisocial.dto.UpdatePasswordRequest;
import com.joshtechnologygroup.minisocial.dto.officialDetail.OfficialDetailMapper;
import com.joshtechnologygroup.minisocial.dto.residentialDetail.ResidentialDetailMapper;
import com.joshtechnologygroup.minisocial.dto.user.*;
import com.joshtechnologygroup.minisocial.dto.userDetail.UserDetailDTO;
import com.joshtechnologygroup.minisocial.dto.userDetail.UserDetailMapper;
import com.joshtechnologygroup.minisocial.exception.InvalidUserCredentialsException;
import com.joshtechnologygroup.minisocial.exception.UnauthorizedException;
import com.joshtechnologygroup.minisocial.exception.UserDoesNotExistException;
import com.joshtechnologygroup.minisocial.repository.OfficialDetailRepository;
import com.joshtechnologygroup.minisocial.repository.ResidentialDetailRepository;
import com.joshtechnologygroup.minisocial.repository.UserDetailRepository;
import com.joshtechnologygroup.minisocial.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailRepository userDetailRepository;
    private final ResidentialDetailRepository residentialDetailRepository;
    private final OfficialDetailRepository officialDetailRepository;
    private final UserMapper userMapper;
    private final UserDetailMapper userDetailMapper;
    private final ResidentialDetailMapper residentialDetailMapper;
    private final OfficialDetailMapper officialDetailMapper;

    public UserService(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder,
            UserDetailRepository userDetailRepository,
            ResidentialDetailRepository residentialDetailRepository,
            OfficialDetailRepository officialDetailRepository,
            UserMapper userMapper,
            UserDetailMapper userDetailMapper,
            ResidentialDetailMapper residentialDetailMapper,
            OfficialDetailMapper officialDetailMapper
    ) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.userDetailRepository = userDetailRepository;
        this.residentialDetailRepository = residentialDetailRepository;
        this.officialDetailRepository = officialDetailRepository;
        this.userMapper = userMapper;
        this.userDetailMapper = userDetailMapper;
        this.residentialDetailMapper = residentialDetailMapper;
        this.officialDetailMapper = officialDetailMapper;
    }

    public void updateUserPassword(UpdatePasswordRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.oldPassword()));

        Optional<User> user = userRepository.findByEmail(request.email());
        if (user.isEmpty()) throw new InvalidUserCredentialsException();

        user.get()
                .setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user.get());
        log.info("Successfully Updated password for user {}", request.email());
    }

    @Transactional
    public UserDTO createUser(UserCreateRequest req) {
        User user = userMapper.createDtoToUser(req);
        user.setPassword(passwordEncoder.encode(req.password()));
        UserDetail userDetail = userDetailMapper.toUserDetail(
                req.userDetails()
        );
        OfficialDetail officialDetail = officialDetailMapper.toOfficialDetail(
                req.userDetails()
                        .officialDetails()
        );
        ResidentialDetail residentialDetail =
                residentialDetailMapper.toResidentialDetail(
                        req.userDetails()
                                .residentialDetails()
                );
        userDetail.setUser(user);
        officialDetail.setUser(user);
        residentialDetail.setUser(user);
        user.setUserDetail(userDetail);
        user.setOfficialDetail(officialDetail);
        user.setResidentialDetail(residentialDetail);

        userRepository.save(user);

        UserDetailDTO detailDTO = userDetailMapper.toDto(
                userDetail,
                residentialDetail,
                officialDetail
        );

        log.info(
                "New user created with ID {}: {}",
                user.getId(),
                user.getEmail()
        );
        return userMapper.toDto(user, detailDTO);
    }

    public Optional<UserDTO> getUser(Long id) {
        Optional<User> userWrapper = userRepository.findById(id);
        if (userWrapper.isEmpty()) return Optional.empty();
        User user = userWrapper.get();

        UserDetailDTO detailDTO = userDetailMapper.toDto(
                user.getUserDetail(),
                user.getResidentialDetail(),
                user.getOfficialDetail()
        );
        return Optional.of(userMapper.toDto(user, detailDTO));
    }

    public List<ActiveUserDTO> getActiveUsers() {
        return userRepository.findActiveUsers();
    }

    @Transactional
    public UserDTO updateUser(UserUpdateRequest req, String userEmail, Long id) {
        Optional<User> existingUser = userRepository.findById(id);
        if (existingUser.isEmpty())
            throw new UserDoesNotExistException();
        if (!existingUser.get()
                .getEmail()
                .equals(userEmail)) {
            throw new UnauthorizedException("Attempted to modify another user");
        }

        User user = userMapper.updateDtoToUser(req);
        user.setId(id);
        userRepository.save(user);

        UserDetail userDetail = userDetailMapper.dtoToUserDetail(
                req.userDetails()
        );
        userDetail.setUser(user);
        userDetailRepository.save(userDetail);

        ResidentialDetail residentialDetail =
                residentialDetailMapper.dtoToResidentialDetail(
                        req.userDetails()
                                .residentialDetails()
                );
        residentialDetail.setUser(user);
        residentialDetailRepository.save(residentialDetail);

        OfficialDetail officialDetail =
                officialDetailMapper.dtoToOfficialDetail(
                        req.userDetails()
                                .officialDetails()
                );
        officialDetail.setUser(user);
        officialDetailRepository.save(officialDetail);

        UserDetailDTO detailDTO = userDetailMapper.toDto(
                userDetail,
                residentialDetail,
                officialDetail
        );

        log.info("User updated with ID {}: {}", user.getId(), user.getEmail());
        return userMapper.toDto(user, detailDTO);
    }

    @Transactional
    public UserDTO deleteUser(Long id) {
        // Get user data
        Optional<User> userWrapper = userRepository.findById(id);
        if (userWrapper.isEmpty()) {
            throw new UserDoesNotExistException();
        }

        User user = userWrapper.get();

        // Create the DTO
        UserDetailDTO detailDTO = userDetailMapper.toDto(
                user.getUserDetail(),
                user.getResidentialDetail(),
                user.getOfficialDetail()
        );
        UserDTO userDTO = userMapper.toDto(user, detailDTO);

        userRepository.deleteById(id);

        log.info("User deleted with ID {}: {}", user.getId(), user.getEmail());
        return userDTO;
    }
}
