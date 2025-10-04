package com.roqia.Drive_demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RenameFileRequest {
    @NotNull
    private Integer file_id;
    @NotBlank
    private String new_name;
}
