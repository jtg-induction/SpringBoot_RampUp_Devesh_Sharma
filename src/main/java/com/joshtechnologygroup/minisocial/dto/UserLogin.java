package com.joshtechnologygroup.minisocial.dto;

<<<<<<< HEAD
public record UserLogin(
        String email,
        String password
) {
=======
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLogin {
    private String email;
    private String password;
>>>>>>> 0b4eb02 (feat: Authentication)
}
