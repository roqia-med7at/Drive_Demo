package com.roqia.Drive_demo.security.jwt.service;



import com.roqia.Drive_demo.error.customExceptions.RecordNotFoundException;
import com.roqia.Drive_demo.model.User;
import com.roqia.Drive_demo.repo.UserRepo;
import com.roqia.Drive_demo.security.jwt.model.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepo userRepo;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepo.findByEmail(email).get();
        if (user==null){
            throw new RecordNotFoundException("User with this email is not found");
        }
        UserPrincipal userPrincipal = new UserPrincipal(user);
        return userPrincipal;
    }
}
