package com.joshtechnologygroup.minisocial.tool.bean;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class UserFollowingDetailRow {
    @NotBlank(message = "User email is required")
    @Email(message = "Invalid user email format")
    private String userEmail;

    @NotEmpty(message = "Following list cannot be empty")
    private List<String> following;
}
