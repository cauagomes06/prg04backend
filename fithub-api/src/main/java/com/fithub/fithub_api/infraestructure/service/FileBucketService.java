package com.fithub.fithub_api.infraestructure.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class FileBucketService {

    private final S3Client s3Client;

    @Value("${supabase.storage.bucket}")
    private String bucket;

    @Value("${supabase.storage.endpoint}")
    private String endpoint;

    public FileBucketService(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public String uploadFile(String fileName, byte[] content) {

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(fileName)
                .contentType("image/jpeg")
                .build();

        s3Client.putObject(
                request,
                RequestBody.fromBytes(content)
        );

        String publicUrlBase = endpoint
                .replace(".storage", "")
                .replace("/s3", "/object/public");
        // Retorna URL pública
        return publicUrlBase + "/" + bucket + "/" + fileName;
    }
}
