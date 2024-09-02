package com.ayds.Cloudmerce.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

@Service
public class FileStorageService {

    @Autowired
    private AmazonS3 s3Client;

    @Value("${aws.s3.bucketName}")
    private String bucketName;

    public String store(String filename, MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("The file must have any content.");
        }

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());

        PutObjectRequest s3PutRequest;
        try {
            s3PutRequest = new PutObjectRequest(bucketName, filename, file.getInputStream(), metadata);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        s3Client.putObject(s3PutRequest);

        return loadUrl(filename);
    }

    public String store(MultipartFile file) {
        String filename = file.getOriginalFilename();
        return store(filename, file);
    }

    public String loadUrl(String filename) {
        return s3Client.getUrl(bucketName, filename).toExternalForm();
    }

    public void delete(String filename) {
        DeleteObjectRequest s3DeleteRequest = new DeleteObjectRequest(bucketName, filename);
        s3Client.deleteObject(s3DeleteRequest);
    }
}
