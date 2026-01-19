package com.joshtechnologygroup.minisocial.dto.userDetail;

import com.joshtechnologygroup.minisocial.bean.UserDetail;
import com.joshtechnologygroup.minisocial.dto.officialDetail.OfficialDetailMapper;
import com.joshtechnologygroup.minisocial.dto.residentialDetail.ResidentialDetailMapper;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {
        ResidentialDetailMapper.class,
        OfficialDetailMapper.class
})
public interface UserDetailMapper {
    @Mapping(source = "userDetail.user.residentialDetail", target = "residentialDetails")
    @Mapping(source = "userDetail.user.officialDetail", target = "officialDetails")
    UserDetailDTO toDto(UserDetail userDetail);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "userId", ignore = true)
    void updateUserDetail(UserDetailDTO dto, @MappingTarget UserDetail entity);
}
