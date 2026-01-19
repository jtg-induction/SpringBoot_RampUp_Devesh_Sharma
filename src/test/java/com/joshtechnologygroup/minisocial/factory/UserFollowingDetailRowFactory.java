package com.joshtechnologygroup.minisocial.factory;

import com.joshtechnologygroup.minisocial.TestDataConfig;
import com.joshtechnologygroup.minisocial.tool.bean.UserFollowingDetailRow;
import net.datafaker.Faker;

import java.util.List;
import java.util.stream.IntStream;

public class UserFollowingDetailRowFactory {
    private static final Faker FAKER = TestDataConfig.FAKER;

    public static UserFollowingDetailRow defaultUserFollowingDetailRow() {
        UserFollowingDetailRow userFollowingDetailRow = new UserFollowingDetailRow();
        userFollowingDetailRow.setUserEmail(FAKER.internet().emailAddress());

        // Generate a list of 2-5 email addresses for following
        int followingCount = FAKER.number().numberBetween(2, 6);
        List<String> followingEmails = IntStream.range(0, followingCount)
                .mapToObj(i -> FAKER.internet().emailAddress())
                .toList();

        userFollowingDetailRow.setFollowing(followingEmails);

        return userFollowingDetailRow;
    }

    public static UserFollowingDetailRow userFollowingDetailRowWithEmails(String userEmail, List<String> followingEmails) {
        UserFollowingDetailRow userFollowingDetailRow = new UserFollowingDetailRow();
        userFollowingDetailRow.setUserEmail(userEmail);
        userFollowingDetailRow.setFollowing(followingEmails);
        return userFollowingDetailRow;
    }
}
