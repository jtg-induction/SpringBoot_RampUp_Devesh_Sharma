package com.joshtechnologygroup.minisocial.dto.officialDetail;

import com.joshtechnologygroup.minisocial.bean.OfficialDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OfficialDetailMapper {
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "lastModified", ignore = true)
    OfficialDetail toOfficialDetail(OfficialDetailCreateRequest req);

    OfficialDetailDTO toDTO(OfficialDetail officialDetail);
}
