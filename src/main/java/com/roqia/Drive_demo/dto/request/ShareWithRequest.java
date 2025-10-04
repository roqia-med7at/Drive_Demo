package com.roqia.Drive_demo.dto.request;

import com.roqia.Drive_demo.Enum.Access;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShareWithRequest {
    @NotNull
    private int itemId;
    @NotBlank
    private String email;
    @NotNull
    private Access access;
}
