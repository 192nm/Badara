package com.badara.backend.web;

import com.badara.backend.dto.FileDtos;
import com.badara.backend.service.FileService;
import com.badara.backend.service.ShareService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/files")
public class FileController {
    private final FileService fileService;
    private final ShareService shareService;

    public FileController(FileService fileService, ShareService shareService) {
        this.fileService = fileService;
        this.shareService = shareService;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public FileDtos.FileItem upload(Authentication authentication, @RequestPart("file") MultipartFile file) {
        return fileService.upload(authentication.getName(), file);
    }

    @GetMapping
    public List<FileDtos.FileItem> list(Authentication authentication, @RequestParam(required = false) String q) {
        return fileService.list(authentication.getName(), q);
    }

    @GetMapping("/{fileKey}/download")
    public ResponseEntity<Resource> download(Authentication authentication, @PathVariable UUID fileKey) {
        var metadata = fileService.getOwnedFile(authentication.getName(), fileKey);
        Resource resource = fileService.download(authentication.getName(), fileKey);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + metadata.getFilename() + "\"")
                .contentType(MediaType.parseMediaType(metadata.getContentType()))
                .body(resource);
    }

    @PostMapping("/{fileKey}/share")
    public FileDtos.ShareResponse share(Authentication authentication, @PathVariable UUID fileKey, @RequestBody FileDtos.ShareRequest request) {
        return shareService.createShareLink(authentication.getName(), fileKey, request);
    }
}
