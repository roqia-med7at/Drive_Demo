package com.roqia.Drive_demo.dto.response;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ReadFileResponse {
    private byte[] data;
    private String fileName;
    private String mimeTpe;
}
