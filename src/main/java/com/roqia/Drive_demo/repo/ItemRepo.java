package com.roqia.Drive_demo.repo;

import com.roqia.Drive_demo.model.File;
import com.roqia.Drive_demo.model.Folder;
import com.roqia.Drive_demo.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ItemRepo extends JpaRepository<Item,Integer> {
    public Optional<Item> findByIdAndOwner_Id(int file_id, int user_id);
    boolean existsByIdAndOwner_Id(int itemId,int ownerId);
    @Query(value = "SELECT * FROM items item WHERE item.name = CONCAT('User_', ?1)", nativeQuery = true)
    Optional<Item> findRootFolder(int userId);
    @Query("SELECT i FROM Item i WHERE i.owner.id = :userId AND LOWER(i.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Item> searchByName(@Param("userId") int userId, @Param("keyword") String keyword);

}
