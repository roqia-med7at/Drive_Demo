package com.roqia.Drive_demo.service;

import com.roqia.Drive_demo.Enum.Access;
import com.roqia.Drive_demo.Enum.Feature;
import com.roqia.Drive_demo.dto.response.FileResponse;
import com.roqia.Drive_demo.dto.response.ReadFileResponse;
import com.roqia.Drive_demo.error.customExceptions.AccessDeniedException;
import com.roqia.Drive_demo.error.customExceptions.RecordNotFoundException;
import com.roqia.Drive_demo.mapper.FileResponseMapper;
import com.roqia.Drive_demo.model.*;
import com.roqia.Drive_demo.repo.AccessorRepo;
import com.roqia.Drive_demo.repo.FileRepo;
import com.roqia.Drive_demo.repo.UserRepo;
import com.roqia.Drive_demo.utility.FileUtility;
import com.roqia.Drive_demo.utility.FileUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class FileService {
    @Autowired
    private FileRepo fileRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private AccessorRepo accessorRepo;
    @Autowired
    private FileResponseMapper responseMapper;
    @Autowired
    private ItemService itemService;
    @Autowired
    private ValidationService validationService;
    @Autowired
    private StorageService storageService;


    public FileResponse upload_file(int user_id, MultipartFile multipartFile, int folder_id) {
        if (!validationService.hasAccess(user_id, folder_id, Feature.UPLOAD)) {
            throw new AccessDeniedException("You have no access to upload file here");
        }
        User owner = userRepo.findById(user_id).orElseThrow(() -> new RecordNotFoundException("No such user found with id" + user_id));

        String extension = FileUtility.getFileExtension(multipartFile.getOriginalFilename());
        long maxSize = 10 * 1024 * 1024;
        if (multipartFile.getSize() > maxSize) {
            throw new IllegalArgumentException("File size exceeds max size :10MB");
        }
        Folder folder = (Folder) itemService.getItem(user_id,folder_id);

        File file = saveFileToDB(getFileName(multipartFile.getOriginalFilename()), folder, extension, owner, multipartFile.getSize(), LocalDateTime.now());

        Accessor accessor = new Accessor();
        accessor.setAccess(Access.OWNER);
        accessor.setUser(owner);
        accessor.setItem(file);
        accessorRepo.save(accessor);

        String pathString = saveFileToStorage(file, folder_id, user_id, multipartFile);

        FileResponse response = responseMapper.mapToDto(file,itemService);
        response.setFile_path(pathString);
        return response;

    }


    public File rename_file(int user_id, int file_id, String newName) {

        validationService.validateUser(user_id);
        File file = (File) itemService.getItem(user_id, file_id);
        int itemId;
        if (file.getFolder() != null) {
            itemId = file.getFolder().getId();
        } else {
            itemId = file.getId();
        }
        if (!validationService.hasAccess(user_id, itemId, Feature.EDIT)) {
            throw new AccessDeniedException("You have no access to rename that file");
        }
        String oldPathStr =itemService.get_item_path(file);

        file.setName(newName);
        File updatedFile = fileRepo.save(file);

        String newPathStr = itemService.get_item_path(file);

        storageService.moveItem(oldPathStr, newPathStr);

        return updatedFile;
    }

    public String move_file(int user_id, int file_id, int folder_id) {

        validationService.validateUser(user_id);
        File file =  (File) itemService.getItem(user_id, file_id);
        int itemId;
        if (file.getFolder() != null) {
            itemId = file.getFolder().getId();
        } else {
            itemId = file.getId();
        }
        if (!validationService.hasAccess(user_id, itemId, Feature.EDIT)) {
            throw new AccessDeniedException("You have no access to move that file");
        }
        String oldPathStr =itemService.get_item_path(file);

        Folder new_parent_folder = (Folder) itemService.getItem(user_id,folder_id);
        file.setFolder(new_parent_folder);

        String newPathStr = itemService.get_item_path(file);

        storageService.moveItem(oldPathStr, newPathStr);
        return newPathStr;

    }

    public void remove_file(int file_id, int user_id) {

        validationService.validateUser(user_id);
        File file =  (File) itemService.getItem(user_id, file_id);
        int itemId;
        if (file.getFolder() != null) {
            itemId = file.getFolder().getId();
        } else {
            itemId = file.getId();
        }
        if (!validationService.hasAccess(user_id, itemId, Feature.EDIT)) {
            throw new AccessDeniedException("You have no access to remove that file");
        }
        String pathStr =itemService.get_item_path(file);

        storageService.deleteFile(pathStr);

        fileRepo.delete(file);

    }

    public Map<String, String> copy_file(int file_id, int folder_id, int user_id) {

        validationService.validateUser(user_id);
        Folder second_folder = (Folder) itemService.getItem(user_id,folder_id);
        File file =  (File) itemService.getItem(user_id, file_id);
        int itemId;
        if (file.getFolder() != null) {
            itemId = file.getFolder().getId();
        } else {
            itemId = file.getId();
        }
        if (!validationService.hasAccess(user_id, itemId, Feature.COPY)) {
            throw new AccessDeniedException("You have no access to copy that file");
        }
        String oldPathStr =itemService.get_item_path(file);

        File copied_file = saveFileToDB(file.getName(), second_folder, file.getFileExtension(), file.getOwner(), file.getFileSize(), file.getCreatedAt());

        String newPathStr =itemService.get_item_path(copied_file);

        storageService.copyFile(oldPathStr, newPathStr);
        Map<String, String> map = new HashMap<>();
        map.put("first_path", oldPathStr);
        map.put("second_path", newPathStr);

        return map;

    }

    public Resource download_file(int userId, int fileId) {

        validationService.validateUser(userId);
        Item item = itemService.getItem(userId, fileId);
        if (!(item instanceof File file)) {
            throw new IllegalArgumentException("Item with id " + fileId + " is not a file");
        }
        int itemId;
        if (file.getFolder() != null) {
            itemId = file.getFolder().getId();
        } else {
            itemId = file.getId();
        }
        if (!validationService.hasAccess(userId, itemId, Feature.DOWNLOAD)) {
            throw new AccessDeniedException("You have no access to download that file");
        }
        String pathStr =itemService.get_item_path(file);
        Path path = Paths.get(pathStr);
        return new FileSystemResource(path);
    }

    public ReadFileResponse readFile(int userId, int fileId) {
        validationService.validateUser(userId);
        validationService.validateItem(userId, fileId);
        Item item = itemService.getItem(userId, fileId);
        if (!(item instanceof File file)) {
            throw new IllegalArgumentException("Item with id " + fileId + " is not a file");
        }
        int itemId;
        if (file.getFolder() != null) {
            itemId = file.getFolder().getId();
        } else {
            itemId = file.getId();
        }
        if (!validationService.hasAccess(userId, itemId, Feature.READ)) {
            throw new AccessDeniedException("You have no access to read that file");
        }
        String strPath =itemService.get_item_path(file);
        byte[] fileData =  storageService.readFile(strPath);;

        String fileMimeType = FileUtility.getMimeTypeByFileName(file.getName());
        ReadFileResponse response =  ReadFileResponse.builder()
                .fileName(file.getName())
                .data(fileData)
                .mimeTpe(fileMimeType)
                .build();

        return response;
    }

//    private String get_file_path(File file) {
//        List<String> path = new ArrayList<>();
//        String fileName = file.getName() + "_" + file.getVersion() + "." + file.getFileExtension();
//        if (file != null) {
//            path.add(fileName);
//            Folder folder = file.getFolder();
//            while (folder != null) {
//                path.add(folder.getName());
//                folder = folder.getParentFolder();
//
//            }
//            Collections.reverse(path);
//            return "storage/" + String.join("/", path);
//        } else {
//            return "";
//        }
//    }

    public FileResponse get_file_info(int user_id, int file_id) {
       File file =  (File) itemService.getItem(user_id, file_id);
      return responseMapper.mapToDto(file,itemService);
    }

    public Integer getFileVersion(String fileName, int folder_id) {
        Integer lastVersion = fileRepo.findMaxVersionByNameAndFolder_Id(fileName, folder_id);
        return (lastVersion != null) ? lastVersion + 1 : 1;
    }

    public String getFileName(String fullName) {
        int indx = fullName.lastIndexOf(".");
        return fullName.substring(0, indx);
    }

    public File saveFileToDB(String fileName, Folder folder, String fileExtension, User owner, long fileSize, LocalDateTime dateTime) {
        long maxSize = 10 * 1024 * 1024;
        if (fileSize > maxSize) {
            throw new IllegalArgumentException("File size exceeds max size :10MB");
        }
        File file_entity = new File();
        file_entity.setFileExtension(fileExtension);
        file_entity.setFileSize(fileSize);
        file_entity.setOwner(owner);
        file_entity.setCreatedAt(dateTime);
        file_entity.setFolder(folder);

        int version = getFileVersion(fileName, folder.getId());
        file_entity.setVersion(version);
        file_entity.setName(fileName);
        return fileRepo.save(file_entity);
    }

    public String saveFileToStorage(File file, int folder_id, int user_id, MultipartFile multipartFile) {
        String pathString = itemService.get_item_path(file);
        String parentPathString = itemService.get_item_path((Folder) itemService.getItem(user_id,folder_id));

        storageService.saveFileToStorage(pathString, parentPathString, multipartFile);

        return pathString;
    }

    public List<File> getFolderFiles(int userId, int folder_id) {
        Item item = itemService.getItem(userId, folder_id);
        if (!(item instanceof Folder folder)) {
            throw new IllegalArgumentException("Item with id " + folder_id + " is not a folder");
        }
        return fileRepo.findAllByFolder_Id(folder.getId());
    }
    public List<FileResponse>getAllFiles(int userId){
        validationService.validateUser(userId);
        List<File>files = fileRepo.findAll();

       return responseMapper.mapToDtos(files,itemService);


    }
    public void deleteFilesInFolderAndSubFolders(int folderId){
        fileRepo.deleteFilesInFolderAndSubfolders(folderId);
    }
}
