package com.roqia.Drive_demo.repo;


import com.roqia.Drive_demo.model.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProviderRepo  extends JpaRepository<Provider,Integer> {
    public Provider findByProviderId(String provider_id);
    Optional<Provider>findByAccessToken(String accessToken);
    Optional<Provider>findByProviderNameAndUserId(String providerName,int userId);
}
