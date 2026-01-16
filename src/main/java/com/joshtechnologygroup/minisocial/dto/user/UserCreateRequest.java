package com.joshtechnologygroup.minisocial.dto.user;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.joshtechnologygroup.minisocial.dto.userDetail.UserDetailCreateRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Builder;

import static com.joshtechnologygroup.minisocial.constants.ValidationConstants.STRONG_PASSWORD_REGEX;

@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record UserCreateRequest(
        @Email(message = "Invalid Email format")
        @NotBlank(message = "Email is required")
        @Size(max = 255, message = "Email must not exceed 255 characters")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 30, message = "Password must be between 8 and 30 characters long")
        @Pattern(regexp = STRONG_PASSWORD_REGEX, message = "Password must contain 1 Uppercase and Lowercase Letter, 1 number and 1 special character")
        String password,

        @NotNull(message = "User Details are required")
        @Valid UserDetailCreateRequest userDetails
) {
}
