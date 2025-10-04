package com.roqia.Drive_demo.dto.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SharedItemResponse {
    private int itemId;
    private String itemName;
    private String ownedBy;
}
