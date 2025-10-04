package com.roqia.Drive_demo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordDto {
    @NotBlank
    private String old_password;
    @NotBlank
    private String new_password;

}
