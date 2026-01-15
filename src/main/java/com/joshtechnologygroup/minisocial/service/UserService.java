package com.joshtechnologygroup.minisocial.service;

import com.joshtechnologygroup.minisocial.bean.User;
import com.joshtechnologygroup.minisocial.dto.UpdatePasswordRequest;
import com.joshtechnologygroup.minisocial.dto.user.*;
import com.joshtechnologygroup.minisocial.exception.InvalidUserCredentialsException;
import com.joshtechnologygroup.minisocial.exception.UnauthorizedException;
import com.joshtechnologygroup.minisocial.exception.UserDoesNotExistException;
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

    public Optional<UserDTO> getUser(Long id) {
        Optional<User> userWrapper = userRepository.findById(id);
        if (userWrapper.isEmpty()) return Optional.empty();
        User user = userWrapper.get();

        return Optional.of(userMapper.toDto(user));
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

        log.info("User updated with ID {}: {}", user.getId(), user.getEmail());
        return userMapper.toDto(user);
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
        UserDTO userDTO = userMapper.toDto(user);

        userRepository.deleteById(id);

        log.info("User deleted with ID {}: {}", user.getId(), user.getEmail());
        return userDTO;
    }
}
