package com.ayds.Cloudmerce.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ayds.Cloudmerce.enums.ProductState;
import com.ayds.Cloudmerce.model.dto.RecentSaleDto;
import com.ayds.Cloudmerce.model.entity.ProductEntity;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long>, JpaSpecificationExecutor<ProductEntity> {

    Optional<ProductEntity> findByIdAndStateNot(Long productId, ProductState state);

    Optional<ProductEntity> findByIdAndState(Long productId, ProductState state);

    // @Query("""
    //         SELECT  AS product
    //         """;)
    // List<RecentSaleDto> findByRecentSale();

    long countByState(ProductState state);
}
