package com.roqia.Drive_demo.repo;

import com.roqia.Drive_demo.model.Folder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface FolderRepo extends JpaRepository<Folder,Integer> {
    @Query(value = "SELECT * FROM folders f WHERE f.owner_id=?1 AND f.parent_folder_id IS NULL ",nativeQuery = true)
   Folder findRootFolderByOwner(int userId);
    Optional<Folder> findByNameAndParentFolder(String folderName,Folder parentFolder);
    Optional<Folder> findByIdAndOwner_Id(int folderId,int user_id);
    @Transactional
    @Modifying
    @Query(value = """
        WITH RECURSIVE folder_hierarchy AS (
            SELECT id FROM folders WHERE id = :folderId
            UNION ALL
            SELECT f.id
            FROM folders f
            INNER JOIN folder_hierarchy fh ON f.parent_folder_id = fh.id
        )
        DELETE FROM folders
        WHERE id IN (SELECT id FROM folder_hierarchy)
        """, nativeQuery = true)
    void deleteFolderAndSubfolders(@Param("folderId") int folderId);

}
