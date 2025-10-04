package com.roqia.Drive_demo.service;

import com.roqia.Drive_demo.Enum.Access;
import com.roqia.Drive_demo.Enum.RequestStatus;
import com.roqia.Drive_demo.error.customExceptions.GoogleProviderException;
import com.roqia.Drive_demo.error.customExceptions.RecordNotFoundException;
import com.roqia.Drive_demo.error.customExceptions.SendEmailException;
import com.roqia.Drive_demo.model.*;
import com.roqia.Drive_demo.repo.AccessRequestRepo;
import com.roqia.Drive_demo.repo.AccessorRepo;
import com.roqia.Drive_demo.repo.ProviderRepo;
import com.roqia.Drive_demo.repo.SharedItemRepo;
import com.roqia.Drive_demo.security.oauth2.service.GoogleService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class RequestService {
    private final AccessorRepo accessorRepo;
    private final ProviderRepo providerRepo;
    private final GoogleService googleService;
    private final AccessRequestRepo accessRequestRepo;
    private final ItemService itemService;
    private final UserService userService;
    private final SharedItemRepo sharedItemRepo;
    public RequestService(AccessorRepo accessorRepo, ProviderRepo providerRepo, GoogleService googleService, AccessRequestRepo accessRequestRepo, ItemService itemService, UserService userService, SharedItemRepo sharedItemRepo) {
        this.accessorRepo = accessorRepo;
        this.providerRepo = providerRepo;
        this.googleService = googleService;
        this.accessRequestRepo = accessRequestRepo;
        this.itemService = itemService;
        this.userService = userService;
        this.sharedItemRepo = sharedItemRepo;
    }


    public void  sendAccessRequest(int userId,int itemId,String recipientEmail)  {
        User sender = userService.get_user(userId);
        String email = sender.getEmail();
        User recipient =  userService.searchByEmail(recipientEmail);
        Item item = itemService.getItem(recipient.getId(),itemId);

        String path = itemService.get_item_path(item);
        String subject = "Access request to :"+path+" From : "+email;

        AccessRequest accessRequest = new AccessRequest();
        accessRequest.setSender(sender);
        accessRequest.setRecipient(recipient);
        int requestId = accessRequestRepo.save(accessRequest).getId();

        String text = "<a href='http://localhost:8080/api/share/access/respond/" + requestId + "' " +
                "style='display:inline-block;padding:12px 24px;" +
                "font-size:16px;font-weight:bold;" +
                "background-color:#28a745;color:#ffffff;" +
                "text-decoration:none;border-radius:8px;" +
                "font-family:Arial,sans-serif;'>Respond</a>";

       // Provider provider = providerRepo.findByProviderNameAndUserId("google",userId).orElseThrow(()->new RecordNotFoundException("No such provider found with name: \"google\" And user id"+userId));
        Optional<Provider> optionalProvider = providerRepo.findByProviderNameAndUserId("google", userId);

        if (optionalProvider.isEmpty()) {

            throw new GoogleProviderException("Please, link your Google account to be able to send the email.");
            //Hit "link google" endpoint
        }
        Provider provider = optionalProvider.get();

        try {
            googleService.sendMail(recipientEmail,subject,text,provider.getAccessToken());
        } catch (Exception e) {
            throw new SendEmailException("Failed to send email",e);
        }

    }
    public void respondToRequest(int userId, int requestId, int itemId, Access access, RequestStatus action){
        User user = userService.get_user(userId);
        Item item =  itemService.getItem(user.getId(),itemId);
        AccessRequest request =getRequest(requestId);
        User sender = request.getSender();
        Provider provider = providerRepo.findByProviderNameAndUserId("google",userId).orElseThrow(()->new RecordNotFoundException("No such provider found with name: \"google\" And user id"+userId));


        if(action==RequestStatus.ACCEPTED){
            Accessor accessor = new Accessor();
            accessor.setUser(sender);
            accessor.setItem(item);
            accessor.setAccess(access);
            accessorRepo.save(accessor);

            request.setStatus(RequestStatus.ACCEPTED);
            accessRequestRepo.save(request);

            SharedItem sharedItem = new SharedItem();
            sharedItem.setSharedWith(sender);
            sharedItem.setOwnedBy(user);
            sharedItem.setCreatedAt(LocalDateTime.now());
            sharedItem.setSharedItem(item);
            sharedItemRepo.save(sharedItem);


            try {
                googleService.sendMail(sender.getEmail(),"Access Response From"+user.getEmail(),"You can now access :"+item.getName()+"As a "+access,provider.getAccessToken());
            } catch (Exception e) {
                throw new SendEmailException("Failed to send email",e);
            }
        }else {
            request.setStatus(RequestStatus.REJECTED);
            accessRequestRepo.save(request);
            try {
                googleService.sendMail(sender.getEmail(),"Access Response From"+user.getEmail(),"Your Request Is Rejected",provider.getAccessToken());
            } catch (Exception e) {
                throw new SendEmailException("Failed to send email",e);
            }
        }


    }
    private AccessRequest getRequest(int requestId){
        return accessRequestRepo.findById(requestId).orElseThrow(()->new RecordNotFoundException("No such request with id : "+requestId));
    }
}
