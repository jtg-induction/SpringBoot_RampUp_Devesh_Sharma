package com.joshtechnologygroup.minisocial.service;

import com.joshtechnologygroup.minisocial.bean.User;
import com.joshtechnologygroup.minisocial.dto.user.*;
import com.joshtechnologygroup.minisocial.exception.UserDoesNotExistException;
import com.joshtechnologygroup.minisocial.exception.ValueConflictException;
import com.joshtechnologygroup.minisocial.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            UserMapper userMapper
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    @Transactional
    public UserDTO createUser(UserCreateRequest req) {
        if (userRepository.findByEmail(req.email())
                .isPresent())
            throw new ValueConflictException("Email already in use");

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

    public List<ActiveUserDTO> getActiveUsers() {
        return userRepository.findActiveUsers();
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
