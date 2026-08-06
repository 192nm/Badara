package com.badara.backend.service;

import com.badara.backend.config.StorageProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
@ConditionalOnProperty(name = "storage.mode", havingValue = "s3")
public class S3StorageService implements StorageService {
    private final S3Client s3Client;
    private final StorageProperties storageProperties;

    public S3StorageService(S3Client s3Client, StorageProperties storageProperties) {
        this.s3Client = s3Client;
        this.storageProperties = storageProperties;
    }

    @Override
    public String upload(String owner, MultipartFile file) {
        String bucket = storageProperties.getS3().getBucket();
        String objectKey = owner + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();

        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(objectKey)
                    .contentType(file.getContentType())
                    .build();
            s3Client.putObject(request, RequestBody.fromBytes(file.getBytes()));
            return objectKey;
        } catch (NoSuchBucketException ex) {
            throw new IllegalStateException("S3 bucket not found: " + bucket, ex);
        } catch (IOException ex) {
            throw new IllegalStateException("S3 upload failed", ex);
        }
    }

    @Override
    public Resource download(String storagePath) {
        String bucket = storageProperties.getS3().getBucket();
        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucket)
                .key(storagePath)
                .build();
        byte[] content = s3Client.getObjectAsBytes(request).asByteArray();
        return new ByteArrayResource(content);
    }
}
