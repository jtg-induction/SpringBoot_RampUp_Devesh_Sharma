package com.joshtechnologygroup.minisocial.dto.user;

import com.joshtechnologygroup.minisocial.dto.userDetail.UserDetailDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Builder;

import java.time.Instant;

@Builder
public record UserUpdateRequest(
        @PositiveOrZero(message = "ID must be unsigned")
        @NotNull(message = "ID is required")
        Long id,

        @Email(message = "Invalid Email format")
        @NotNull(message = "Email is required")
        @Size(max = 255, message = "Email must not exceed 255 characters")
        String email,

        @Size(min = 8, max = 255, message = "Password must be between 8 and 255 characters long")
        @NotBlank(message = "Password is required")
        String password,

        @NotNull(message = "active is required")
        Boolean active,

        @NotNull(message = "lastModified is required")
        @PastOrPresent(message = "lastModified should not be in future")
        Instant lastModified,

        @NotNull(message = "User Details are required")
        @Valid UserDetailDTO userDetails
) {
}
