package com.roqia.Drive_demo.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MoveOrCopyFileRequest {
    @NotNull
   private Integer file_id;
    @NotNull
    private Integer new_folder_id;
}
