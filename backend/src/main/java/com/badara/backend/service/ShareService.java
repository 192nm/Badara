package com.badara.backend.service;

import com.badara.backend.domain.ShareLink;
import com.badara.backend.dto.FileDtos;
import com.badara.backend.repository.ShareLinkRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class ShareService {
    private final FileService fileService;
    private final ShareLinkRepository shareLinkRepository;

    public ShareService(FileService fileService, ShareLinkRepository shareLinkRepository) {
        this.fileService = fileService;
        this.shareLinkRepository = shareLinkRepository;
    }

    public FileDtos.ShareResponse createShareLink(String username, UUID fileKey, FileDtos.ShareRequest request) {
        var file = fileService.getOwnedFile(username, fileKey);
        ShareLink shareLink = new ShareLink();
        shareLink.setToken(UUID.randomUUID());
        int expiresIn = request.expiresInHours() <= 0 ? 24 : request.expiresInHours();
        int maxDownloads = request.maxDownloads() <= 0 ? 100 : request.maxDownloads();
        shareLink.setExpiresAt(Instant.now().plusSeconds(expiresIn * 3600L));
        shareLink.setMaxDownloads(maxDownloads);
        shareLink.setDownloadCount(0);
        shareLink.setFile(file);
        shareLinkRepository.save(shareLink);
        return new FileDtos.ShareResponse(shareLink.getToken(), shareLink.getExpiresAt(), shareLink.getMaxDownloads());
    }

    public org.springframework.core.io.Resource resolvePublicDownload(UUID token) {
        ShareLink link = shareLinkRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Link not found"));
        if (link.getExpiresAt().isBefore(Instant.now())) {
            throw new IllegalArgumentException("Link expired");
        }
        if (link.getDownloadCount() >= link.getMaxDownloads()) {
            throw new IllegalArgumentException("Download limit exceeded");
        }
        link.setDownloadCount(link.getDownloadCount() + 1);
        shareLinkRepository.save(link);
        return fileService.downloadByMetadata(link.getFile());
    }
}
