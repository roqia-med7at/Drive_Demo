package com.roqia.Drive_demo.service;

import com.roqia.Drive_demo.Enum.Access;
import com.roqia.Drive_demo.Enum.Feature;
import com.roqia.Drive_demo.dto.response.FileResponse;
import com.roqia.Drive_demo.dto.response.FolderResponseDto;
import com.roqia.Drive_demo.error.customExceptions.AccessDeniedException;
import com.roqia.Drive_demo.error.customExceptions.RecordNotFoundException;
import com.roqia.Drive_demo.model.*;
import com.roqia.Drive_demo.repo.AccessorRepo;
import com.roqia.Drive_demo.repo.FolderRepo;
import com.roqia.Drive_demo.repo.UserRepo;
import com.roqia.Drive_demo.utility.FileUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class FolderService {
    @Autowired
    private FolderRepo folderRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private FileService fileService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private ValidationService validationService;
    @Autowired
    private StorageService storageService;
    @Autowired
    private AccessorRepo accessorRepo;


    public Folder create_root_folder(int user_id) {

        String folderName = "User" + "_" + user_id;
        User owner = userRepo.findById(user_id).orElseThrow(()->new RecordNotFoundException("No such user found with id"+user_id));

       Folder savedFolder = saveFolderToDB(folderName,null,owner,LocalDateTime.now());
        owner.setRootFolder(savedFolder);
        Accessor accessor = new Accessor();
        accessor.setUser(savedFolder.getOwner());
        accessor.setItem(savedFolder);
        accessor.setAccess(Access.OWNER);
        accessorRepo.save(accessor);
        storageService.createOrGetFolder(folderName,"storage");
        return savedFolder;
    }
    public int create_folder(Integer user_id, Integer parent_folder_id, String folder_name) {

        User owner =userRepo.findById(user_id).orElseThrow(()->new RecordNotFoundException("No such user found with id"+user_id));

        Folder parent_folder = (Folder) itemService.getItem(user_id,parent_folder_id);

        if (!validationService.hasAccess(user_id,parent_folder.getId(), Feature.CREATE)) {
            throw new AccessDeniedException("You have no access to create folder here");
        }
        String parentStringPath = itemService.get_item_path(parent_folder);

        Folder savedFolder = saveFolderToDB(folder_name,parent_folder,owner,LocalDateTime.now());
        Accessor accessor = new Accessor();
        accessor.setUser(savedFolder.getOwner());
        accessor.setItem(savedFolder);
        accessor.setAccess(Access.OWNER);
        accessorRepo.save(accessor);
        storageService.createOrGetFolder(folder_name,parentStringPath);

        return  savedFolder.getId();
    }



    public void rename_folder(int user_id, int folder_id, String newFolderName) {
        if (!validationService.hasAccess(user_id,folder_id, Feature.EDIT)) {
            throw new AccessDeniedException("You have no access to rename that folder");
        }
        validationService.validateUser(user_id);
        Folder folder = (Folder) itemService.getItem(user_id,folder_id);
        String oldPathStr = itemService.get_item_path(folder);
        folder.setName(newFolderName);
        folderRepo.save(folder);

        String newPathStr = itemService.get_item_path(itemService.getItem(user_id,folder_id));
        storageService.moveItem(oldPathStr,newPathStr);

    }
@Transactional
    public void remove_folder(int user_id, int folder_id) {
        if (!validationService.hasAccess(user_id,folder_id, Feature.EDIT)) {
            throw new AccessDeniedException("You have no access to delete that folder");
        }
        Folder folder = (Folder) itemService.getItem(user_id,folder_id);
        String stringPath = itemService.get_item_path(folder);

        storageService.deleteFolder(stringPath);

        fileService.deleteFilesInFolderAndSubFolders(folder_id);

        folderRepo.deleteFolderAndSubfolders(folder_id);
    }

    public String move_folder(int user_id, int folder_id, int new_parent_folder_id) {
        if (!validationService.hasAccess(user_id,folder_id, Feature.EDIT)) {
            throw new AccessDeniedException("You have no access to move that folder");
        }
        Folder folder = (Folder) itemService.getItem(user_id,folder_id);
        Folder newParent = (Folder) itemService.getItem(user_id,new_parent_folder_id);
        String source =itemService.get_item_path(folder);

        folder.setParentFolder(newParent);
        folderRepo.save(folder);

        String destination = itemService.get_item_path(folder);

        storageService.moveItem(source,destination);
        return destination;
    }
public void upload_folder(Integer user_id, Integer parent_folder_id, String folder_name, List<MultipartFile>files){
    if (!validationService.hasAccess(user_id,parent_folder_id, Feature.UPLOAD)) {
        throw new AccessDeniedException("You have no access to upload files here");
    }
     for(MultipartFile multipartFile :files){
         create_subFoldersForAFile(user_id,parent_folder_id,folder_name,multipartFile);
       }

}
 private  void create_subFoldersForAFile(Integer user_id, Integer parent_folder_id, String folder_name, MultipartFile multipartFile) {
    Folder parent_folder = (Folder) itemService.getItem(user_id,parent_folder_id);
    User owner = userRepo.findById(user_id).orElseThrow(()->new RecordNotFoundException("No such user found with id"+user_id));

     long fileSize = multipartFile.getSize();
     String[] parts = multipartFile.getOriginalFilename().split("/");

     Folder currentParent = parent_folder;

     for (int i = 0; i < parts.length - 1; i++) {
         String folderName = parts[i];
         Folder folder = folderRepo.findByNameAndParentFolder(folderName, currentParent).orElseGet(()->{return null;});
         if (folder == null) {
             folder = saveFolderToDB(folderName,currentParent,owner,LocalDateTime.now());

            String parentPathStr = itemService.get_item_path(currentParent);

             storageService.createOrGetFolder(folderName,parentPathStr);
         }
         currentParent = folder;
     }
     String fileName = parts[parts.length - 1];
     String fileExtension = FileUtility.getFileExtension(fileName);

     File fileEntity = fileService.saveFileToDB(fileName, currentParent, fileExtension, owner, fileSize,LocalDateTime.now());
     fileService.saveFileToStorage(fileEntity, fileEntity.getFolder().getId(), user_id, multipartFile);

 }
    public void download_folder(int userId,int folderId, OutputStream output) {
        if (!validationService.hasAccess(userId,folderId, Feature.DOWNLOAD)) {
            throw new AccessDeniedException("You have no access to download that folder");
        }
       validationService.validateUser(userId);
       Item item = itemService.getItem(userId,folderId);
        if (! (item instanceof Folder folder)){
            throw new IllegalArgumentException("Item with id " + folderId + " is not a folder");
        }
        String stringPath = itemService.get_item_path(folder);

        storageService.downloadFolder(stringPath,output);

    }
    public void copy_folder(int user_id, int folder_id, int new_parent_folder_id){
        if (!validationService.hasAccess(user_id,folder_id, Feature.EDIT)) {
            throw new AccessDeniedException("You have no access to copy that folder");
        }
       validationService.validateUser(user_id);
        Folder old_folder = (Folder) itemService.getItem(user_id,folder_id);
        String oldPathStr = itemService.get_item_path(old_folder);

        Folder new_parent = (Folder) itemService.getItem(user_id,new_parent_folder_id);

        Folder new_folder = new Folder();
        new_folder.setName(old_folder.getName());
        new_folder.setCreatedAt(old_folder.getCreatedAt());
        new_folder.setOwner(old_folder.getOwner());
        new_folder.setParentFolder(new_parent);
        folderRepo.save(new_folder);

        String newPathStr =itemService.get_item_path(new_folder);

       storageService.copyFolder(oldPathStr,newPathStr);

    }
    public Map<String,List<String>> getFolderContents(int userId,int folderId){
       validationService.validateUser(userId);
        Item item = itemService.getItem(userId,folderId);

        String strPath = itemService.get_item_path(item);

        return storageService.getFolderContents(strPath);
    }
    private Folder saveFolderToDB(String name,Folder parentFolder,User owner,LocalDateTime createdAt){
        Folder folder = new Folder();

        folder.setName(name);
        folder.setOwner(owner);
        folder.setParentFolder(parentFolder);
        folder.setCreatedAt(createdAt);
       return folderRepo.save(folder);
    }
    public Map<String,List<String>> getRootFolderContents(int userId){

       User user = userRepo.findById(userId).orElseThrow(()->new RecordNotFoundException("No such user found with id"+userId));
       return getFolderContents(userId,user.getRootFolder().getId());

    }


}