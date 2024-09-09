package com.ayds.Cloudmerce.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ayds.Cloudmerce.model.dto.ImageDto;
import com.ayds.Cloudmerce.model.dto.ProductDto;
import com.ayds.Cloudmerce.model.exception.ValueNotFoundException;
import com.ayds.Cloudmerce.service.FileStorageService;
import com.ayds.Cloudmerce.service.ImageService;
import com.ayds.Cloudmerce.service.ProductService;

import jakarta.validation.constraints.Positive;

@RestController
@RequestMapping("/api/products/{productId}/images")
public class ImageController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private FileStorageService fileStorageService;

    @GetMapping("/{slot}")
    public ResponseEntity<?> getProductImage(@PathVariable @Positive Long productId,
            @PathVariable @Positive Integer slot) {
        ProductDto product = productService.findProduct(productId)
                .orElseThrow(() -> new ValueNotFoundException("No se pudo encontrar el producto"));

        String filename = product.name() + "_" + slot;
        String mediaType = fileStorageService.loadContentType(filename);
        String extension = mediaType.replaceAll(".*/", "");

        try {
            byte[] pdfBytes = fileStorageService.load(filename);

            ByteArrayResource resource = new ByteArrayResource(pdfBytes);

            return ResponseEntity.ok()
                    .contentType(MediaType.valueOf(mediaType))
                    .contentLength(resource.contentLength())
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            ContentDisposition.attachment()
                                    .filename(filename + "." + extension)
                                    .build()
                                    .toString())
                    .body(resource);
        } catch (IOException e) {
            throw new ValueNotFoundException("No se pudo encontrar la imagen");
        }
    }

    @PutMapping("/{slot}")
    public ResponseEntity<ImageDto> storeProductImage(@PathVariable @Positive Long productId,
            @PathVariable @Positive Integer slot, @RequestPart MultipartFile imageFile) {
        ProductDto product = productService.findProduct(productId)
                .orElseThrow(() -> new ValueNotFoundException("No se pudo encontrar el producto"));

        String imageUrls = fileStorageService.store(product.name() + "_" + slot, imageFile);
        ImageDto image = imageService.saveIfNotExistsImage(productId, imageUrls);

        return ResponseEntity.ok(image);
    }

    @DeleteMapping("/{slot}")
    public ResponseEntity<?> removeProductImage(@PathVariable @Positive Long productId,
            @PathVariable @Positive Integer slot) {
        ProductDto product = productService.findProduct(productId)
                .orElseThrow(() -> new ValueNotFoundException("No se pudo encontrar el producto"));

        String filename = product.name() + "_" + slot;

        fileStorageService.delete(filename);
        imageService.deleteImage(productId, filename);

        return ResponseEntity.ok().build();
    }
}
