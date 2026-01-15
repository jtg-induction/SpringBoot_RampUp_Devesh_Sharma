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
        @NotNull(message = "Email is required", groups = {Put.class})
        @Size(max = 255, message = "Email must not exceed 255 characters")
        String email,

        @NotNull(message = "active is required", groups = {Put.class})
        Boolean active,

        @NotNull(message = "User Details are required", groups = {Put.class})
        @Valid UserDetailDTO userDetails
) {
        public interface Put {};
}
