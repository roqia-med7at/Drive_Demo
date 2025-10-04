package com.roqia.Drive_demo.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "folders")
@Getter
@Setter
@NoArgsConstructor
public class Folder extends Item {
    @ManyToOne
    @JoinColumn(name = "parent_folder_id")
    private Folder parentFolder;

}
