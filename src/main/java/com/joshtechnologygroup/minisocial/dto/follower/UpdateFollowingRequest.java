package com.joshtechnologygroup.minisocial.dto.follower;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record UpdateFollowingRequest (
        @NotNull
        List<
                @NotNull(message = "ID cannot be null")
                @PositiveOrZero(message = "ID must be unsigned") Long
                > userIds
) {}
