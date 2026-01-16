package com.joshtechnologygroup.minisocial.factory;

import com.joshtechnologygroup.minisocial.TestDataConfig;
import com.joshtechnologygroup.minisocial.bean.OfficialDetail;
import com.joshtechnologygroup.minisocial.dto.officialDetail.OfficialDetailDTO;
import net.datafaker.Faker;

public class OfficialDetailFactory {
    private static final Faker FAKER = TestDataConfig.FAKER;

    public static OfficialDetail defaultOfficialDetail() {
        return defaultOfficialDetail((long) FAKER.number()
                .positive());
    }

    public static OfficialDetail defaultOfficialDetail(Long userId) {
        OfficialDetail officialDetail = new OfficialDetail();
        officialDetail.setUserId(userId);
        officialDetail.setEmployeeCode(FAKER.letterify("??????"));
        officialDetail.setAddress(FAKER.address()
                .fullAddress());
        officialDetail.setCity(FAKER.address()
                .city());
        officialDetail.setState(FAKER.address()
                .state());
        officialDetail.setCountry(FAKER.address()
                .country());
        officialDetail.setCompanyContactNo(FAKER.numerify("+91##########"));
        officialDetail.setCompanyContactEmail(FAKER.internet()
                .emailAddress());
        officialDetail.setCompanyName(FAKER.company()
                .name());
        return officialDetail;
    }

    public static OfficialDetailDTO.OfficialDetailDTOBuilder defaultOfficialDetailDTO(Long userId) {
        OfficialDetail officialDetail = defaultOfficialDetail();
        officialDetail.setUserId(userId);
        return defaultOfficialDetailDTO(officialDetail);
    }

    public static OfficialDetailDTO.OfficialDetailDTOBuilder defaultOfficialDetailDTO(OfficialDetail officialDetail) {
        return OfficialDetailDTO.builder()
                .employeeCode(officialDetail.getEmployeeCode())
                .address(officialDetail.getAddress())
                .city(officialDetail.getCity())
                .state(officialDetail.getState())
                .country(officialDetail.getCountry())
                .companyContactNo(officialDetail.getCompanyContactNo())
                .companyContactEmail(officialDetail.getCompanyContactEmail())
                .companyName(officialDetail.getCompanyName());
    }
}
