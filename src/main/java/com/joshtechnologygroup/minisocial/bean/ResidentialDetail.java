package com.joshtechnologygroup.minisocial.bean;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Data
@Entity
@Table(name = "residential_details")
public class ResidentialDetail {
    @Id
    @Column(nullable = false)
    private Long userId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

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
    private String contactNo1;

    @Size(max = 255)
    @Column
    private String contactNo2;
}
