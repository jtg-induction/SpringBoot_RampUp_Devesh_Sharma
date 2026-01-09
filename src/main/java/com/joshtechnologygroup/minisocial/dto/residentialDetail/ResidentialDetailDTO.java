package com.joshtechnologygroup.minisocial.dto.residentialDetail;

public record ResidentialDetailDTO(
        Long userId,
        String address,
        String city,
        String state,
        String country,
        String contactNo1,
        String contactNo2
) {
}
