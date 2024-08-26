package com.ayds.Cloudmerce.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ayds.Cloudmerce.model.entity.ProductCategoryEntity;

public interface ProductCategoryRepository extends JpaRepository<ProductCategoryEntity, Long> {

    Set<ProductCategoryEntity> findByProductIdAndCategoryIdNotIn(Long productId, Iterable<Long> categoryIds);
}
