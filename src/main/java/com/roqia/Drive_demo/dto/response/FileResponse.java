package com.roqia.Drive_demo.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class FileResponse {
        private String file;
        private long fileSize;
        private LocalDateTime createdAt;
        private int folder_id;
        private int file_id;
        private String file_path;
    }


