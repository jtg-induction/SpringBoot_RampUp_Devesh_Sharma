package com.joshtechnologygroup.minisocial.dto.userDetail;

import com.joshtechnologygroup.minisocial.dto.officialDetail.OfficialDetailCreateRequest;
import com.joshtechnologygroup.minisocial.dto.residentialDetail.ResidentialDetailCreateRequest;

public record UserDetailCreateRequest(
        String email,
        String password,
        boolean active,

        ResidentialDetailCreateRequest residentialDetails,
        OfficialDetailCreateRequest officialDetails
) {
}
