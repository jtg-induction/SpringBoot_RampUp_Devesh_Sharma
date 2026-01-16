package com.joshtechnologygroup.minisocial.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import static com.joshtechnologygroup.minisocial.constants.ValidationConstants.STRONG_PASSWORD_REGEX;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record UpdatePasswordRequest(
        @NotBlank(message = "Old Password is required")
        @Size(min = 8, max = 255, message = "Password must be between 8 and 255 characters long")
        String oldPassword,

        @NotBlank(message = "New Password is required")
        @Size(min = 8, max = 255, message = "Password must be between 8 and 255 characters long")
        @Pattern(regexp = STRONG_PASSWORD_REGEX,message = "New Password must have at least 1 number, 1 special character and 1 uppercase and lowercase character")
        String newPassword
) {
    @AssertTrue(message = "New Password must be different from Old Password")
    private boolean isNewPasswordDifferent() {
        return !newPassword.equals(oldPassword);
    }
}
