package com.joshtechnologygroup.minisocial.dto.user;

import com.joshtechnologygroup.minisocial.bean.OfficialDetail;
import com.joshtechnologygroup.minisocial.bean.ResidentialDetail;
import com.joshtechnologygroup.minisocial.bean.User;
import com.joshtechnologygroup.minisocial.bean.UserDetail;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record PopulatedUser (
    @NotNull(message = "User is required")
    User user,

    @NotNull(message = "User Detail is required")
    UserDetail userDetail,

    @NotNull(message = "Residential Detail is required")
    ResidentialDetail residentialDetail,

    @NotNull(message = "Official Detail is required")
    OfficialDetail officialDetail
){}
