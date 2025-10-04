package com.roqia.Drive_demo.security.jwt.repo;

import com.roqia.Drive_demo.security.jwt.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepo extends JpaRepository<RefreshToken,Integer> {
   public RefreshToken findByToken(String token);
}
