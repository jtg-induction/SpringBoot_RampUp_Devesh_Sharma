package com.joshtechnologygroup.minisocial.dto.residentialDetail;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import static com.joshtechnologygroup.minisocial.constants.ValidationConstants.PHONE_NUMBER_REGEX;

@Builder
public record ResidentialDetailCreateRequest(
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

        @Size(min = 13, max = 13, message = "Contact Number 1 must be 14 characters long")
        @Pattern(regexp = PHONE_NUMBER_REGEX, message = "Contact number must be in the format '+CCXXXXXXXXXX' where CC is country code")
        @NotBlank(message = "Contact Number 1 is required")
        String contactNo1,

        @Size(min = 13, max = 13, message = "Contact Number 2 must be 14 characters long")
        @Pattern(regexp = PHONE_NUMBER_REGEX, message = "Contact number must be in the format '+CCXXXXXXXXXX' where CC is country code")
        String contactNo2
) {
}
