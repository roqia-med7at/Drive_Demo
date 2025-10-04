package com.roqia.Drive_demo.model;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "files")
@Getter
@Setter
@NoArgsConstructor
public class File extends Item{
    private long fileSize;
    @Column(nullable = false)
    private String fileExtension;
    @ManyToOne
    @JoinColumn(name = "folder_id",nullable = false)
    private Folder folder;
    private int version;


}
