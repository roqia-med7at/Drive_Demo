package com.roqia.Drive_demo.controller;

import com.roqia.Drive_demo.dto.UserDto;
import com.roqia.Drive_demo.dto.UserProfileDto;
import com.roqia.Drive_demo.mapper.TokenMapper;
import com.roqia.Drive_demo.mapper.UserProfileMapper;
import com.roqia.Drive_demo.model.User;
import com.roqia.Drive_demo.security.jwt.model.RefreshToken;
import com.roqia.Drive_demo.security.jwt.model.UserPrincipal;
import com.roqia.Drive_demo.security.jwt.service.JwtService;
import com.roqia.Drive_demo.security.jwt.service.TokenService;
import com.roqia.Drive_demo.service.FolderService;
import com.roqia.Drive_demo.service.HandleUserLoginService;
import com.roqia.Drive_demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private UserService userService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private UserProfileMapper userProfileMapper;
    @Autowired
    private HandleUserLoginService loginService;
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserDto dto) {
        userService.add(dto);
        return ResponseEntity.ok("User registered successfully");
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String email ,@RequestParam String password) {
        try {
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(email, password);
            Authentication authorized = authenticationManager.authenticate(auth);
            UserPrincipal principal = (UserPrincipal) authorized.getPrincipal();
            int user_id = principal.getUserId();
            String userName = principal.getUsername();
            if (authorized.isAuthenticated()) {
                Map<String,String>map = loginService.handleTokens(user_id);
                int parentId = loginService.createRootFolder(user_id);
                return ResponseEntity.ok("Login successful for user: " + authorized.getName()+"\n"+"Token : "+map.get("token")+"\nRefresh-Token : "+map.get("refreshToken"));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login failed");
            }
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials: " + ex.getMessage());
        }
    }
    @PostMapping("/refresh")
        public ResponseEntity<?> refresh_token(@RequestBody Map<String, String> refresh_token){
        String refresh = refresh_token.get("refresh_token");
        RefreshToken refreshToken = tokenService.getRefreshTokenByToken(refresh);
        if (refreshToken.isRevoked()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token revoked,Login again");
        }
        if(jwtService.token_expired(refresh)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token expired,Login again");
        }
        else {
            String userId = jwtService.extract_userId(refresh);
            String new_token = jwtService.generate_token(Integer.parseInt(userId));
            return ResponseEntity.ok("Token : "+ new_token+"\nRefresh-Token : "+refresh_token);
        }

        }
        @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody Map<String,String> refreshToken){
          tokenService.revokeToken(refreshToken.get("refresh_token"));
          return ResponseEntity.ok("Logout successful");
        }
        @GetMapping("/me")
    public UserProfileDto get_userInfo(Authentication authentication){
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User user = userService.get_user(userPrincipal.getUserId());
        UserProfileDto dto = userProfileMapper.mapToDto(user);
        return dto;


        }
}
