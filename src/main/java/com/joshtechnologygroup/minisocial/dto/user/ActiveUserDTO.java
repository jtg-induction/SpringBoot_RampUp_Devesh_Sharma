package com.joshtechnologygroup.minisocial.dto.user;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;

@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record ActiveUserDTO(
        @NotNull(message = "ID is required")
        @PositiveOrZero(message = "ID must be unsigned")
        Long userId,

        @Email(message = "Invalid Email format")
        @NotBlank(message = "Email is required")
        String email
) {
}
