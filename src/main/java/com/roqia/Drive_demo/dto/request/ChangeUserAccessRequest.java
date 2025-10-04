package com.roqia.Drive_demo.dto.request;

import com.roqia.Drive_demo.Enum.Access;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangeUserAccessRequest {
    @NotNull
    private int itemId;
    @NotNull
    private int userId;
    @NotNull
    private Access newAccess;
}
