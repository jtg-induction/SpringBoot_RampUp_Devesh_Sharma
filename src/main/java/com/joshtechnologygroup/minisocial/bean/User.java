package com.joshtechnologygroup.minisocial.bean;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.Instant;
import java.util.Set;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 255)
    @NotNull
    @Column(name = "email", nullable = false)
    private String email;

    @Size(max = 512)
    @NotNull
    @Column(name = "password", nullable = false, length = 512)
    private String password;

    @Column(name = "active")
    private Boolean active;

    @Column(name = "created_at")
    private Instant createdAt = Instant.now();

    @Column(name = "last_modified")
    private Instant lastModified = Instant.now();

    @ManyToMany(mappedBy = "followers")
    Set<User> followed;

    @ManyToMany
    @JoinTable(
            name = "followers",
            joinColumns = @JoinColumn(name = "followed_user"),
            inverseJoinColumns = @JoinColumn(name = "following_user")
    )
    Set<User> followers;
}
