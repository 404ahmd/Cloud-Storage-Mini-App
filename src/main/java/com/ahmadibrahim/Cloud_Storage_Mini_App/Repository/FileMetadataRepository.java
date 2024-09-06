package com.ahmadibrahim.Cloud_Storage_Mini_App.Repository;

import com.ahmadibrahim.Cloud_Storage_Mini_App.Entity.FileMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FileMetadataRepository extends JpaRepository<FileMetadata, Long> {
    Optional<FileMetadata> findByFileName(String fileName);
}
