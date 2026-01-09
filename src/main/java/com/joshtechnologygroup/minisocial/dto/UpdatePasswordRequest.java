package com.joshtechnologygroup.minisocial.dto;

public record UpdatePasswordRequest(
        String email,
        String oldPassword,
        String newPassword
) {
}
