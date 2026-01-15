package com.joshtechnologygroup.minisocial.specification;

import com.joshtechnologygroup.minisocial.bean.User;
import com.joshtechnologygroup.minisocial.enums.Gender;
import com.joshtechnologygroup.minisocial.enums.MaritalStatus;
import com.joshtechnologygroup.minisocial.enums.UserSortOrder;
import jakarta.persistence.criteria.Order;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class UserSpecificationBuilder {
    private Specification<User> spec;

    public UserSpecificationBuilder() {
        this.spec = Specification.unrestricted();
    }

    public UserSpecificationBuilder withFirstName(String firstName) {
        if (firstName != null && !firstName.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(
                            criteriaBuilder.lower(root.join("userDetail")
                                    .get("firstName")),
                            "%" + firstName.toLowerCase() + "%"
                    )
            );
        }
        return this;
    }

    public UserSpecificationBuilder withLastName(String lastName) {
        if (lastName != null && !lastName.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(
                            criteriaBuilder.lower(root.join("userDetail")
                                    .get("lastName")),
                            "%" + lastName.toLowerCase() + "%"
                    )
            );
        }
        return this;
    }

    public UserSpecificationBuilder withMinAge(Integer minAge) {
        if (minAge != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.join("userDetail")
                            .get("age"), minAge)
            );
        }
        return this;
    }

    public UserSpecificationBuilder withMaxAge(Integer maxAge) {
        if (maxAge != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThanOrEqualTo(root.join("userDetail")
                            .get("age"), maxAge)
            );
        }
        return this;
    }

    public UserSpecificationBuilder withMinFollowers(Integer minFollowers) {
        if (minFollowers != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(criteriaBuilder.size(root.get("followers")), minFollowers)
            );
        }
        return this;
    }

    public UserSpecificationBuilder withMaxFollowers(Integer maxFollowers) {
        if (maxFollowers != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThanOrEqualTo(criteriaBuilder.size(root.get("followers")), maxFollowers)
            );
        }
        return this;
    }

    public UserSpecificationBuilder withMinFollowing(Integer minFollowing) {
        if (minFollowing != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(criteriaBuilder.size(root.get("followed")), minFollowing)
            );
        }
        return this;
    }

    public UserSpecificationBuilder withMaxFollowing(Integer maxFollowing) {
        if (maxFollowing != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThanOrEqualTo(criteriaBuilder.size(root.get("followed")), maxFollowing)
            );
        }
        return this;
    }

    public UserSpecificationBuilder withMaritalStatus(MaritalStatus maritalStatus) {
        if (maritalStatus != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.join("userDetail")
                            .get("maritalStatus"), maritalStatus)
            );
        }
        return this;
    }

    public UserSpecificationBuilder withGender(Gender gender) {
        if (gender != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.join("userDetail")
                            .get("gender"), gender));
        }
        return this;
    }

    public UserSpecificationBuilder companyNameIn(List<String> companyNames) {
        if (companyNames != null && !companyNames.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    root.join("officialDetail")
                            .get("companyName")
                            .in(companyNames)
            );
        }
        return this;
    }

    public UserSpecificationBuilder residentialCityIn(List<String> cities) {
        if (cities != null && !cities.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    root.join("residentialDetail")
                            .get("city")
                            .in(cities)
            );
        }
        return this;
    }

    public UserSpecificationBuilder officialCityIn(List<String> cities) {
        if (cities != null && !cities.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    root.join("officialDetail")
                            .get("city")
                            .in(cities)
            );
        }
        return this;
    }

    public UserSpecificationBuilder isActive(Boolean isActive) {
        if (isActive != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("active"), isActive)
            );
        }
        return this;
    }

    public UserSpecificationBuilder orderBy(List<UserSortOrder> order) {
        if (order != null && !order.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) -> {
                List<Order> orders = new ArrayList<>();

                for (UserSortOrder ord : order) {
                    switch (ord) {
                        case NAME -> {
                            // Order by first and last name
                            orders.add(criteriaBuilder.desc(root.join("userDetail")
                                    .get("firstName")));
                            orders.add(criteriaBuilder.desc(root.join("userDetail")
                                    .get("lastName")));
                        }
                        case EMAIL -> orders.add(criteriaBuilder.desc(root.get("email")));
                        case RESIDENTIAL_DETAIL -> {
                            orders.add(criteriaBuilder.desc(root.join("residentialDetail")
                                    .get("city")));
                            orders.add(criteriaBuilder.desc(root.join("residentialDetail")
                                    .get("state")));
                            orders.add(criteriaBuilder.desc(root.join("residentialDetail")
                                    .get("country")));
                        }
                        case FOLLOWING_COUNT -> orders.add(criteriaBuilder.desc(
                                criteriaBuilder.size(root.get("followed"))));
                        case FOLLOWER_COUNT -> orders.add(criteriaBuilder.desc(
                                criteriaBuilder.size(root.get("followers"))));
                        case GENDER -> orders.add(criteriaBuilder.desc(root.join("userDetail")
                                .get("gender")));
                        case MARITAL_STATUS -> orders.add(criteriaBuilder.desc(root.join("userDetail")
                                .get("maritalStatus")));
                        case COMPANY_NAME -> orders.add(criteriaBuilder.desc(root.join("officialDetail")
                                .get("companyName")));
                    }
                }

                if (!orders.isEmpty()) {
                    query.orderBy(orders.toArray(new Order[0]));
                }
                return criteriaBuilder.conjunction();
            });
        }
        return this;
    }

    public Specification<User> build() {
        return spec;
    }
}
