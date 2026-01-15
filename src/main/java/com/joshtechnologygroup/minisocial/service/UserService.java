package com.joshtechnologygroup.minisocial.service;

import com.joshtechnologygroup.minisocial.bean.User;
import com.joshtechnologygroup.minisocial.dto.UpdatePasswordRequest;
import com.joshtechnologygroup.minisocial.dto.user.*;
import com.joshtechnologygroup.minisocial.exception.InvalidUserCredentialsException;
import com.joshtechnologygroup.minisocial.exception.UserDoesNotExistException;
import com.joshtechnologygroup.minisocial.repository.UserRepository;
import com.joshtechnologygroup.minisocial.specification.UserSpecificationBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
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
    private final UserMapper userMapper;

    public UserService(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder,
            UserMapper userMapper
    ) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
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

        userRepository.save(user);

        log.info(
                "New user created with ID {}: {}",
                user.getId(),
                user.getEmail()
        );

        return userMapper.toDto(user);
    }

    public UserDTO getUser(Long id) {
        Optional<User> userWrapper = userRepository.findById(id);
        if (userWrapper.isEmpty()) throw new UserDoesNotExistException();

        return userMapper.toDto(userWrapper.get());
    }

    public List<UserDTO> getAllUsers(UserQueryParams userQueryParams) {
        Specification<User> userSpecification = new UserSpecificationBuilder().withMinAge(userQueryParams.minAge())
                .withMaxAge(userQueryParams.maxAge())
                .withMinFollowers(userQueryParams.minFollowerCount())
                .withMaxFollowers(userQueryParams.maxFollowerCount())
                .withMinFollowing(userQueryParams.minFollowingCount())
                .withMaxFollowing(userQueryParams.maxFollowingCount())
                .withFirstName(userQueryParams.firstName())
                .withLastName(userQueryParams.lastName())
                .withGender(userQueryParams.gender())
                .withMaritalStatus(userQueryParams.maritalStatus())
                .residentialCityIn(userQueryParams.residentialCities())
                .officialCityIn(userQueryParams.officialCities())
                .companyNameIn(userQueryParams.companyName())
                .isActive(userQueryParams.active())
                .orderBy(userQueryParams.sortOrders())
                .build();
        List<User> users = userRepository.findAll(userSpecification);
        return users.stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Transactional
    public UserDTO updateUser(UserUpdateRequest req, String userEmail) {
        Optional<User> existingUser = userRepository.findByEmail(userEmail);
        if (existingUser.isEmpty())
            throw new UserDoesNotExistException();

        User user = userMapper.updateDtoToUser(req);
        user.setId(existingUser.get()
                .getId());
        userRepository.save(user);

        log.info("User updated with ID {}: {}", user.getId(), user.getEmail());
        return userMapper.toDto(user);
    }

    @Transactional
    public UserDTO deleteUser(String email) {
        // Get user data
        Optional<User> userWrapper = userRepository.findByEmail(email);
        if (userWrapper.isEmpty()) {
            throw new UserDoesNotExistException();
        }

        User user = userWrapper.get();

        // Create the DTO
        UserDTO userDTO = userMapper.toDto(user);

        userRepository.deleteById(user.getId());

        log.info("User deleted with ID {}: {}", user.getId(), user.getEmail());
        return userDTO;
    }
}
