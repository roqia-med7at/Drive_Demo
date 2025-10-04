package com.roqia.Drive_demo.repo;

import com.roqia.Drive_demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User,Integer> {
    public User findByName(String username);

   public Optional<User> findByEmail(String email);
   public boolean existsById(int userId);
}
