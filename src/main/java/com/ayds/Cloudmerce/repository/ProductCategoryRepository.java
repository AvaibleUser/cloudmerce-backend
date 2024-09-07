package com.ayds.Cloudmerce.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ayds.Cloudmerce.model.entity.ProductCategoryEntity;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategoryEntity, Long> {

    Set<ProductCategoryEntity> findByProductIdAndCategoryIdNotIn(Long productId, Iterable<Long> categoryIds);

    Set<ProductCategoryEntity> findByProductIdAndCategoryNameNotIn(Long productId, Iterable<String> categoryNames);
}
