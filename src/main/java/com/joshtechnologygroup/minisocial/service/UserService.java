package com.joshtechnologygroup.minisocial.service;

import com.joshtechnologygroup.minisocial.bean.OfficialDetail;
import com.joshtechnologygroup.minisocial.bean.ResidentialDetail;
import com.joshtechnologygroup.minisocial.bean.User;
import com.joshtechnologygroup.minisocial.bean.UserDetail;
import com.joshtechnologygroup.minisocial.dao.OfficialDetailRepository;
import com.joshtechnologygroup.minisocial.dao.ResidentialDetailRepository;
import com.joshtechnologygroup.minisocial.dao.UserDetailRepository;
import com.joshtechnologygroup.minisocial.dao.UserRepository;
import com.joshtechnologygroup.minisocial.dto.UpdatePasswordRequest;
import com.joshtechnologygroup.minisocial.dto.officialDetail.OfficialDetailMapper;
import com.joshtechnologygroup.minisocial.dto.residentialDetail.ResidentialDetailMapper;
import com.joshtechnologygroup.minisocial.dto.user.PopulatedUser;
import com.joshtechnologygroup.minisocial.dto.user.UserCreateRequest;
import com.joshtechnologygroup.minisocial.dto.user.UserDTO;
import com.joshtechnologygroup.minisocial.dto.user.UserMapper;
import com.joshtechnologygroup.minisocial.dto.userDetail.UserDetailDTO;
import com.joshtechnologygroup.minisocial.dto.userDetail.UserDetailMapper;
import com.joshtechnologygroup.minisocial.exception.InvalidUserCredentialsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

    public UserService(UserRepository userRepository, AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder, UserDetailRepository userDetailRepository, ResidentialDetailRepository residentialDetailRepository, OfficialDetailRepository officialDetailRepository, UserMapper userMapper, UserDetailMapper userDetailMapper, ResidentialDetailMapper residentialDetailMapper, OfficialDetailMapper officialDetailMapper) {
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

    public UserDTO createUser(UserCreateRequest req) {
        User user = userMapper.toUser(req);
        userRepository.save(user);

        UserDetail userDetail = userDetailMapper.toUserDetail(req.userDetails());
        userDetail.setUser(user);
        userDetailRepository.save(userDetail);

        ResidentialDetail residentialDetail = residentialDetailMapper.toResidentialDetail(req.userDetails()
                .residentialDetails());
        residentialDetail.setUser(user);
        residentialDetailRepository.save(residentialDetail);

        OfficialDetail officialDetail = officialDetailMapper.toOfficialDetail(req.userDetails()
                .officialDetails());
        officialDetail.setUser(user);
        officialDetailRepository.save(officialDetail);

        UserDetailDTO detailDTO = userDetailMapper.toDto(userDetail, residentialDetail, officialDetail);

        log.info("New user created with ID {}: {}", user.getId(), user.getEmail());
        return userMapper.toDto(user, detailDTO);
    }

    public Optional<UserDTO> getUser(Long id) {
        Optional<PopulatedUser> userWrapper = userRepository.findUserPopulated(id);
        if (userWrapper.isEmpty()) return Optional.empty();
        PopulatedUser user = userWrapper.get();

        UserDetailDTO detailDTO = userDetailMapper.toDto(user.userDetail(), user.residentialDetail(), user.officialDetail());
        return Optional.of(userMapper.toDto(user.user(), detailDTO));
    }
}
