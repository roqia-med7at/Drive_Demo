package com.roqia.Drive_demo.model;

import com.roqia.Drive_demo.Enum.RequestStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class AccessRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne
    @JoinColumn(name = "sender_id",nullable = false)
    private User sender;
    @ManyToOne
    @JoinColumn(name = "recipient_id",nullable = false)
    private User recipient;
    @Enumerated(EnumType.STRING)
    private RequestStatus status=RequestStatus.PENDING;
}
