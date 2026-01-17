package com.joshtechnologygroup.minisocial.service;

import com.joshtechnologygroup.minisocial.bean.User;
import com.joshtechnologygroup.minisocial.dto.user.*;
import com.joshtechnologygroup.minisocial.exception.UserDoesNotExistException;
import com.joshtechnologygroup.minisocial.exception.ValueConflictException;
import com.joshtechnologygroup.minisocial.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        if (
                userRepository.findByEmail(req.email())
                        .isPresent()
        ) throw new ValueConflictException("Validation Error");

        User user = userMapper.createDtoToUser(req);
        user.setPassword(passwordEncoder.encode(req.password()));

        userRepository.saveAndFlush(user);

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

    public UserDTO getUserByEmail(String email) {
        Optional<User> userWrapper = userRepository.findByEmail(email);
        if (userWrapper.isEmpty()) throw new UserDoesNotExistException();

        return userMapper.toDto(userWrapper.get());
    }

    public Page<ActiveUserDTO> getActiveUsers(Pageable pageable) {
        return userRepository.findActiveUsers(pageable);
    }

    @Transactional
    public UserDTO updateUser(UserUpdateRequest req, String userEmail) {
        User existingUser = userRepository.findByEmail(userEmail)
                .orElseThrow(UserDoesNotExistException::new);

        userMapper.updateUserFromDto(req, existingUser);
        userRepository.saveAndFlush(existingUser);

        log.info("User updated with ID {}: {}", existingUser.getId(), existingUser.getEmail());
        return userMapper.toDto(existingUser);
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
