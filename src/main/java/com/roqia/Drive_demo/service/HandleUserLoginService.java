package com.roqia.Drive_demo.service;

import com.roqia.Drive_demo.model.Folder;
import com.roqia.Drive_demo.security.jwt.service.JwtService;
import com.roqia.Drive_demo.security.jwt.service.TokenService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class HandleUserLoginService {
    private final JwtService jwtService;
    private final FolderService folderService;
    private final TokenService tokenService;
    public HandleUserLoginService(JwtService jwtService, FolderService folderService, TokenService tokenService) {
        this.jwtService = jwtService;
        this.folderService = folderService;
        this.tokenService = tokenService;
    }

    public int createRootFolder(int userId){
       Folder folder = folderService.create_root_folder(userId);


        return folder.getId();
    }
    public Map<String,String> handleTokens(int userId){
        String token = jwtService.generate_token(userId);
        int tokenUserId = Integer.parseInt(jwtService.extract_userId(token));
        String refreshToken = jwtService.generate_refresh_token(userId);
        tokenService.addToken(refreshToken,tokenUserId);
        Map<String,String>map = new HashMap<>();
        map.put("token",token);
        map.put("refreshToken",refreshToken);
        return map;
    }
}
