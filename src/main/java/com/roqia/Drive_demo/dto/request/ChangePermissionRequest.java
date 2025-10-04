package com.roqia.Drive_demo.dto.request;

import com.roqia.Drive_demo.Enum.Permission;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePermissionRequest {
    @NotNull
    private int itemId;
    @NotNull
    private Permission newPermission;
}
