package com.roqia.Drive_demo.controller;

import com.roqia.Drive_demo.dto.request.AccessRequest;
import com.roqia.Drive_demo.dto.request.RespondRequest;
import com.roqia.Drive_demo.dto.response.SuccessResponse;
import com.roqia.Drive_demo.security.jwt.model.UserPrincipal;
import com.roqia.Drive_demo.service.RequestService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/requests")
public class RequestController {
    @Autowired
    private RequestService requestService;
    @PostMapping("/send")
    public ResponseEntity<SuccessResponse> sendAccessRequest(Authentication authentication, @Valid @RequestBody AccessRequest accessRequest){
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        int user_id = userPrincipal.getUserId();
        requestService.sendAccessRequest(user_id,accessRequest.getItem_id(),accessRequest.getRecipientEmail());
        SuccessResponse successResponse = new SuccessResponse("Email sent successfully",null);
        return ResponseEntity.ok(successResponse);
    }
    @PostMapping("/respond")
    public ResponseEntity<SuccessResponse> respondToRequest(Authentication authentication, @Valid @RequestBody RespondRequest request){
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        int user_id = userPrincipal.getUserId();
        requestService.respondToRequest(user_id,request.getRequestId(),request.getItemId(),request.getAccess(),request.getAction());
        SuccessResponse successResponse = new SuccessResponse("Request handled successfully",null);
        return ResponseEntity.ok(successResponse);
    }
}
