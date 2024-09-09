package com.ayds.Cloudmerce.service;

import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ayds.Cloudmerce.model.dto.ImageDto;
import com.ayds.Cloudmerce.model.entity.ImageEntity;
import com.ayds.Cloudmerce.model.entity.ProductEntity;
import com.ayds.Cloudmerce.model.exception.ValueNotFoundException;
import com.ayds.Cloudmerce.repository.ImageRepository;
import com.ayds.Cloudmerce.repository.ProductRepository;

@Service
public class ImageService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ImageRepository imageRepository;

    private ImageDto toImageDto(ImageEntity image) {
        return new ImageDto(image.getUrl(), image.getProduct().getId());
    }

    public ImageDto saveIfNotExistsImage(long productId, String imageUrl) {
        List<ImageEntity> images = imageRepository.findByUrlAndProductId(imageUrl, productId);

        if (!ObjectUtils.isEmpty(images)) {
            return toImageDto(images.getFirst());
        }
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new ValueNotFoundException("El producto no existe"));

        return toImageDto(imageRepository.save(new ImageEntity(imageUrl, product)));
    }

    public void deleteImage(long productId, String imageName) {
        imageRepository.deleteByProductIdAndUrlLike(productId, "%" + imageName);
    }
}
