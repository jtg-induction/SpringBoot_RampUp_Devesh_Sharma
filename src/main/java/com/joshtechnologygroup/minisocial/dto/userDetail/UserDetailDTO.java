package com.joshtechnologygroup.minisocial.dto.userDetail;

import com.joshtechnologygroup.minisocial.bean.Gender;
import com.joshtechnologygroup.minisocial.bean.MaritalStatus;
import com.joshtechnologygroup.minisocial.dto.officialDetail.OfficialDetailDTO;
import com.joshtechnologygroup.minisocial.dto.residentialDetail.ResidentialDetailDTO;
import lombok.Builder;

@Builder
public record UserDetailDTO(
        Long userId,
        String firstName,
        String lastName,
        Integer age,
        Gender gender,
        MaritalStatus maritalStatus,

        ResidentialDetailDTO residentialDetails,
        OfficialDetailDTO officialDetails
) {
}
