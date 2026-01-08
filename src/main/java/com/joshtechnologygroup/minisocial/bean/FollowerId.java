package com.joshtechnologygroup.minisocial.bean;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@EqualsAndHashCode
@Embeddable
public class FollowerId implements Serializable {
    private static final long serialVersionUID = -5847704880109750714L;
    @NotNull
    @Column(name = "followed_user", nullable = false)
    private Long followedUser;

    @NotNull
    @Column(name = "following_user", nullable = false)
    private Long followingUser;


}