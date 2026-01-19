package com.joshtechnologygroup.minisocial.bean;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Data
@Entity
@Table(name = "official_details")
public class OfficialDetail {
    @Id
    @Column(nullable = false)
    private Long userId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id")
    @EqualsAndHashCode.Exclude
    private User user;

    @Size(max = 255)
    @NotNull
    @Column(nullable = false)
    private String employeeCode;

    @Size(max = 255)
    @NotNull
    @Column(nullable = false)
    private String address;

    @Size(max = 255)
    @NotNull
    @Column(nullable = false)
    private String city;

    @Size(max = 255)
    @NotNull
    @Column(nullable = false)
    private String state;

    @Size(max = 255)
    @NotNull
    @Column(nullable = false)
    private String country;

    @Size(max = 255)
    @NotNull
    @Column(nullable = false)
    private String companyContactNo;

    @Size(max = 255)
    @NotNull
    @Column(nullable = false)
    private String companyContactEmail;

    @Size(max = 255)
    @NotNull
    @Column(nullable = false)
    private String companyName;

    @Version
    private Long version;

    @Column(name = "created_at")
    @CreationTimestamp()
    private Instant createdAt;

    @Column(name = "last_modified")
    @UpdateTimestamp()
    private Instant lastModified;
}
