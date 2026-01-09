package com.joshtechnologygroup.minisocial.dto.residentialDetail;

import lombok.Builder;

@Builder
public record ResidentialDetailCreateRequest(
        String address,
        String city,
        String state,
        String country,
        String contactNo1,
        String contactNo2
) {
}
