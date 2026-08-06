package com.badara.backend.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    String upload(String owner, MultipartFile file);
    Resource download(String storagePath);
}
