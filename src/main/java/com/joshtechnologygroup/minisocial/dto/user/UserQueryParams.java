package com.joshtechnologygroup.minisocial.dto.user;

import com.joshtechnologygroup.minisocial.enums.Gender;
import com.joshtechnologygroup.minisocial.enums.MaritalStatus;
import com.joshtechnologygroup.minisocial.enums.UserSortOrder;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.List;

public record UserQueryParams(
        // Filtering
        @Positive Integer minAge,
        @Positive Integer maxAge,
        List<String> residentialCities,
        List<String> officialCities,
        List<String> companyName,
        MaritalStatus maritalStatus,
        Gender gender,
        String firstName,
        String lastName,
        @PositiveOrZero Integer minFollowingCount,
        @PositiveOrZero Integer maxFollowingCount,
        @PositiveOrZero Integer minFollowerCount,
        @PositiveOrZero Integer maxFollowerCount,
        Boolean active,

        // Sorting
        List<UserSortOrder> sortOrders) {
}
