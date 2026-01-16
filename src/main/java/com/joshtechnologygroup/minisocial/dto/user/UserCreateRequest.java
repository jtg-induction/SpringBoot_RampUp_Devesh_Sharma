package com.joshtechnologygroup.minisocial.dto.user;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.joshtechnologygroup.minisocial.dto.userDetail.UserDetailCreateRequest;
import com.joshtechnologygroup.minisocial.util.Validations;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Builder;

@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record UserCreateRequest(
        @Email(message = "Invalid Email format")
        @NotBlank(message = "Email is required")
        @Size(max = 255, message = "Email must not exceed 255 characters")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 255, message = "Password must be between 8 and 255 characters long")
        String password,

        @NotNull(message = "active is required")
        Boolean active,

        @NotNull(message = "User Details are required")
        @Valid UserDetailCreateRequest userDetails
) {
        @AssertTrue(message = "Password must contain 1 Uppercase and Lowercase Letter, 1 number and 1 special character")
        private boolean isPasswordIsStrong() {
                return Validations.isPasswordStrong(password);
        }
}
