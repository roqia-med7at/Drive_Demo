package com.roqia.Drive_demo.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;
@Getter
@Setter
public class AccessLinkResponse {
    private String item_name;
    private Map<String, List<String>> folder_contents;
    private byte[] file_data;
    private boolean allowed;
    private String mimeType;
}
