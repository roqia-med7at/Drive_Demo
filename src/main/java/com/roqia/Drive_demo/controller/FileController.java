package com.roqia.Drive_demo.controller;

import com.roqia.Drive_demo.dto.request.MoveOrCopyFileRequest;
import com.roqia.Drive_demo.dto.request.RenameFileRequest;
import com.roqia.Drive_demo.dto.response.FileResponse;
import com.roqia.Drive_demo.dto.response.SuccessResponse;
import com.roqia.Drive_demo.model.File;
import com.roqia.Drive_demo.security.jwt.model.UserPrincipal;
import com.roqia.Drive_demo.service.FileService;
import com.roqia.Drive_demo.service.ItemService;
import com.roqia.Drive_demo.utility.FileUtility;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Validated
@RequestMapping("/api/file")
public class FileController {
@Autowired
private FileService fileService;
    @Autowired
    private ItemService itemService;
    @PostMapping("/upload")
    public ResponseEntity<SuccessResponse> upload_file(Authentication authentication, @NotNull @RequestParam MultipartFile multipartFile,@NotNull @RequestParam int folder_id ){
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        int user_id = userPrincipal.getUserId();
    FileResponse fileResponse = fileService.upload_file(user_id,multipartFile,folder_id);
    Map<String,Object> data = new HashMap<>();
    data.put("file id",fileResponse.getFile_id());
    data.put("parent_file id",fileResponse.getFolder_id());
    data.put("name",fileResponse.getFile());
    data.put("size",fileResponse.getFileSize());
    data.put("path",fileResponse.getFile_path());
    data.put("Created At",fileResponse.getCreatedAt());
     SuccessResponse response = new SuccessResponse("file Uploaded successfully",data);
     return ResponseEntity.ok(response);
    }
    @PutMapping("/rename")
    public ResponseEntity<SuccessResponse> rename_file(Authentication authentication, @RequestBody RenameFileRequest request){
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        int user_id = userPrincipal.getUserId();
       File updated = fileService.rename_file(user_id,request.getFile_id(),request.getNew_name());
        Map<String,Object> data = new HashMap<>();
        data.put("file_id",updated.getId());
        data.put("file_new_name",updated.getName());
        SuccessResponse response = new SuccessResponse("file renamed successfully",data);
        return ResponseEntity.ok(response);
    }
    @PutMapping("/move")
    public ResponseEntity<SuccessResponse> move_file(Authentication authentication, @Valid @RequestBody MoveOrCopyFileRequest request){
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        int user_id = userPrincipal.getUserId();
        String new_path = fileService.move_file(user_id,request.getFile_id(),request.getNew_folder_id());
        Map<String,Object> data = new HashMap<>();
        data.put("new_path",new_path);
        SuccessResponse response = new SuccessResponse("file moved successfully",new_path);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/remove/{file_id}")
    public ResponseEntity<SuccessResponse> remove_file(Authentication authentication,@PathVariable int file_id){
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        int user_id = userPrincipal.getUserId();
        fileService.remove_file(file_id,user_id);
        SuccessResponse response = new SuccessResponse("file removed successfully",null);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/copy")
    public ResponseEntity<SuccessResponse> copy_file (Authentication authentication,@RequestBody MoveOrCopyFileRequest request){
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        int user_id = userPrincipal.getUserId();
        Map<String,String> map = fileService.copy_file(request.getFile_id(),request.getNew_folder_id(),user_id);
        SuccessResponse response = new SuccessResponse("file coped successfully",map);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/download/{file_id}")
    public ResponseEntity<Resource> download_file(Authentication authentication, @PathVariable int file_id){
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        int user_id = userPrincipal.getUserId();
       File file = (File) itemService.getItem(user_id,file_id);
       Resource resource = fileService.download_file(user_id, file_id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                .contentType(MediaType.parseMediaType(FileUtility.getMimeTypeByFileName(file.getName())))
                .contentLength(file.getFileSize())
                .body(resource);
    }
    @GetMapping("/files")
    public ResponseEntity<SuccessResponse>listAllFiles(Authentication authentication){
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        int user_id = userPrincipal.getUserId();
       List<FileResponse> fileResponses = fileService.getAllFiles(user_id);
       SuccessResponse response = new SuccessResponse<>("All Files: ",fileResponses);
       return ResponseEntity.ok(response);
    }
    @GetMapping("/files/{id}")
    public ResponseEntity<SuccessResponse>getFileInfo(Authentication authentication,@PathVariable int id){
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        int user_id = userPrincipal.getUserId();
       FileResponse fileResponse = fileService.get_file_info(user_id,id);
       SuccessResponse response = new SuccessResponse<>("File info : ",fileResponse);
       return ResponseEntity.ok(response);
    }

}
