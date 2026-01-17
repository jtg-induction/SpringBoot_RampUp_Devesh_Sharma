package com.joshtechnologygroup.minisocial.dto.user;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.joshtechnologygroup.minisocial.dto.userDetail.UserDetailDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Builder;

import java.time.Instant;

@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record UserDTO(
        @NotNull(message = "ID is required")
        @PositiveOrZero(message = "ID must be unsigned")
        Long id,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid Email format")
        String email,

        @NotNull
        Instant lastModified,

        @NotNull
        Instant createdAt,

        @NotNull(message = "User Details are required")
        @Valid UserDetailDTO userDetails
) {
}
