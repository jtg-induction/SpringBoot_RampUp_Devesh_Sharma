package com.joshtechnologygroup.minisocial.dto.user;

import com.joshtechnologygroup.minisocial.dto.userDetail.UserDetailDTO;
import lombok.Builder;

import java.time.Instant;

@Builder
public record UserDTO(
        Long id,
        String email,
        boolean active,
        Instant last_modified,

        UserDetailDTO userDetails
) {
}
