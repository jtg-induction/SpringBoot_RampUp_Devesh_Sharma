package com.joshtechnologygroup.minisocial.bean;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Table(name = "followers")
@IdClass(UserFollow.UserFollowId.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserFollow {
    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "following_user", nullable = false)
    private User follower;

    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "followed_user", nullable = false)
    private User followed;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserFollowId implements Serializable {
        private Long follower;
        private Long followed;
    }
}
