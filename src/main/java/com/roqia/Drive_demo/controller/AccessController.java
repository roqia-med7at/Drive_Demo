package com.roqia.Drive_demo.controller;

import com.roqia.Drive_demo.dto.request.AccessLinkRequest;
import com.roqia.Drive_demo.dto.request.ChangePermissionRequest;
import com.roqia.Drive_demo.dto.request.ChangeUserAccessRequest;
import com.roqia.Drive_demo.dto.request.DeleteUserAccessRequest;
import com.roqia.Drive_demo.dto.response.AccessLinkResponse;
import com.roqia.Drive_demo.dto.response.AccessorsResponse;
import com.roqia.Drive_demo.dto.response.SuccessResponse;
import com.roqia.Drive_demo.Enum.Permission;
import com.roqia.Drive_demo.security.jwt.model.UserPrincipal;
import com.roqia.Drive_demo.service.AccessService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/access")
public class AccessController {
    @Autowired
    private AccessService accessService;

    @GetMapping("/get/{itemId}")
    public ResponseEntity<SuccessResponse> get_general_permission(Authentication authentication , @PathVariable int itemId) {
        UserPrincipal userPrincipal = (UserPrincipal)authentication.getPrincipal();
        int userId = userPrincipal.getUserId();
        Permission permission = accessService.get_general_permission(userId,itemId);
        SuccessResponse response = new SuccessResponse<>("General Permission Returned",permission);
        return ResponseEntity.ok(response);
    }
    @PutMapping("/general/change")
    public ResponseEntity<SuccessResponse> change_general_permission(Authentication authentication, @Valid @RequestBody ChangePermissionRequest request){
        UserPrincipal userPrincipal = (UserPrincipal)authentication.getPrincipal();
        int userId = userPrincipal.getUserId();
        Permission newPermission = accessService.change_general_permission(userId,request.getItemId(),request.getNewPermission());
        Map<String,Permission>permissionMap = new HashMap<>();
        permissionMap.put("New permission",newPermission);
        SuccessResponse response = new SuccessResponse<>("General Permission Changed Successfully",permissionMap);
        return ResponseEntity.ok(response);
    }
      @GetMapping("get/hasAccess/{itemId}")
    public ResponseEntity<SuccessResponse> get_people_with_access(Authentication authentication,@PathVariable int itemId){
          UserPrincipal userPrincipal = (UserPrincipal)authentication.getPrincipal();
          int userId = userPrincipal.getUserId();

          List<AccessorsResponse>accessors = accessService.get_people_with_access(userId,itemId);
          SuccessResponse response = new SuccessResponse<>("People Who Has Access Returned",accessors);
          return ResponseEntity.ok(response);
      }

      @PostMapping("/link")
    public ResponseEntity<?> access_link(@Valid @RequestBody AccessLinkRequest request){
       AccessLinkResponse linkResponse = accessService.access_link(request.getEmail(),request.getToken());
        if(linkResponse.getFile_data()!=null){
            ByteArrayResource resource = new ByteArrayResource(linkResponse.getFile_data());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + linkResponse.getItem_name()+ "\"")
                    .contentType(MediaType.parseMediaType(linkResponse.getMimeType()))
                    .contentLength(linkResponse.getFile_data().length)
                    .body(resource);
        }else {
            return ResponseEntity.ok(linkResponse);
        }
      }
      @PutMapping("/change")
    public ResponseEntity<SuccessResponse> changeUserAccess(Authentication authentication, @Valid @RequestBody ChangeUserAccessRequest request){
          UserPrincipal userPrincipal = (UserPrincipal)authentication.getPrincipal();
          int ownerId = userPrincipal.getUserId();
         accessService.changeUserAccess(ownerId,request.getUserId(),request.getItemId(),request.getNewAccess());
         Map<String, Object>map=new HashMap<>();
         map.put("UserId",request.getUserId());
         map.put("NewAccess",request.getNewAccess());

         SuccessResponse response = new SuccessResponse<>("Access changed successfully",map);
         return ResponseEntity.ok(response);
      }
      @DeleteMapping("/remove")
    public ResponseEntity<SuccessResponse> removeUserAccess(Authentication authentication, @Valid @RequestBody DeleteUserAccessRequest request){
          UserPrincipal userPrincipal = (UserPrincipal)authentication.getPrincipal();
          int ownerId = userPrincipal.getUserId();
          accessService.removeUserAccess(ownerId,request.getUserId(),request.getItemId());
          SuccessResponse response = new SuccessResponse<>(
                  "Access For User : "+request.getUserId()+"For Item : "+request.getItemId()+"Deleted Successfully",null);
          return ResponseEntity.ok(response);

      }
}
