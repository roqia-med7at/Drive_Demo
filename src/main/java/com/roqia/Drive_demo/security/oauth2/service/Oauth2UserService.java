package com.roqia.Drive_demo.security.oauth2.service;

import com.roqia.Drive_demo.error.customExceptions.RecordNotFoundException;
import com.roqia.Drive_demo.model.Provider;
import com.roqia.Drive_demo.model.User;
import com.roqia.Drive_demo.repo.ProviderRepo;
import com.roqia.Drive_demo.repo.UserRepo;
import com.roqia.Drive_demo.security.oauth2.model.CustomOauth2User;
import com.roqia.Drive_demo.service.FolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;

@Service
public class Oauth2UserService {
    @Autowired
    private ProviderRepo providerRepo;
    @Autowired
    private UserRepo userRepo;
    public User findOrCreateUser(CustomOauth2User oauth2User, String accessToken, String refreshAccessToken, Instant expiryDate) {
        String providerId = oauth2User.getProviderId();
        String userName = oauth2User.getUserName();
        String userEmail = oauth2User.getEmail();
        String providerName = oauth2User.getProviderName();
        Provider provider = providerRepo.findByProviderId(providerId);

        if (provider != null) {
            int userId = provider.getUserId();
            return userRepo.findById(userId)
                    .orElseThrow(() -> new RecordNotFoundException("No such user with id: " + userId));
        } else {
            User user = userRepo.findByEmail(userEmail).orElse(null);

            if (user == null) {
                user = new User();
                user.setName(userName);
                user.setEmail(userEmail);
                user.setRole("ROLE_USER");
                user = userRepo.save(user);
            }
            provider = new Provider();
            provider.setUserId(user.getId());
            provider.setProviderId(providerId);
            provider.setProviderName(providerName);
            provider.setAccessToken(accessToken);
            provider.setRefreshAccessToken(refreshAccessToken);
            provider.setExpiryDate(expiryDate);
            providerRepo.save(provider);

            return user;
        }
    }
}
