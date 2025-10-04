package com.roqia.Drive_demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "shared_files",uniqueConstraints = {
        @UniqueConstraint(columnNames = {"item_id", "shared_with_id"})
})
public class SharedItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private Item sharedItem;
    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User ownedBy;
    @ManyToOne
    @JoinColumn(name = "shared_with_id", nullable = false)
    private User sharedWith;
    private LocalDateTime createdAt;
}
