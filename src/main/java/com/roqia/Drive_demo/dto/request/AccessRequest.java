package com.roqia.Drive_demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccessRequest {
    @NotNull
    private int item_id;
    @NotBlank
    private String recipientEmail;
}
