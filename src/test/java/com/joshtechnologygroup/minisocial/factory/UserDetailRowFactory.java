package com.joshtechnologygroup.minisocial.factory;

import com.joshtechnologygroup.minisocial.TestDataConfig;
import com.joshtechnologygroup.minisocial.bean.Gender;
import com.joshtechnologygroup.minisocial.bean.MaritalStatus;
import com.joshtechnologygroup.minisocial.tool.bean.UserDetailRow;
import net.datafaker.Faker;

public class UserDetailRowFactory {
    private static final Faker FAKER = TestDataConfig.FAKER;

    public static UserDetailRow defaultUserDetailRow() {
        UserDetailRow userDetailRow = new UserDetailRow();
        userDetailRow.setEmailId(FAKER.internet().emailAddress());
        userDetailRow.setPassword(FAKER.credentials().password());
        userDetailRow.setFirstName(FAKER.name().firstName());
        userDetailRow.setLastName(FAKER.name().lastName());
        userDetailRow.setAge(FAKER.number().numberBetween(18, 65));
        userDetailRow.setMaritalStatus(FAKER.options().option(MaritalStatus.class));
        userDetailRow.setGender(FAKER.options().option(Gender.class));
        userDetailRow.setResidentialAddress(FAKER.address().streetAddress());
        userDetailRow.setOfficeAddress(FAKER.address().streetAddress());
        userDetailRow.setResidentialCity(FAKER.address().city());
        userDetailRow.setOfficeCity(FAKER.address().city());
        userDetailRow.setResidentialState(FAKER.address().state());
        userDetailRow.setOfficeState(FAKER.address().state());
        userDetailRow.setResidentialCountry(FAKER.address().country());
        userDetailRow.setOfficeCountry(FAKER.address().country());
        userDetailRow.setPrimaryContactNumber(FAKER.numerify("+91##########"));
        userDetailRow.setSecondaryContactNumber(FAKER.numerify("+91##########"));
        userDetailRow.setEmployeeCode(FAKER.letterify("??????"));
        userDetailRow.setCompanyName(FAKER.company().name());
        userDetailRow.setCompanyContactEmail(FAKER.internet().emailAddress());
        userDetailRow.setCompanyContactNumber(FAKER.numerify("+91##########"));

        return userDetailRow;
    }
}
