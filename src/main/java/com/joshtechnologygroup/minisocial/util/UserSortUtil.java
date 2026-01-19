package com.joshtechnologygroup.minisocial.util;

import com.joshtechnologygroup.minisocial.enums.UserSortOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public final class UserSortUtil {
    public static Sort createSort(List<UserSortOrder> sortOrders) {
        if (sortOrders == null || sortOrders.isEmpty()) {
            return Sort.unsorted();
        }

        List<Sort.Order> orders = new ArrayList<>();

        for (UserSortOrder sortOrder : sortOrders) {
            switch (sortOrder) {
                case NAME -> {
                    orders.add(Sort.Order.desc("userDetail.firstName"));
                    orders.add(Sort.Order.desc("userDetail.lastName"));
                }
                case EMAIL -> orders.add(Sort.Order.desc("email"));
                case RESIDENTIAL_DETAIL -> {
                    orders.add(Sort.Order.desc("residentialDetail.city"));
                    orders.add(Sort.Order.desc("residentialDetail.state"));
                    orders.add(Sort.Order.desc("residentialDetail.country"));
                }
                case GENDER -> orders.add(Sort.Order.desc("userDetail.gender"));
                case MARITAL_STATUS -> orders.add(Sort.Order.desc("userDetail.maritalStatus"));
                case COMPANY_NAME -> orders.add(Sort.Order.desc("officialDetail.companyName"));
                case FOLLOWING_COUNT, FOLLOWER_COUNT -> log.warn("Collection size sorting ({}) not supported via Pageable, use specification instead", sortOrder);
            }
        }

        return Sort.by(orders);
    }
}
