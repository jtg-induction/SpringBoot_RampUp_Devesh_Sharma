package com.joshtechnologygroup.minisocial.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserFollowDao {

    private final JdbcTemplate jdbcTemplate;

    public void insertBatch(long followerId, List<Long> followedIds) {
        jdbcTemplate.batchUpdate(
                "insert into followers (following_user, followed_user) values (?, ?)",
                followedIds,
                50,
                (ps, followedId) -> {
                    ps.setLong(1, followerId);
                    ps.setLong(2, followedId);
                }
        );
    }
}
