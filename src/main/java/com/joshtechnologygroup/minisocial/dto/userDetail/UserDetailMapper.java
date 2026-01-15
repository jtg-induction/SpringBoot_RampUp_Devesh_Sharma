package com.joshtechnologygroup.minisocial.dto.userDetail;

import com.joshtechnologygroup.minisocial.bean.UserDetail;
import com.joshtechnologygroup.minisocial.dto.officialDetail.OfficialDetailMapper;
import com.joshtechnologygroup.minisocial.dto.residentialDetail.ResidentialDetailMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {
        ResidentialDetailMapper.class,
        OfficialDetailMapper.class
})
public interface UserDetailMapper {
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "user", ignore = true)
    UserDetail toUserDetail(UserDetailCreateRequest req);

    @Mapping(target = "user", ignore = true)
    UserDetail dtoToUserDetail(UserDetailDTO dto);

    @Mapping(source = "userDetail.userId", target = "userId")
    @Mapping(source = "userDetail.user.residentialDetail", target = "residentialDetails")
    @Mapping(source = "userDetail.user.officialDetail", target = "officialDetails")
    UserDetailDTO toDto(UserDetail userDetail);
}
