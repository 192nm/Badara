package com.badara.backend.web;

import com.badara.backend.service.ShareService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/public")
public class PublicController {
    private final ShareService shareService;

    public PublicController(ShareService shareService) {
        this.shareService = shareService;
    }

    @GetMapping("/share/{token}")
    public ResponseEntity<Resource> downloadShared(@PathVariable UUID token) {
        Resource resource = shareService.resolvePublicDownload(token);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment").body(resource);
    }
}
