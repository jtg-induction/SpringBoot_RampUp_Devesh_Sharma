package com.joshtechnologygroup.minisocial.dto.user;

import com.joshtechnologygroup.minisocial.bean.User;
import com.joshtechnologygroup.minisocial.dto.officialDetail.OfficialDetailMapper;
import com.joshtechnologygroup.minisocial.dto.residentialDetail.ResidentialDetailMapper;
import com.joshtechnologygroup.minisocial.dto.userDetail.UserDetailMapper;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {
        UserDetailMapper.class,
        ResidentialDetailMapper.class,
        OfficialDetailMapper.class
})
public interface UserMapper {
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "lastModified", ignore = true)
    @Mapping(target = "followed", ignore = true)
    @Mapping(target = "followers", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userDetail", ignore = true)
    @Mapping(target = "residentialDetail", ignore = true)
    @Mapping(target = "officialDetail", ignore = true)
    @Mapping(target = "active", ignore = true)
    User createDtoToUser(UserCreateRequest req);

    @Mapping(source = "userDetail", target = "userDetails")
    UserDTO toDto(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "lastModified", ignore = true)
    @Mapping(target = "followed", ignore = true)
    @Mapping(target = "followers", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(source = "userDetails", target = "userDetail")
    @Mapping(source = "userDetails.residentialDetails", target = "residentialDetail")
    @Mapping(source = "userDetails.officialDetails", target = "officialDetail")
    void updateEntityFromDto(UserUpdateRequest req, @MappingTarget User user);

    @AfterMapping
    default void afterUserUpdateRequestConversion(UserUpdateRequest req, @MappingTarget User user) {
        linkUserDetails(user);
    }

    private void linkUserDetails(@MappingTarget User user) {
        if (user.getUserDetail() != null) {
            user.getUserDetail().setUser(user);
        }
        if (user.getOfficialDetail() != null) {
            user.getOfficialDetail().setUser(user);
        }
        if (user.getResidentialDetail() != null) {
            user.getResidentialDetail().setUser(user);
        }
    }
}
