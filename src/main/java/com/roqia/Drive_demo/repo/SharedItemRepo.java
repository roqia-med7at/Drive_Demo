package com.roqia.Drive_demo.repo;

import com.roqia.Drive_demo.model.SharedItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SharedItemRepo extends JpaRepository<SharedItem,Integer> {
    Optional<SharedItem> findBySharedItem_IdAndOwnedBy_Id(int item_id, int owner_id);
    List<SharedItem> findBySharedItem_Id(int itemId);
    List<SharedItem> findBySharedWith_Id(int userId);
    boolean existsBySharedItem_IdAndOwnedBy_Id(int itemId, int ownerId);

   Optional<SharedItem> findBySharedItem_IdAndSharedWith_Id(int id, int userId);
}
