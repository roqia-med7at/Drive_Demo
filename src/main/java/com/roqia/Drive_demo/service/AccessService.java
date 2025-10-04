package com.roqia.Drive_demo.service;

import com.roqia.Drive_demo.Enum.Access;
import com.roqia.Drive_demo.Enum.Feature;
import com.roqia.Drive_demo.Enum.Permission;
import com.roqia.Drive_demo.dto.response.AccessLinkResponse;
import com.roqia.Drive_demo.dto.response.AccessorsResponse;
import com.roqia.Drive_demo.error.customExceptions.AccessDeniedException;
import com.roqia.Drive_demo.error.customExceptions.RecordNotFoundException;
import com.roqia.Drive_demo.model.*;
import com.roqia.Drive_demo.repo.AccessorRepo;
import com.roqia.Drive_demo.repo.SharedItemRepo;
import com.roqia.Drive_demo.repo.SharedLinkRepo;
import com.roqia.Drive_demo.utility.FileUtility;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class AccessService {
    private final AccessorRepo accessorRepo;
    private final SharedLinkRepo sharedLinkRepo;
    private final UserService userService;
    private final RequestService requestService;
    private final FileService fileService;
    private final FolderService folderService;
    private final ValidationService validationService;
    private final SharedItemRepo sharedItemRepo;

    public AccessService(
            AccessorRepo accessorRepo, SharedLinkRepo sharedLinkRepo, UserService userService, RequestService requestService, FileService fileService, FolderService folderService, ValidationService validationService1, SharedItemRepo sharedItemRepo) {
        this.accessorRepo = accessorRepo;
        this.sharedLinkRepo = sharedLinkRepo;
        this.userService = userService;
        this.requestService = requestService;
        this.fileService = fileService;
        this.folderService = folderService;
        this.validationService = validationService1;
        this.sharedItemRepo = sharedItemRepo;
    }


    public Permission get_general_permission(int owner_id, int item_id) {
        validationService.validateItem(owner_id, item_id);
        return sharedLinkRepo.findBySharedItem_Id(item_id)
                .map(SharedLink::getPermission)
                .orElseThrow(() -> new RuntimeException("No shared link exists for this item"));
    }

    public Permission change_general_permission(int owner_id, int item_id, Permission permission) {
        if (!validationService.hasAccess(owner_id, item_id, Feature.MANAGE)) {
            throw new AccessDeniedException("You have no access to manage access for that item");
        }
        validationService.validateUser(owner_id);
        validationService.validateItem(owner_id, item_id);

        return sharedLinkRepo.findBySharedItem_Id(item_id)
                .map(link -> {
                    link.setPermission(permission);
                    return sharedLinkRepo.save(link).getPermission();
                })
                .orElseThrow(() -> new RuntimeException("No shared link exists for this item"));

    }

    public List<AccessorsResponse> get_people_with_access(int owner_id, int item_id) {
        validationService.validateSharedItem(item_id, owner_id);
        List<Accessor> accessors = accessorRepo.findAllByItem_Id(item_id);
        List<AccessorsResponse> accessorsResponses = new ArrayList<>();
        for (Accessor accessor : accessors) {
            AccessorsResponse response = new AccessorsResponse();
            response.setName(accessor.getUser().getName());
            response.setEmail(accessor.getUser().getEmail());
            response.setAccess(accessor.getAccess());
            accessorsResponses.add(response);
        }
        return accessorsResponses;

    }

    public void changeUserAccess(int ownerId, int userId, int itemId, Access newAccess) {
        validationService.validateUser(ownerId);
        validationService.validateUser(userId);
        validationService.validateItem(ownerId, itemId);
        Accessor accessor = accessorRepo.findByItem_IdAndUser_Id(itemId, userId).orElseThrow(() -> new RecordNotFoundException("No such accessor found with these data"));
        accessor.setAccess(newAccess);
        accessorRepo.save(accessor);
    }
    public void removeUserAccess(int ownerId, int userId, int itemId){
        validationService.validateUser(ownerId);
        validationService.validateUser(userId);
        validationService.validateItem(ownerId, itemId);
        Accessor accessor = accessorRepo.findByItem_IdAndUser_Id(itemId, userId).orElseThrow(() -> new RecordNotFoundException("No such accessor found with these data"));
        Item item = accessor.getItem();

        SharedItem sharedItem = sharedItemRepo.findBySharedItem_IdAndSharedWith_Id(item.getId(), userId).orElseThrow(()->new RecordNotFoundException("This file is not found"));
        sharedItemRepo.delete(sharedItem);

        accessorRepo.delete(accessor);
    }

    public AccessLinkResponse access_link(String email, String token) {

        User user = userService.searchByEmail(email);
        SharedLink sharedLink = validationService.validateSharedLink(token);

            Item item = sharedLink.getSharedItem();
            User owner = item.getOwner();
            int ownerId = owner.getId();
            Accessor accessor = accessorRepo.findByItem_IdAndUser_Id(item.getId(), user.getId()).orElse(null);
            boolean hasAccess = accessor != null &&
                    (sharedLink.getPermission() == Permission.ANYONE ||
                            sharedLink.getPermission() == Permission.SHARED_WITH_ONLY);

            if (hasAccess)
             {
                if (item instanceof File file) {
                  AccessLinkResponse response = buildFileResponse(file,ownerId);
                    return response;
                } else {
                    Folder folder = (Folder) item;
                    AccessLinkResponse response = buildFolderResponse(folder,ownerId);
                    return response;
                }
            } else {
                requestService.sendAccessRequest(user.getId(), item.getId(), owner.getEmail());

                AccessLinkResponse response = new AccessLinkResponse();
                response.setAllowed(false);

                return response;
            }
        }

    private AccessLinkResponse buildFileResponse(File file, int ownerId) {
        AccessLinkResponse response = new AccessLinkResponse();
        byte[] fileData = fileService.readFile(ownerId, file.getId()).getData();
        String fileMimeType = FileUtility.getMimeTypeByFileName(file.getName());
        response.setItem_name(file.getName());
        response.setFile_data(fileData);
        response.setAllowed(true);
        response.setMimeType(fileMimeType);
        return response;
    }
    private AccessLinkResponse buildFolderResponse(Folder folder, int ownerId) {
        AccessLinkResponse response = new AccessLinkResponse();
        Map<String, List<String>> contents = folderService.getFolderContents(ownerId, folder.getId());
        response.setItem_name(folder.getName());
        response.setFolder_contents(contents);
        response.setAllowed(true);
        return response;
    }


}
