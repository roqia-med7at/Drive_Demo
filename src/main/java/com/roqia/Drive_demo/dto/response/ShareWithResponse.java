package com.roqia.Drive_demo.dto.response;

import com.roqia.Drive_demo.Enum.Access;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShareWithResponse {
    private String sharedBy;
    private String sharedWith;
    private Access accessLevel;
    private String sharedItem_name;
    private String sharedItem_type;
    private String sharedItem_path;

}
