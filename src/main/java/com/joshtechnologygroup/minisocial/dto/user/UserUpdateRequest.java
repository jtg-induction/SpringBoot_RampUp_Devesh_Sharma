package com.joshtechnologygroup.minisocial.dto.user;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.joshtechnologygroup.minisocial.dto.userDetail.UserDetailDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Builder;

@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record UserUpdateRequest(
        @Email(message = "Invalid Email format")
        @NotNull(message = "Email is required")
        @Size(max = 255, message = "Email must not exceed 255 characters")
        String email,

        @Size(min = 8, max = 255, message = "Password must be between 8 and 255 characters long")
        @NotBlank(message = "Password is required")
        String password,

        @NotNull(message = "active is required")
        Boolean active,

        @NotNull(message = "User Details are required")
        @Valid UserDetailDTO userDetails
) {
}
