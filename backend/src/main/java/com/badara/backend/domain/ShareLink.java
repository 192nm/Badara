package com.badara.backend.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "share_links")
public class ShareLink {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private UUID token;

    @ManyToOne(optional = false)
    private FileMetadata file;

    @Column(nullable = false)
    private Instant expiresAt;

    @Column(nullable = false)
    private int maxDownloads;

    @Column(nullable = false)
    private int downloadCount;

    public Long getId() { return id; }
    public UUID getToken() { return token; }
    public void setToken(UUID token) { this.token = token; }
    public FileMetadata getFile() { return file; }
    public void setFile(FileMetadata file) { this.file = file; }
    public Instant getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }
    public int getMaxDownloads() { return maxDownloads; }
    public void setMaxDownloads(int maxDownloads) { this.maxDownloads = maxDownloads; }
    public int getDownloadCount() { return downloadCount; }
    public void setDownloadCount(int downloadCount) { this.downloadCount = downloadCount; }
}
