package com.roqia.Drive_demo.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.antlr.v4.runtime.misc.NotNull;

@Getter
@Setter
public class AccessLinkRequest {
    @Email
    private String email;
    @NotBlank
    private String token;
}
