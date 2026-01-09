package com.joshtechnologygroup.minisocial.dto.officialDetail;

import lombok.Builder;

@Builder
public record OfficialDetailCreateRequest(
        String employeeCode,
        String address,
        String city,
        String state,
        String country,
        String companyContactNo,
        String companyContactEmail,
        String companyName
) {
}
