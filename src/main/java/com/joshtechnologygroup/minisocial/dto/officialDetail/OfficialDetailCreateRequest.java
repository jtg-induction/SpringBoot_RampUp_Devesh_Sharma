package com.joshtechnologygroup.minisocial.dto.officialDetail;

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
