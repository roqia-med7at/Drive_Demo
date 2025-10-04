package com.roqia.Drive_demo.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MoveOrCopyFolderRequest {
    @NotNull
    private Integer folder_id;
    @NotNull
   private Integer new_parent_folder_id;
}
