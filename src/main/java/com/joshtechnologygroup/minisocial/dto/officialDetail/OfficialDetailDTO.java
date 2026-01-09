package com.joshtechnologygroup.minisocial.dto.officialDetail;

import lombok.Builder;

@Builder
public record OfficialDetailDTO(
        Long userId,
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
