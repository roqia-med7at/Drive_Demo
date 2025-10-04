package com.roqia.Drive_demo.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FolderResponseDto {
     private int folder_id;
     private int parent_folder_id;
     private String folder_name;
}
