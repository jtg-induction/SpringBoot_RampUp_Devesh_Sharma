package com.joshtechnologygroup.minisocial.factory;

import com.joshtechnologygroup.minisocial.TestDataConfig;
import com.joshtechnologygroup.minisocial.bean.User;
import com.joshtechnologygroup.minisocial.dto.user.UserCreateRequest;
import com.joshtechnologygroup.minisocial.dto.user.UserDTO;
import com.joshtechnologygroup.minisocial.dto.user.UserUpdateRequest;
import net.datafaker.Faker;

import java.time.Instant;
import java.util.HashSet;

public class UserFactory {
    private static final Faker FAKER = TestDataConfig.FAKER;

    public static User defaultUser() {
        User user = new User();
        user.setId((long) FAKER.number()
                .positive());
        user.setEmail(FAKER.internet()
                .emailAddress());
        user.setPassword(FAKER.credentials()
                .password());
        user.setActive(FAKER.bool()
                .bool());
        user.setCreatedAt(Instant.now());
        user.setLastModified(Instant.now());
        user.setFollowers(new HashSet<>());
        user.setFollowed(new HashSet<>());

        return user;
    }

    public static UserCreateRequest.UserCreateRequestBuilder defaultUserCreateRequest() {
        User user = defaultUser();
        return defaultUserCreateRequest(user);
    }

    public static UserCreateRequest.UserCreateRequestBuilder defaultUserCreateRequest(User user) {
        return UserCreateRequest.builder()
                .email(user.getEmail())
                .password(user.getPassword())
                .active(user.getActive())
                .userDetails(UserDetailFactory.defaultUserDetailCreateRequest()
                        .build());
    }

    public static UserDTO.UserDTOBuilder defaultUserDTO() {
        User user = defaultUser();
        return defaultUserDTO(user);
    }

    public static UserDTO.UserDTOBuilder defaultUserDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .active(user.getActive())
                .lastModified(user.getLastModified())
                .userDetails(UserDetailFactory.defaultUserDetailDTO(user.getId())
                        .build());
    }

    public static UserUpdateRequest.UserUpdateRequestBuilder defaultUserUpdateRequest() {
        User user = defaultUser();
        return defaultUserUpdateRequest(user);
    }

    public static UserUpdateRequest.UserUpdateRequestBuilder defaultUserUpdateRequest(User user) {
        return UserUpdateRequest.builder()
                .id(user.getId())
                .email(user.getEmail())
                .password(user.getPassword())
                .active(user.getActive())
                .lastModified(user.getLastModified())
                .userDetails(UserDetailFactory.defaultUserDetailDTO(user.getId())
                        .build());
    }
}
