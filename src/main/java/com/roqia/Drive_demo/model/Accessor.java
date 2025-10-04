package com.roqia.Drive_demo.model;

import com.roqia.Drive_demo.Enum.Access;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "accessors",uniqueConstraints = @UniqueConstraint(columnNames = {"user_id","item_id"}))
@Getter
@Setter
public class Accessor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Enumerated(EnumType.STRING)
    private Access access;
    @ManyToOne
    @JoinColumn(name = "item_id",nullable = false)
    private Item item;
    @ManyToOne
    @JoinColumn(name = "user_id",nullable = false)
    private User user;
}
