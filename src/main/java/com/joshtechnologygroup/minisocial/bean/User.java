package com.joshtechnologygroup.minisocial.bean;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SourceType;

import java.time.Instant;
import java.util.HashSet;
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
    private Boolean active = true;

    @Column(name = "created_at")
    @CreationTimestamp(source = SourceType.DB)
    private Instant createdAt;

    @Column(name = "last_modified")
    @UpdateTimestamp(source = SourceType.DB)
    private Instant lastModified;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    private UserDetail userDetail;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    private OfficialDetail officialDetail;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    private ResidentialDetail residentialDetail;

    @ManyToMany
    @EqualsAndHashCode.Exclude
    @JoinTable(
            name = "followers",
            joinColumns = @JoinColumn(name = "following_user"),
            inverseJoinColumns = @JoinColumn(name = "followed_user")
    )
    Set<User> followed = new HashSet<>();

    @ManyToMany(mappedBy = "followed")
    @EqualsAndHashCode.Exclude
    Set<User> followers = new HashSet<>();

    public void addFollowed(User followed) {
        this.followed.add(followed);   // Add to owning side
        followed.followers.add(this);    // Add to inverse side (in-memory)
    }

    public void removeFollowed(User followed) {
        this.followed.remove(followed); // Remove from owning side
        followed.followers.remove(this);  // Remove from inverse side (in-memory)
    }
}
