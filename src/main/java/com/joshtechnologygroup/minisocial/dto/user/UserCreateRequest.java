package com.joshtechnologygroup.minisocial.dto.user;

import com.joshtechnologygroup.minisocial.dto.userDetail.UserDetailCreateRequest;
import lombok.Builder;

@Builder
public record UserCreateRequest(
        String email,
        String password,
        boolean active,
        UserDetailCreateRequest userDetails
) {
}
