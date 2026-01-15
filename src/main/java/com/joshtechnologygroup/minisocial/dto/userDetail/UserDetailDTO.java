package com.joshtechnologygroup.minisocial.dto.userDetail;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.joshtechnologygroup.minisocial.dto.user.UserUpdateRequest;
import com.joshtechnologygroup.minisocial.enums.Gender;
import com.joshtechnologygroup.minisocial.enums.MaritalStatus;
import com.joshtechnologygroup.minisocial.dto.officialDetail.OfficialDetailDTO;
import com.joshtechnologygroup.minisocial.dto.residentialDetail.ResidentialDetailDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Builder;

@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record UserDetailDTO(
        @Size(max = 100, message = "First name must not exceed 100 characters")
        @NotBlank(message = "First name is required", groups = UserUpdateRequest.Put.class)
        String firstName,

        @Size(max = 100, message = "Last name must not exceed 100 characters")
        String lastName,

        @Positive(message = "Age must be a positive integer")
        @Max(value = 150, message = "Age must not exceed 150")
        @NotNull(message = "Age is required", groups = UserUpdateRequest.Put.class)
        Integer age,

        @NotNull(message = "Gender is required", groups = UserUpdateRequest.Put.class)
        Gender gender,

        @NotNull(message = "Marital status is required", groups = UserUpdateRequest.Put.class)
        MaritalStatus maritalStatus,

        @NotNull(message = "Residential Details are required", groups = UserUpdateRequest.Put.class)
        @Valid ResidentialDetailDTO residentialDetails,

        @NotNull(message = "Official Details are required", groups = UserUpdateRequest.Put.class)
        @Valid OfficialDetailDTO officialDetails
) {
}
