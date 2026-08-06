package com.badara.backend.dto;

import java.time.Instant;
import java.util.UUID;

public class FileDtos {
    public record FileItem(UUID fileKey, String filename, String contentType, long size, Instant createdAt, String syncStatus) {}
    public record ShareRequest(int expiresInHours, int maxDownloads) {}
    public record ShareResponse(UUID token, Instant expiresAt, int maxDownloads) {}
}
