package com.joshtechnologygroup.minisocial.dto.userDetail;

import com.joshtechnologygroup.minisocial.bean.Gender;
import com.joshtechnologygroup.minisocial.bean.MaritalStatus;
import com.joshtechnologygroup.minisocial.dto.officialDetail.OfficialDetailCreateRequest;
import com.joshtechnologygroup.minisocial.dto.officialDetail.OfficialDetailDTO;
import com.joshtechnologygroup.minisocial.dto.residentialDetail.ResidentialDetailCreateRequest;
import com.joshtechnologygroup.minisocial.dto.residentialDetail.ResidentialDetailDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Builder;

@Builder
public record UserDetailCreateRequest(
        @Size(max = 100, message = "First name must not exceed 100 characters")
        @NotBlank(message = "First name is required")
        String firstName,

        @Size(max = 100, message = "Last name must not exceed 100 characters")
        String lastName,

        @Positive(message = "Age must be a positive integer")
        @Max(value = 150, message = "Age must not exceed 150")
        @NotNull(message = "Age is required")
        Integer age,

        @NotNull(message = "Gender is required")
        Gender gender,

        @NotNull(message = "Marital status is required")
        MaritalStatus maritalStatus,

        @NotNull(message = "Residential Details are required")
        @Valid ResidentialDetailCreateRequest residentialDetails,

        @NotNull(message = "Official Details are required")
        @Valid OfficialDetailCreateRequest officialDetails
) {
}
