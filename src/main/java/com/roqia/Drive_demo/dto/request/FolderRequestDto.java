package com.roqia.Drive_demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FolderRequestDto {
    @NotNull
    private Integer parent_folder_id;
    @NotBlank
    private String folder_name;
}
