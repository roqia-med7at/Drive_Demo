package com.roqia.Drive_demo.repo;

import com.roqia.Drive_demo.model.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileRepo extends JpaRepository<File,Integer> {
    Optional<File> findByIdAndOwner_Id(int file_id, int user_id);
    @Query("SELECT MAX(f.version) FROM File f WHERE f.name = ?1 AND f.folder.id = ?2")
    Integer findMaxVersionByNameAndFolder_Id(String file_name, int folder_id);
   List<File> findAllByFolder_Id(int folderId);
    @Modifying
    @Transactional
    @Query(value = """
    WITH RECURSIVE folder_hierarchy AS (
        SELECT id FROM folders WHERE id = :folderId
        UNION ALL
        SELECT f.id
        FROM folders f
        INNER JOIN folder_hierarchy fh ON f.parent_folder_id = fh.id
    )
    DELETE FROM files
    WHERE folder_id IN (SELECT id FROM folder_hierarchy)
    """, nativeQuery = true)
    void deleteFilesInFolderAndSubfolders(@Param("folderId") int folderId);
}
