package com.roqia.Drive_demo.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeleteUserAccessRequest {
    @NotNull
    private int itemId;
    @NotNull
    private int userId;
}
