package com.roqia.Drive_demo.dto.request;

import com.roqia.Drive_demo.Enum.Access;
import com.roqia.Drive_demo.Enum.RequestStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RespondRequest {
    @NotNull
   private int requestId;
    @NotNull
    private int itemId;
    @NotNull
    private Access access;
    @NotNull
    private RequestStatus action;
}
