package com.joshtechnologygroup.minisocial.dto.residentialDetail;

import com.joshtechnologygroup.minisocial.bean.ResidentialDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ResidentialDetailMapper {
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "lastModified", ignore = true)
    @Mapping(target = "version", ignore = true)
    ResidentialDetail toResidentialDetail(ResidentialDetailCreateRequest req);

    ResidentialDetailDTO toDto(ResidentialDetail residentialDetail);
}
