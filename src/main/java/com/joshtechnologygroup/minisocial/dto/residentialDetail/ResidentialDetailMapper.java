package com.joshtechnologygroup.minisocial.dto.residentialDetail;

import com.joshtechnologygroup.minisocial.bean.ResidentialDetail;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ResidentialDetailMapper {
    ResidentialDetailDTO entityToDTO(ResidentialDetail residentialDetail);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "userId", ignore = true)
    void updateResidentialDetail(ResidentialDetailDTO dto, @MappingTarget ResidentialDetail entity);
}
