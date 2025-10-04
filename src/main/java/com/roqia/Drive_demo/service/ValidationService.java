package com.roqia.Drive_demo.service;

import com.roqia.Drive_demo.Enum.Access;
import com.roqia.Drive_demo.Enum.Feature;
import com.roqia.Drive_demo.error.customExceptions.AccessDeniedException;
import com.roqia.Drive_demo.error.customExceptions.RecordNotFoundException;
import com.roqia.Drive_demo.model.*;
import com.roqia.Drive_demo.repo.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ValidationService {
    private final UserRepo userRepo;
    private final ItemRepo itemRepo;
    private final SharedItemRepo sharedItemRepo;
    private final AccessorRepo accessorRepo;
    private final SharedLinkRepo sharedLinkRepo;

    public ValidationService(UserRepo userRepo, ItemRepo itemRepo, SharedItemRepo sharedItemRepo, AccessorRepo accessorRepo, SharedLinkRepo sharedLinkRepo) {
        this.userRepo = userRepo;
        this.itemRepo = itemRepo;
        this.sharedItemRepo = sharedItemRepo;
        this.accessorRepo = accessorRepo;
        this.sharedLinkRepo = sharedLinkRepo;
    }

    public void validateUser(int userId) {
        if (!userRepo.existsById(userId)) {
            throw new RecordNotFoundException("No such user found with id :" + userId);
        }
    }

    public void validateItem(int ownerId,int itemId){
        if(!itemRepo.existsByIdAndOwner_Id(itemId,ownerId)){
            throw new RecordNotFoundException("No such item found with id :"+itemId);
        }
    }
    public void validateSharedItem(int itemId, int ownerId) {
        if (!sharedItemRepo.existsBySharedItem_IdAndOwnedBy_Id(itemId, ownerId)) {
            throw new RecordNotFoundException("No such item found with id :" + itemId);
        }
    }
    private Access getAccess(int userId, int itemId) {

        return accessorRepo.findByItem_IdAndUser_Id(itemId, userId)
                .map(Accessor::getAccess)
                .orElseThrow(() -> new RecordNotFoundException("No such accessor with user id: " + userId + " has access to item with id: " + itemId));
    }

    public boolean hasAccess(int userId, int itemId, Feature feature) {
        Access access = getAccess(userId, itemId);
        if (feature.equals(Feature.READ) || feature.equals(Feature.DOWNLOAD)) {
            return access == Access.OWNER || access == Access.EDITOR || access == Access.VIEWER || access == Access.COMMENTER;
        } else if (feature.equals(Feature.EDIT) || feature.equals(Feature.UPLOAD) || feature.equals(Feature.COPY)||feature.equals(Feature.CREATE)) {
            return access == Access.OWNER || access == Access.EDITOR;
        } else if (feature.equals(Feature.MANAGE) || feature.equals(Feature.SHARE)) {
            return access == Access.OWNER;
        } else {
            return false;
        }
    }

   // public  boolean hasGoogleProvider(int userId)
    public SharedLink validateSharedLink(String token) {
        SharedLink sharedLink = sharedLinkRepo.findByToken(token)
                .orElseThrow(() -> new RuntimeException("No such link found with this id"));

        if (sharedLink.getExpiryDate().isBefore(LocalDateTime.now())) {
            sharedLink.setRevoked(true);
            sharedLinkRepo.save(sharedLink);
            throw new AccessDeniedException("This link is expired");
        }
        return sharedLink;
    }

}

