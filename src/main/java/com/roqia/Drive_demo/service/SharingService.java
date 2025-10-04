package com.roqia.Drive_demo.service;

import com.roqia.Drive_demo.Enum.Access;
import com.roqia.Drive_demo.Enum.Feature;
import com.roqia.Drive_demo.dto.response.CopyLinkResponse;
import com.roqia.Drive_demo.dto.response.ShareWithResponse;
import com.roqia.Drive_demo.dto.response.SharedItemResponse;
import com.roqia.Drive_demo.error.customExceptions.AccessDeniedException;
import com.roqia.Drive_demo.mapper.SharedItemMapper;
import com.roqia.Drive_demo.model.*;
import com.roqia.Drive_demo.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class SharingService {

    @Autowired
    private UserService userService;
    @Autowired
    private SharedItemRepo sharedItemRepo;
    @Autowired
    private SharedLinkRepo sharedLinkRepo;
    @Autowired
    private ItemService itemService;
    @Autowired
    private SharedItemMapper sharedItemMapper;
    @Autowired
    private ValidationService validationService;
    @Autowired
    private StorageService storageService;
    @Autowired
    private AccessorRepo accessorRepo;

    public ShareWithResponse share_with (int owner_id, int item_id, String email , Access access){
        if (!validationService.hasAccess(owner_id,item_id, Feature.SHARE)) {
            throw new AccessDeniedException("You have no access to share that item");
        }
        User owner = userService.get_user(owner_id);
        User userToShareWith = userService.searchByEmail(email);
        Item item = itemService.getItem(owner_id,item_id);
          SharedItem sharedItem = saveSharedItemToDB(item,owner,userToShareWith);
          Accessor accessor = new Accessor();
          accessor.setUser(userToShareWith);
          accessor.setItem(item);
          accessor.setAccess(access);
          accessorRepo.save(accessor);

        String path = getSharedItemPath(itemService.get_item_path(item));

       ShareWithResponse response = new ShareWithResponse();
         response.setSharedItem_name( sharedItem.getSharedItem().getName());
         response.setSharedItem_path(path);
         response.setSharedBy(sharedItem.getOwnedBy().getEmail());
         response.setSharedWith(userToShareWith.getEmail());
         response.setAccessLevel(access);
         response.setSharedItem_type(item.getClass().getName());
        return response;

    }
    public CopyLinkResponse copyOrGetLink(int owner_id, int item_id){
        Item item = itemService.getItem(owner_id,item_id);

        SharedLink confirmedLink;
        Optional<SharedLink> link = sharedLinkRepo.findBySharedItem_Id(item_id);
        if (link.isPresent()) {
            if (!link.get().isRevoked() && link.get().getExpiryDate().isAfter(LocalDateTime.now())) {
                confirmedLink = link.get();
            } else {
                SharedLink existedLink=link.get();

               existedLink.setRevoked(false);
               existedLink.setToken(UUID.randomUUID().toString());
               existedLink.setExpiryDate(LocalDateTime.now().plusHours(24));
               confirmedLink = sharedLinkRepo.save(existedLink);
            }
        }else {
            String token = UUID.randomUUID().toString();
            LocalDateTime expiryDate = LocalDateTime.now().plusHours(24);
           confirmedLink= saveSharedLinkToDB(token, expiryDate, item);
        }
        String link_path = get_link(confirmedLink);
        String link_token = confirmedLink.getToken();
        CopyLinkResponse response = new CopyLinkResponse();
        response.setLink_path(link_path);
        response.setLink_token(link_token);

        return response;
    }

    public List<SharedItemResponse> getAllSharedItems(int userId){
       validationService.validateUser(userId);
        List<SharedItemResponse> responses = new ArrayList<>();
       List<SharedItem> sharedItems = sharedItemRepo.findBySharedWith_Id(userId);
       for (SharedItem item :sharedItems){
           SharedItemResponse response =   sharedItemMapper.mapToDto(item);
           responses.add(response);
       }
        return  responses;
    }

    public String getSharedItemPath (String itemPath){
        String path = "shared/";
        String[]arr = itemPath.split("/");

        for (int i=2;i<arr.length;i++){
           path+=arr[i];
           if(i!=arr.length-1)
               path+="/";
        }
       return path;
    }
    private String get_link(SharedLink sharedLink){
      String token =sharedLink.getToken();
        return "http://localhost:8080/shared/" + token;
    }
    private SharedLink saveSharedLinkToDB(String token,LocalDateTime expiryDate,Item item){
        SharedLink link = new SharedLink();
        link.setSharedItem(item);
        link.setToken(token);
        link.setExpiryDate(expiryDate);
        return sharedLinkRepo.save(link);

    }
    private SharedItem saveSharedItemToDB(Item item,User owner,User userToShareWith){
        SharedItem sharedItem = new SharedItem();
        sharedItem.setSharedItem(item);
        sharedItem.setOwnedBy(owner);
        sharedItem.setCreatedAt(LocalDateTime.now());
        sharedItem.setSharedWith(userToShareWith);
        return sharedItemRepo.save(sharedItem);
    }

}
