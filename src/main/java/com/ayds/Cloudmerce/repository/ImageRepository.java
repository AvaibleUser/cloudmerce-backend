package com.ayds.Cloudmerce.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ayds.Cloudmerce.model.entity.ImageEntity;

@Repository
public interface ImageRepository extends JpaRepository<ImageEntity, Long> {

    List<ImageEntity> findByUrlAndProductId(String url, Long productId);

    void deleteByProductIdAndUrlLike(Long productId, String filename);
}
