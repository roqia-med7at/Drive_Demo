package com.roqia.Drive_demo.repo;

import com.roqia.Drive_demo.model.SharedLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SharedLinkRepo extends JpaRepository<SharedLink,Integer> {
     Optional<SharedLink> findBySharedItem_Id(int item_id);
     Optional<SharedLink> findByToken(String token);
}
