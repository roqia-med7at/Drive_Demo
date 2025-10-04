package com.roqia.Drive_demo.dto.response;

import com.roqia.Drive_demo.Enum.Access;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestAccessResponse {
    @NotNull
    private int user_id;
    @NotBlank
    private String user_email;
    @NotBlank
    private Access access;
    @NotBlank
    private String action;
}
