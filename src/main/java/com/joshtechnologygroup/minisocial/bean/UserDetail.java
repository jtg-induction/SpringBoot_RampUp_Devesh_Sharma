package com.joshtechnologygroup.minisocial.bean;

import com.joshtechnologygroup.minisocial.enums.Gender;
import com.joshtechnologygroup.minisocial.enums.MaritalStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.*;

import java.time.Instant;

@Data
@Entity
@Table(name = "user_details")
public class UserDetail {
    @Id
    @Column(nullable = false)
    private Long userId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @EqualsAndHashCode.Exclude
    @JoinColumn(name = "user_id")
    private User user;

    @Size(max = 255)
    @NotNull
    @Column(nullable = false)
    private String firstName;

    @Size(max = 255)
    @Column
    private String lastName;

    @Column(columnDefinition = "int UNSIGNED")
    private Integer age;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @NotNull
    @ColumnDefault("'single'")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MaritalStatus maritalStatus;

    @Version
    private Long version;

    @Column(name = "created_at")
    @CreationTimestamp()
    private Instant createdAt;

    @Column(name = "last_modified")
    @UpdateTimestamp()
    private Instant lastModified;
}
