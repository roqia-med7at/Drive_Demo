package com.roqia.Drive_demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  int id;
    private String name;
    private String password;
    @Column(unique = true, nullable = false)
    private String email;
    private String role;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "root_folder_id", referencedColumnName = "id")
    private Folder rootFolder;
}
