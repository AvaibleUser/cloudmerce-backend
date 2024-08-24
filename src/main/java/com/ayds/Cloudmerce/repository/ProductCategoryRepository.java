package com.ayds.Cloudmerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ayds.Cloudmerce.model.entity.ProductCategoryEntity;

public interface ProductCategoryRepository extends JpaRepository<ProductCategoryEntity, Long> {

}
