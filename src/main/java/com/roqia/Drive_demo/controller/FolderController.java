package com.roqia.Drive_demo.controller;

import com.roqia.Drive_demo.dto.request.FolderRequestDto;
import com.roqia.Drive_demo.dto.request.MoveOrCopyFolderRequest;
import com.roqia.Drive_demo.dto.request.RenameFolderRequest;
import com.roqia.Drive_demo.dto.response.FolderResponseDto;
import com.roqia.Drive_demo.dto.response.SuccessResponse;
import com.roqia.Drive_demo.model.Folder;
import com.roqia.Drive_demo.security.jwt.model.UserPrincipal;
import com.roqia.Drive_demo.service.FolderService;
import com.roqia.Drive_demo.service.ItemService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/folder")
@Validated
public class FolderController {
    @Autowired
    private FolderService folderService;
    @Autowired
    private ItemService itemService;
    @PostMapping("/create")
    public ResponseEntity<FolderResponseDto> create_folder(Authentication authentication, @RequestBody FolderRequestDto folderRequestDto){
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        int user_id = userPrincipal.getUserId();
        String folderName = folderRequestDto.getFolder_name();
        int parentFolderId = folderRequestDto.getParent_folder_id();
       int folder_id = folderService.create_folder(user_id,parentFolderId,folderName);
        FolderResponseDto folderResponseDto = new FolderResponseDto().builder()
                .folder_id(folder_id)
                .folder_name(folderName)
                .parent_folder_id(parentFolderId)
                .build();
         return ResponseEntity.ok(folderResponseDto);

    }
    @PostMapping("/rename")
    public ResponseEntity<?> rename_folder(Authentication authentication, @RequestBody RenameFolderRequest renameFolderRequest){
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        int user_id = userPrincipal.getUserId();
        folderService.rename_folder(user_id,renameFolderRequest.getFolder_id(),renameFolderRequest.getNew_folder_name());
        return ResponseEntity.ok("Folder renamed successfully");
    }
    @DeleteMapping("/delete/{folder_id}")
    public ResponseEntity<?> remove_folder(Authentication authentication,@PathVariable int folder_id){
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        int user_id = userPrincipal.getUserId();
        folderService.remove_folder(user_id,folder_id);
        return ResponseEntity.ok("Folder removed successfully");
    }
    @PostMapping("/move")
    public ResponseEntity<FolderResponseDto> move_folder(Authentication authentication, @RequestBody MoveOrCopyFolderRequest moveFolderRequest){
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        int user_id = userPrincipal.getUserId();
        int folder_id = moveFolderRequest.getFolder_id();
        Folder folder = (Folder) itemService.getItem(user_id,folder_id);
        int new_parent_folder_id = moveFolderRequest.getNew_parent_folder_id();
        folderService.move_folder(user_id,folder_id,new_parent_folder_id);
        FolderResponseDto folderResponseDto = new FolderResponseDto().builder()
                .folder_id(folder_id)
                .folder_name(folder.getName())
                .parent_folder_id(folder.getParentFolder().getId())
                .build();
        return ResponseEntity.ok(folderResponseDto);
    }

    @PostMapping("/copy")
    public ResponseEntity<SuccessResponse> copy_folder(Authentication authentication,@Valid @RequestBody MoveOrCopyFolderRequest request){
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        int user_id = userPrincipal.getUserId();
        folderService.copy_folder(user_id,request.getFolder_id(),request.getNew_parent_folder_id());
        SuccessResponse successResponse = new SuccessResponse<>("Folder coped successfully",null);
        return ResponseEntity.ok(successResponse);
    }

    @PostMapping("/upload")
    public ResponseEntity<SuccessResponse> upload_folder(Authentication authentication, @NotNull @RequestParam int parentFolderId, @NotBlank @RequestParam String folderName, @NotNull @RequestParam List<MultipartFile> files){
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        int user_id = userPrincipal.getUserId();
        folderService.upload_folder(user_id,parentFolderId,folderName,files);
        SuccessResponse successResponse = new SuccessResponse<>("Folder uploaded successfully",null);
        return ResponseEntity.ok(successResponse);
    }
    @GetMapping("/download/{folder_id}")
    public void download_folder(Authentication authentication, @PathVariable int folder_id, HttpServletResponse response){
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        int user_id = userPrincipal.getUserId();
        Folder folder = (Folder) itemService.getItem(user_id,folder_id);
        try {
            folderService.download_folder(user_id,folder_id,response.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        response.setContentType("application/zip");
        response.setHeader("Content-Disposition", "attachment; filename=\""+folder.getName()+".zip\"");
    }
    @GetMapping("/root")
    public ResponseEntity<SuccessResponse>getRootContents(Authentication authentication){
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        int user_id = userPrincipal.getUserId();
        Map<String,List<String>> map = folderService.getRootFolderContents(user_id);
        SuccessResponse response = new SuccessResponse<>("Root Folder Contents : ",map);
        return ResponseEntity.ok(response);
        }
    }

