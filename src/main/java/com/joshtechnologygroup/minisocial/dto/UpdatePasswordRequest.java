package com.joshtechnologygroup.minisocial.dto;

<<<<<<< HEAD
public record UpdatePasswordRequest(
        String email,
        String oldPassword,
        String newPassword
) {
=======
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePasswordRequest {
    private String email;
    private String oldPassword;
    private String newPassword;
>>>>>>> 0b4eb02 (feat: Authentication)
}
