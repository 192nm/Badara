package com.badara.backend.service;

import com.badara.backend.domain.FileMetadata;
import com.badara.backend.dto.FileDtos;
import com.badara.backend.repository.FileMetadataRepository;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class FileService {
    private final AuthService authService;
    private final FileMetadataRepository fileMetadataRepository;
    private final StorageService storageService;

    public FileService(AuthService authService, FileMetadataRepository fileMetadataRepository, StorageService storageService) {
        this.authService = authService;
        this.fileMetadataRepository = fileMetadataRepository;
        this.storageService = storageService;
    }

    public FileDtos.FileItem upload(String username, MultipartFile file) {
        var owner = authService.requireUser(username);
        String storagePath = storageService.upload(username, file);
        FileMetadata metadata = new FileMetadata();
        metadata.setFileKey(UUID.randomUUID());
        metadata.setOwner(owner);
        metadata.setFilename(file.getOriginalFilename() == null ? "unnamed" : file.getOriginalFilename());
        metadata.setContentType(file.getContentType() == null ? "application/octet-stream" : file.getContentType());
        metadata.setSize(file.getSize());
        metadata.setCreatedAt(Instant.now());
        metadata.setStoragePath(storagePath);
        metadata.setSyncStatus("SYNCED");
        fileMetadataRepository.save(metadata);
        return toDto(metadata);
    }

    public List<FileDtos.FileItem> list(String username, String q) {
        var owner = authService.requireUser(username);
        List<FileMetadata> items = (q == null || q.isBlank())
                ? fileMetadataRepository.findByOwner(owner)
                : fileMetadataRepository.findByOwnerAndFilenameContainingIgnoreCase(owner, q);
        return items.stream().map(this::toDto).toList();
    }

    public Resource download(String username, UUID fileKey) {
        FileMetadata metadata = fileMetadataRepository.findByFileKey(fileKey)
                .orElseThrow(() -> new IllegalArgumentException("File not found"));
        if (!metadata.getOwner().getUsername().equals(username)) {
            throw new IllegalArgumentException("Forbidden");
        }
        return storageService.download(metadata.getStoragePath());
    }

    public FileMetadata getOwnedFile(String username, UUID fileKey) {
        FileMetadata metadata = fileMetadataRepository.findByFileKey(fileKey)
                .orElseThrow(() -> new IllegalArgumentException("File not found"));
        if (!metadata.getOwner().getUsername().equals(username)) {
            throw new IllegalArgumentException("Forbidden");
        }
        return metadata;
    }

    public Resource downloadByMetadata(FileMetadata metadata) {
        return storageService.download(metadata.getStoragePath());
    }

    public FileDtos.FileItem toDto(FileMetadata metadata) {
        return new FileDtos.FileItem(
                metadata.getFileKey(),
                metadata.getFilename(),
                metadata.getContentType(),
                metadata.getSize(),
                metadata.getCreatedAt(),
                metadata.getSyncStatus()
        );
    }
}
