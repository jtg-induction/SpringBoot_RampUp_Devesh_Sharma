package com.joshtechnologygroup.minisocial.dto.userDetail;

import com.joshtechnologygroup.minisocial.bean.OfficialDetail;
import com.joshtechnologygroup.minisocial.bean.ResidentialDetail;
import com.joshtechnologygroup.minisocial.bean.UserDetail;
import com.joshtechnologygroup.minisocial.dto.officialDetail.OfficialDetailDTO;
import com.joshtechnologygroup.minisocial.dto.residentialDetail.ResidentialDetailDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserDetailMapper {
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "user", ignore = true)
    UserDetail toUserDetail(UserDetailCreateRequest req);

    @Mapping(source = "userDetail.userId", target = "userId")
    @Mapping(source = "res", target = "residentialDetails")
    @Mapping(source = "off", target = "officialDetails")
    UserDetailDTO toDto(UserDetail userDetail, ResidentialDetail res, OfficialDetail off);

    // Helper mappings for the nested parts
    ResidentialDetailDTO toDto(ResidentialDetail entity);

    OfficialDetailDTO toDto(OfficialDetail entity);
}
