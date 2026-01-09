package com.joshtechnologygroup.minisocial.dto.user;

import com.joshtechnologygroup.minisocial.dto.userDetail.UserDetailDTO;
import lombok.Builder;

import java.time.Instant;

@Builder
public record UserUpdateRequest(
        Long id,
        String email,
        String password,
        Boolean active,
        Instant lastModified,

        UserDetailDTO userDetails
) {
}
