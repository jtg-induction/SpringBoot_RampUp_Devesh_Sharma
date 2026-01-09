package com.joshtechnologygroup.minisocial.dto.user;

import lombok.Builder;

@Builder
public record ActiveUserDTO(
        Long userId,
        String email
) {
}
