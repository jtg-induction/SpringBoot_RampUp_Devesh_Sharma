package com.joshtechnologygroup.minisocial.dto.userDetail;

import com.joshtechnologygroup.minisocial.bean.Gender;
import com.joshtechnologygroup.minisocial.bean.MaritalStatus;
import com.joshtechnologygroup.minisocial.dto.officialDetail.OfficialDetailCreateRequest;
import com.joshtechnologygroup.minisocial.dto.residentialDetail.ResidentialDetailCreateRequest;
import lombok.Builder;

@Builder
public record UserDetailCreateRequest(
        String firstName,
        String lastName,
        Integer age,
        Gender gender,
        MaritalStatus maritalStatus,

        ResidentialDetailCreateRequest residentialDetails,
        OfficialDetailCreateRequest officialDetails
) {
}
