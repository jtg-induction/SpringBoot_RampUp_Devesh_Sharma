package com.joshtechnologygroup.minisocial.repository;

import com.joshtechnologygroup.minisocial.bean.UserFollow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserFollowRepository
    extends
        JpaRepository<UserFollow, UserFollow.UserFollowId>
{
    boolean existsByFollowerIdAndFollowedId(Long followerId, Long followedId);

    @Modifying(clearAutomatically = true)
    @Query("delete from UserFollow uf where uf.follower.id = :followerId")
    int deleteByFollowerId(Long followerId);

    Long deleteByFollowerIdAndFollowedId(Long followerId, Long followedId);

    // intentionally custom (projection)
    @Query(
        """
            select uf.followed.id
            from UserFollow uf
            where uf.follower.email = :email
        """
    )
    List<Long> findFollowedIdsByEmail(String email);

    @Query(
        """
            select uf.follower.id
            from UserFollow uf
            where uf.followed.email = :email
        """
    )
    List<Long> findFollowerIdsByEmail(String email);
}
