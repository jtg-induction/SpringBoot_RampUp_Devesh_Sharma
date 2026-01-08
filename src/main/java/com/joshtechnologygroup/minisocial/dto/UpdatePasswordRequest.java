package com.joshtechnologygroup.minisocial.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePasswordRequest {
    private String email;
    private String oldPassword;
    private String newPassword;
}
