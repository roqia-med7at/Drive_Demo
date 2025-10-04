package com.roqia.Drive_demo.controller;

import com.roqia.Drive_demo.dto.response.SuccessResponse;
import com.roqia.Drive_demo.model.Item;
import com.roqia.Drive_demo.repo.ItemRepo;
import com.roqia.Drive_demo.security.jwt.model.UserPrincipal;
import com.roqia.Drive_demo.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/search")
public class SearchController {
    @Autowired
    private  ItemService itemService;

    @GetMapping()
    public ResponseEntity<SuccessResponse> search(Authentication authentication , @RequestParam String keyword) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        int user_id = userPrincipal.getUserId();
        List<String> itemNames = itemService.searchByItemName(user_id,keyword);
        SuccessResponse response = new SuccessResponse("All Items Match : ",itemNames);
        return ResponseEntity.ok(response);

    }
}
