package com.roqia.Drive_demo.service;

import com.roqia.Drive_demo.dto.UserDto;
import com.roqia.Drive_demo.dto.UserProfileDto;
import com.roqia.Drive_demo.error.customExceptions.IncorrectPasswordException;
import com.roqia.Drive_demo.error.customExceptions.RecordNotFoundException;
import com.roqia.Drive_demo.mapper.UserMapper;
import com.roqia.Drive_demo.mapper.UserProfileMapper;
import com.roqia.Drive_demo.model.User;
import com.roqia.Drive_demo.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserProfileMapper userProfileMapper;

    public User add(UserDto dto) {
        User user = userMapper.mapTouser(dto);
        String pass = passwordEncoder.encode(user.getPassword());
        user.setPassword(pass);
        return userRepo.save(user);
    }

    public UserProfileDto get_user_profile(int user_id) {
        User user = userRepo.findById(user_id).orElseThrow(()->new RecordNotFoundException("No such user found with id" + user_id));
            return userProfileMapper.mapToDto(user);
    }

    public User get_user(int user_id) {
        Optional<User> user = userRepo.findById(user_id);
        if (user.isEmpty()) {
            throw new RecordNotFoundException("No such user found with id" + user_id);
        } else {
            return user.get();
        }
    }

    public void update_user_profile(User user, User updated_user) {
        user.setName(updated_user.getName());
        user.setEmail(updated_user.getEmail());
        user.setRole(updated_user.getRole());
        userRepo.save(user);

    }

    public void delete_user(User user) {
        userRepo.delete(user);

    }

    public void change_password(int user_id, String old_pass, String new_pass) {
        User user = get_user(user_id);
        boolean flag = passwordEncoder.matches(old_pass, user.getPassword());

        if (flag) {
            user.setPassword(passwordEncoder.encode(new_pass));
            userRepo.save(user);
        } else {
            throw new IncorrectPasswordException("Wrong password");
        }


    }

    public User searchByEmail(String email) {
        Optional<User> user = userRepo.findByEmail(email);
            return user.orElseThrow(()-> new RecordNotFoundException("User with email :"+email+"is not found"));
    }
}

