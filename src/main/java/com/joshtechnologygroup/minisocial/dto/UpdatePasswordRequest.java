package com.joshtechnologygroup.minisocial.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdatePasswordRequest(
        @Email(message = "Invalid Email format")
        @NotBlank(message = "Email is required")
        @Size(max = 255, message = "Email must not exceed 255 characters")
        String email,

        @NotBlank(message = "Old Password is required")
        @Size(min = 8, max = 255, message = "Password must be between 8 and 255 characters long")
        String oldPassword,

        @NotBlank(message = "New Password is required")
        @Size(min = 8, max = 255, message = "Password must be between 8 and 255 characters long")
        String newPassword
) {
}
