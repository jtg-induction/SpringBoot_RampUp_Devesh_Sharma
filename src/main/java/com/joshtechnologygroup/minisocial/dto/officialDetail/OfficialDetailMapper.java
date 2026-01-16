package com.joshtechnologygroup.minisocial.dto.officialDetail;

import com.joshtechnologygroup.minisocial.bean.OfficialDetail;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface OfficialDetailMapper {
    OfficialDetailDTO entityToDTO(OfficialDetail officialDetail);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "userId", ignore = true)
    void updateOfficialDetail(OfficialDetailDTO dto, @MappingTarget OfficialDetail entity);
}
