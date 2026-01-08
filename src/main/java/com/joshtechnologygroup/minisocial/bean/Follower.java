package com.joshtechnologygroup.minisocial.bean;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "followers")
public class Follower {
    @EmbeddedId
    private FollowerId id;

    @MapsId("followedUser")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "followed_user", nullable = false)
    private User followedUser;

    @MapsId("followingUser")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "following_user", nullable = false)
    private User followingUser;


}