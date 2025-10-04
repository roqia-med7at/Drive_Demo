package com.roqia.Drive_demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RenameFolderRequest {
    @NotNull
    private Integer folder_id;
    @NotBlank
    private String new_folder_name;
}
