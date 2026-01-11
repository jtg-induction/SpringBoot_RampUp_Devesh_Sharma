package com.joshtechnologygroup.minisocial.factory;

import com.joshtechnologygroup.minisocial.TestDataConfig;
import com.joshtechnologygroup.minisocial.bean.ResidentialDetail;
import com.joshtechnologygroup.minisocial.dto.residentialDetail.ResidentialDetailCreateRequest;
import com.joshtechnologygroup.minisocial.dto.residentialDetail.ResidentialDetailDTO;
import net.datafaker.Faker;

public class ResidentialDetailFactory {
    private static final Faker FAKER = TestDataConfig.FAKER;

    public static ResidentialDetail defaultResidentialDetail() {
        return defaultResidentialDetail((long) FAKER.number()
                .positive());
    }

    public static ResidentialDetail defaultResidentialDetail(Long userId) {
        ResidentialDetail residentialDetail = new ResidentialDetail();
        residentialDetail.setUserId(userId);
        residentialDetail.setAddress(FAKER.address()
                .fullAddress());
        residentialDetail.setCity(FAKER.address()
                .city());
        residentialDetail.setState(FAKER.address()
                .state());
        residentialDetail.setCountry(FAKER.address()
                .country());
        residentialDetail.setContactNo1(FAKER.numerify("+91##########"));
        residentialDetail.setContactNo2(FAKER.numerify("+91##########"));
        return residentialDetail;
    }

    public static ResidentialDetailCreateRequest.ResidentialDetailCreateRequestBuilder defaultResidentialDetailCreateRequest() {
        ResidentialDetail residentialDetail = defaultResidentialDetail();
        return defaultResidentialDetailCreateRequest(residentialDetail);
    }

    public static ResidentialDetailCreateRequest.ResidentialDetailCreateRequestBuilder defaultResidentialDetailCreateRequest(ResidentialDetail residentialDetail) {
        return ResidentialDetailCreateRequest.builder()
                .address(residentialDetail.getAddress())
                .city(residentialDetail.getCity())
                .state(residentialDetail.getState())
                .country(residentialDetail.getCountry())
                .contactNo1(residentialDetail.getContactNo1())
                .contactNo2(residentialDetail.getContactNo2());
    }

    public static ResidentialDetailDTO.ResidentialDetailDTOBuilder defaultResidentialDetailDTO() {
        return defaultResidentialDetailDTO((long) FAKER.number()
                .positive());
    }

    public static ResidentialDetailDTO.ResidentialDetailDTOBuilder defaultResidentialDetailDTO(Long userId) {
        ResidentialDetail residentialDetail = defaultResidentialDetail();
        residentialDetail.setUserId(userId);
        return defaultResidentialDetailDTO(residentialDetail);
    }

    public static ResidentialDetailDTO.ResidentialDetailDTOBuilder defaultResidentialDetailDTO(ResidentialDetail residentialDetail) {
        return ResidentialDetailDTO.builder()
                .userId(residentialDetail.getUserId())
                .address(residentialDetail.getAddress())
                .city(residentialDetail.getCity())
                .state(residentialDetail.getState())
                .country(residentialDetail.getCountry())
                .contactNo1(residentialDetail.getContactNo1())
                .contactNo2(residentialDetail.getContactNo2());
    }
}
