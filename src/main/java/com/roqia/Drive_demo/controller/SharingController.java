package com.roqia.Drive_demo.controller;

import com.roqia.Drive_demo.dto.request.ShareWithRequest;
import com.roqia.Drive_demo.dto.response.CopyLinkResponse;
import com.roqia.Drive_demo.dto.response.ShareWithResponse;
import com.roqia.Drive_demo.dto.response.SharedItemResponse;
import com.roqia.Drive_demo.dto.response.SuccessResponse;
import com.roqia.Drive_demo.security.jwt.model.UserPrincipal;
import com.roqia.Drive_demo.service.SharingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/share")
public class SharingController {
    @Autowired
    private SharingService sharingService;

    @GetMapping("/getAll")
    public ResponseEntity<SuccessResponse> getALlItemsShared(Authentication authentication){
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        int user_id = userPrincipal.getUserId();

        List<SharedItemResponse>responses = sharingService.getAllSharedItems(user_id);
        SuccessResponse response = new SuccessResponse<>("All Shared Items Returned",responses);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/copy/link/{itemId}")
    public ResponseEntity<SuccessResponse> copyOrGetLink(Authentication authentication, @PathVariable int itemId){
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        int user_id = userPrincipal.getUserId();

        CopyLinkResponse linkResponse = sharingService.copyOrGetLink(user_id,itemId);
        SuccessResponse response = new SuccessResponse<>("Link Is Coppied Successfully",linkResponse);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/with")
    public ResponseEntity<SuccessResponse>shareItemWith(Authentication authentication, @Valid @RequestBody ShareWithRequest request){
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        int user_id = userPrincipal.getUserId();

        ShareWithResponse shareWithResponse = sharingService.share_with(user_id,request.getItemId(),request.getEmail(),request.getAccess());
        SuccessResponse response = new SuccessResponse<>("Item Shared Successfully",shareWithResponse);
        return ResponseEntity.ok(response);
    }
}
