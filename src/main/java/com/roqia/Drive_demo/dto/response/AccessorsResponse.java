package com.roqia.Drive_demo.dto.response;

import com.roqia.Drive_demo.Enum.Access;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccessorsResponse {
    private String name;
    private String email;
    private Access access;
}
