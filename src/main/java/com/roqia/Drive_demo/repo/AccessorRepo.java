package com.roqia.Drive_demo.repo;

import com.roqia.Drive_demo.model.Accessor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccessorRepo extends JpaRepository<Accessor,Integer> {
    List<Accessor> findAllByItem_Id(int itemId);
    Optional<Accessor> findByItem_IdAndUser_Id(int itemId,int userId);
}
