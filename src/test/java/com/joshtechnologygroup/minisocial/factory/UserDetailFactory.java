package com.joshtechnologygroup.minisocial.factory;

import com.joshtechnologygroup.minisocial.TestDataConfig;
import com.joshtechnologygroup.minisocial.bean.Gender;
import com.joshtechnologygroup.minisocial.bean.MaritalStatus;
import com.joshtechnologygroup.minisocial.bean.UserDetail;
import com.joshtechnologygroup.minisocial.dto.userDetail.UserDetailCreateRequest;
import com.joshtechnologygroup.minisocial.dto.userDetail.UserDetailDTO;
import net.datafaker.Faker;

public class UserDetailFactory {
    private static final Faker FAKER = TestDataConfig.FAKER;

    public static UserDetail defaultUserDetail() {
        return defaultUserDetail((long) FAKER.number()
                .positive());
    }

    public static UserDetail defaultUserDetail(Long userId) {
        UserDetail userDetail = new UserDetail();
        userDetail.setUserId(userId);
        userDetail.setFirstName(FAKER.name()
                .firstName());
        userDetail.setLastName(FAKER.name()
                .lastName());
        userDetail.setAge(FAKER.number()
                .numberBetween(18, 90));
        userDetail.setGender(FAKER.options()
                .option(Gender.class));
        userDetail.setMaritalStatus(FAKER.options()
                .option(MaritalStatus.class));
        return userDetail;
    }

    public static UserDetailCreateRequest.UserDetailCreateRequestBuilder defaultUserDetailCreateRequest() {
        UserDetail userDetail = defaultUserDetail();
        return defaultUserDetailCreateRequest(userDetail);
    }

    public static UserDetailCreateRequest.UserDetailCreateRequestBuilder defaultUserDetailCreateRequest(UserDetail userDetail) {
        return UserDetailCreateRequest.builder()
                .firstName(userDetail.getFirstName())
                .lastName(userDetail.getLastName())
                .age(userDetail.getAge())
                .gender(userDetail.getGender())
                .maritalStatus(userDetail.getMaritalStatus())
                .residentialDetails(ResidentialDetailFactory.defaultResidentialDetailCreateRequest()
                        .build())
                .officialDetails(OfficialDetailFactory.defaultOfficialDetailCreateRequest()
                        .build());
    }

    public static UserDetailDTO.UserDetailDTOBuilder defaultUserDetailDTO() {
        return defaultUserDetailDTO((long) FAKER.number()
                .positive());
    }

    public static UserDetailDTO.UserDetailDTOBuilder defaultUserDetailDTO(Long userId) {
        UserDetail userDetail = defaultUserDetail();
        userDetail.setUserId(userId);
        return defaultUserDetailDTO(userDetail);
    }

    public static UserDetailDTO.UserDetailDTOBuilder defaultUserDetailDTO(UserDetail userDetail) {
        return UserDetailDTO.builder()
                .userId(userDetail.getUserId())
                .firstName(userDetail.getFirstName())
                .lastName(userDetail.getLastName())
                .age(userDetail.getAge())
                .gender(userDetail.getGender())
                .maritalStatus(userDetail.getMaritalStatus())
                .residentialDetails(ResidentialDetailFactory.defaultResidentialDetailDTO(userDetail.getUserId())
                        .build())
                .officialDetails(OfficialDetailFactory.defaultOfficialDetailDTO(userDetail.getUserId())
                        .build());
    }
}
