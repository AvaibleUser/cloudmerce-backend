package com.ayds.Cloudmerce.service;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
import com.ayds.Cloudmerce.model.exception.BadRequestException;

@Service
public class FileStorageService {

    @Autowired
    private AmazonS3 s3Client;

    @Value("${aws.s3.bucketName}")
    private String bucketName;

    public String store(String filename, InputStream inputStream, String contentType, long contentLength) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(contentType);
        metadata.setContentLength(contentLength);

        PutObjectRequest s3PutRequest = new PutObjectRequest(bucketName, filename, inputStream, metadata);
        s3Client.putObject(s3PutRequest);

        return loadUrl(filename);
    }

    public String store(String filename, MultipartFile file) {
        if (file.isEmpty()) {
            throw new BadRequestException("The file must have any content.");
        }

        try {
            return store(filename, file.getInputStream(), file.getContentType(), file.getSize());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String store(MultipartFile file) {
        String filename = file.getOriginalFilename();
        return store(filename, file);
    }

    public byte[] load(String filename) throws IOException {
        GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, filename);
        S3Object response = s3Client.getObject(getObjectRequest);

        return IOUtils.toByteArray(response.getObjectContent());
    }

    public String loadContentType(String filename) {
        GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, filename);
        return s3Client.getObject(getObjectRequest)
                .getObjectMetadata()
                .getContentType();
    }

    public String loadUrl(String filename) {
        return s3Client.getUrl(bucketName, filename).toExternalForm();
    }

    public void delete(String filename) {
        DeleteObjectRequest s3DeleteRequest = new DeleteObjectRequest(bucketName, filename);
        s3Client.deleteObject(s3DeleteRequest);
    }
}
