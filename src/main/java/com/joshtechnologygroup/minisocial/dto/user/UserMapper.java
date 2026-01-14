package com.joshtechnologygroup.minisocial.dto.user;

import com.joshtechnologygroup.minisocial.bean.User;
import com.joshtechnologygroup.minisocial.dto.userDetail.UserDetailDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "userDetail", ignore = true)
    @Mapping(target = "officialDetail", ignore = true)
    @Mapping(target = "residentialDetail", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "lastModified", ignore = true)
    @Mapping(target = "followed", ignore = true)
    @Mapping(target = "followers", ignore = true)
    @Mapping(target = "id", ignore = true)
    User createDtoToUser(UserCreateRequest req);

    @Mapping(source = "details", target = "userDetails")
    UserDTO toDto(User user, UserDetailDTO details);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "lastModified", ignore = true)
    @Mapping(target = "followed", ignore = true)
    @Mapping(target = "followers", ignore = true)
    @Mapping(target = "userDetail", ignore = true)
    @Mapping(target = "officialDetail", ignore = true)
    @Mapping(target = "residentialDetail", ignore = true)
    User updateDtoToUser(UserUpdateRequest req);
}
