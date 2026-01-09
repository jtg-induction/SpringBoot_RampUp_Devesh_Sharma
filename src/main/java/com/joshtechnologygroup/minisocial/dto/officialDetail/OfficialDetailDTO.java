package com.joshtechnologygroup.minisocial.dto.officialDetail;

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
