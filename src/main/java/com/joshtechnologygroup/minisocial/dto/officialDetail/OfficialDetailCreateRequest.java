package com.joshtechnologygroup.minisocial.dto.officialDetail;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import static com.joshtechnologygroup.minisocial.constants.ValidationConstants.PHONE_NUMBER_REGEX;

@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record OfficialDetailCreateRequest(
        @Size(min = 3, max = 100, message = "Employee Code must be between 3 and 100 characters long")
        @NotBlank(message = "Employee Code is required")
        String employeeCode,

        @Size(max = 255, message = "Address must not exceed 255 characters")
        @NotBlank(message = "Address is required")
        String address,

        @Size(max = 100, message = "City must not exceed 100 characters")
        @NotBlank(message = "City is required")
        String city,

        @Size(max = 100, message = "State must not exceed 100 characters")
        @NotBlank(message = "State is required")
        String state,

        @Size(max = 100, message = "Country must not exceed 100 characters")
        @NotBlank(message = "Country is required")
        String country,

        @Size(min = 13, max = 13, message = "Company Contact must be 14 characters long")
        @Pattern(regexp = PHONE_NUMBER_REGEX, message = "Contact number must be in the format '+CCXXXXXXXXXX' where CC is country code")
        @NotBlank(message = "Company Contact Number is required")
        String companyContactNo,

        @Size(max = 255, message = "Company Contact Email must not exceed 255 characters")
        @NotBlank(message = "Company Contact Email is required")
        @Email
        String companyContactEmail,

        @Size(max = 255, message = "Company Name must not exceed 255 characters")
        @NotBlank(message = "Company Name is required")
        String companyName
) {
}
