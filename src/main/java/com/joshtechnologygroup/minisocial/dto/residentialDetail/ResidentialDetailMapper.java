package com.joshtechnologygroup.minisocial.dto.residentialDetail;

import com.joshtechnologygroup.minisocial.bean.ResidentialDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ResidentialDetailMapper {
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "lastModified", ignore = true)
    ResidentialDetail toResidentialDetail(ResidentialDetailCreateRequest req);

    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "lastModified", ignore = true)
    void updateResidentialDetails(ResidentialDetailDTO dto, @MappingTarget ResidentialDetail entity);

    ResidentialDetailDTO toDto(ResidentialDetail residentialDetail);
}
