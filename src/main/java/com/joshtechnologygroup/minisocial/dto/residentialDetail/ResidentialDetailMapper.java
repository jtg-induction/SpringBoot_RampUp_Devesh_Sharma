package com.joshtechnologygroup.minisocial.dto.residentialDetail;

import com.joshtechnologygroup.minisocial.bean.ResidentialDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ResidentialDetailMapper {
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "user", ignore = true)
    ResidentialDetail toResidentialDetail(ResidentialDetailCreateRequest req);

    @Mapping(target = "user", ignore = true)
    ResidentialDetail dtoToResidentialDetail(ResidentialDetailDTO dto);
}
