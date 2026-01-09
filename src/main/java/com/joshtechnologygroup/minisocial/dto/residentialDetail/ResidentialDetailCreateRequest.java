package com.joshtechnologygroup.minisocial.dto.residentialDetail;

public record ResidentialDetailCreateRequest(
        String address,
        String city,
        String state,
        String country,
        String contactNo1,
        String contactNo2
) {
}
