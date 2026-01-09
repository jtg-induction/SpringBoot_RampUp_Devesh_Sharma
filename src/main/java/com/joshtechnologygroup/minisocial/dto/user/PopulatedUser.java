package com.joshtechnologygroup.minisocial.dto.user;

import com.joshtechnologygroup.minisocial.bean.OfficialDetail;
import com.joshtechnologygroup.minisocial.bean.ResidentialDetail;
import com.joshtechnologygroup.minisocial.bean.User;
import com.joshtechnologygroup.minisocial.bean.UserDetail;
import lombok.Builder;

@Builder
public record PopulatedUser (
    User user,
    UserDetail userDetail,
    ResidentialDetail residentialDetail,
    OfficialDetail officialDetail
){}
