package com.ayds.Cloudmerce.repository;

import com.ayds.Cloudmerce.model.entity.CartItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItemEntity, Integer> {

    Optional<List<CartItemEntity>> findAllByCartId(Integer cartId);
}
