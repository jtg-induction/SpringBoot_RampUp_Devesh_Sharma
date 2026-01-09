package com.joshtechnologygroup.minisocial.dto.user;

import com.joshtechnologygroup.minisocial.dto.userDetail.UserDetailDTO;

import java.time.Instant;

public record UserUpdateRequest(
        Long id,
        String email,
        String password,
        boolean active,
        Instant lastModified,

        UserDetailDTO userDetails
) {
}
