package com.roqia.Drive_demo.model;

import com.roqia.Drive_demo.Enum.Permission;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
public class SharedLink {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @OneToOne
    @JoinColumn(name = "sharedItem_id",nullable = false,unique = true)
    private Item sharedItem;
    @Column(unique = true)
    private String token;
    private LocalDateTime expiryDate;
    private boolean revoked = false;
    @Enumerated(EnumType.STRING)
    private Permission permission=Permission.SHARED_WITH_ONLY;
}
