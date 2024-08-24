package com.ayds.Cloudmerce.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ayds.Cloudmerce.model.entity.ImageEntity;
import com.ayds.Cloudmerce.repository.ImageRepository;

@Service
public class ImageService {

    @Autowired
    private ImageRepository imageRepository;

    public List<ImageEntity> findAllImagesById(Iterable<Long> imageIds) {
        return imageRepository.findAllById(imageIds);
    }

    public ImageEntity saveImage(String imageUrl) {
        return imageRepository.save(new ImageEntity(imageUrl));
    }
}
