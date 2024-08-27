package com.ayds.Cloudmerce.repository;

import com.ayds.Cloudmerce.model.entity.CartItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItemEntity, Integer> {
}
