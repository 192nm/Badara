package com.badara.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
@ConditionalOnProperty(name = "storage.mode", havingValue = "local", matchIfMissing = true)
public class LocalStorageService implements StorageService {
    private final Path root;

    public LocalStorageService(@Value("${storage.local-root}") String rootPath) throws IOException {
        this.root = Path.of(rootPath).toAbsolutePath().normalize();
        Files.createDirectories(this.root);
    }

    @Override
    public String upload(String owner, MultipartFile file) {
        try {
            Path ownerDir = root.resolve(owner);
            Files.createDirectories(ownerDir);
            String objectKey = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path target = ownerDir.resolve(objectKey).normalize();
            file.transferTo(target);
            return target.toString();
        } catch (IOException e) {
            throw new IllegalStateException("Upload failed", e);
        }
    }

    @Override
    public Resource download(String storagePath) {
        return new PathResource(Path.of(storagePath));
    }
}
