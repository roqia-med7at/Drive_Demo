package com.roqia.Drive_demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserProfileDto {
    @NotBlank
    private String name;
    @NotBlank
    private String role;
    @Email
    private String email;

}
