package com.joshtechnologygroup.minisocial.dto.follower;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.List;

public record UpdateFollowingRequest (
        @NotNull
        List<
                @NotNull(message = "ID cannot be null")
                @PositiveOrZero(message = "ID must be unsigned") Long
                > userIds
) {}
