package com.joshtechnologygroup.minisocial.dto.userDetail;

import com.joshtechnologygroup.minisocial.bean.UserDetail;
import com.joshtechnologygroup.minisocial.dto.officialDetail.OfficialDetailMapper;
import com.joshtechnologygroup.minisocial.dto.residentialDetail.ResidentialDetailMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(
    componentModel = "spring",
    uses = { ResidentialDetailMapper.class, OfficialDetailMapper.class }
)
public interface UserDetailMapper {
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "lastModified", ignore = true)
    UserDetail toUserDetail(UserDetailCreateRequest req);

    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "lastModified", ignore = true)
    void updateUserDetails(UserDetailDTO dto, @MappingTarget UserDetail entity);

    @Mapping(source = "userDetail.user.residentialDetail", target = "residentialDetails")
    @Mapping(source = "userDetail.user.officialDetail", target = "officialDetails")
    UserDetailDTO toDto(UserDetail userDetail);
}
