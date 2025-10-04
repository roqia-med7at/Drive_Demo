package com.roqia.Drive_demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "providers",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "provider_name"}),
                @UniqueConstraint(columnNames = {"provider_id", "provider_name"})
        }
)
@Getter
@Setter
public class Provider {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "provider_id",nullable = false)
    private String providerId;
    @Column(name = "user_id",nullable = false)
    private int userId;
    @Column(name = "provider_name",nullable = false)
    private String providerName;
    private String accessToken;
    private String refreshAccessToken;
    private Instant expiryDate;

}
