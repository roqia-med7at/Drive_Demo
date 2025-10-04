package com.roqia.Drive_demo.controller;


import com.roqia.Drive_demo.dto.PasswordDto;
import com.roqia.Drive_demo.dto.UserProfileDto;
import com.roqia.Drive_demo.mapper.UserMapper;
import com.roqia.Drive_demo.mapper.UserProfileMapper;
import com.roqia.Drive_demo.model.User;
import com.roqia.Drive_demo.security.jwt.model.UserPrincipal;
import com.roqia.Drive_demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/users")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserProfileMapper userProfileMapper;
    @GetMapping("/profile")
    public ResponseEntity<UserProfileDto>view_profile(Authentication authentication){
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        int user_id = userPrincipal.getUserId();
        UserProfileDto userDto = userService.get_user_profile(user_id);
        return ResponseEntity.ok(userDto);
    }
    @PutMapping("/profile")
    public ResponseEntity<?> update_profile(Authentication authentication,@RequestBody UserProfileDto userProfileDto){
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        int user_id = userPrincipal.getUserId();
        User user = userService.get_user(user_id);
       User updated_user = userProfileMapper.mapToUser(userProfileDto);
        userService.update_user_profile(user,updated_user);
        return ResponseEntity.ok("Profile for user with id "+user_id+"updated successfully");
    }
    @DeleteMapping("/account")
    public ResponseEntity<?> delete_account(Authentication authentication){
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        int user_id = userPrincipal.getUserId();
        User user = userService.get_user(user_id);
        userService.delete_user(user);
        return ResponseEntity.ok("Acccount for user with id"+user_id+"deleted successfully");
    }
    @PutMapping("/password")
    public ResponseEntity<?> change_password(Authentication authentication, @RequestBody PasswordDto passwordDto){
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User user = userService.get_user(userPrincipal.getUserId());
        userService.change_password(user.getId(),passwordDto.getOld_password(),passwordDto.getNew_password());
        return ResponseEntity.ok("Password for user : "+user.getName()+"Reset successfully ");
    }
}
