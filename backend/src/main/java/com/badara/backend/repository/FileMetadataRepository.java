package com.badara.backend.repository;

import com.badara.backend.domain.AppUser;
import com.badara.backend.domain.FileMetadata;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FileMetadataRepository extends JpaRepository<FileMetadata, Long> {
    List<FileMetadata> findByOwnerAndFilenameContainingIgnoreCase(AppUser owner, String keyword);
    List<FileMetadata> findByOwner(AppUser owner);
    Optional<FileMetadata> findByFileKey(UUID fileKey);
}
