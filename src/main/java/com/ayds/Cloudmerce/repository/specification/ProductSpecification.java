package com.ayds.Cloudmerce.repository.specification;

import java.util.Set;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import com.ayds.Cloudmerce.enums.ProductState;
import com.ayds.Cloudmerce.model.entity.ProductCategoryEntity;
import com.ayds.Cloudmerce.model.entity.ProductEntity;

import jakarta.persistence.criteria.Join;

@Component
public class ProductSpecification {

    public Specification<ProductEntity> byCategoriesWithId(Set<Long> categoryIds) {
        return (root, query, criteriaBuilder) -> {
            Join<ProductCategoryEntity, ProductEntity> productCategories = root.join("productCategories");
            return productCategories.<Long>get("category_id").in(categoryIds);
        };
    }

    public Specification<ProductEntity> byPriceBetween(float min, float max) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.between(root.<Float>get("price"), min, max);
    }

    public Specification<ProductEntity> byStockGreaterThanOrEqualTo(long min) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.<Long>get("stock"), min);
    }

    public Specification<ProductEntity> byState(ProductState state) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.<ProductState>get("state"), state);
    }

    public Specification<ProductEntity> byNameLike(String regex) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.<String>get("name"), regex);
    }

    public Specification<ProductEntity> byDescriptionLike(String regex) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.<String>get("description"), regex);
    }
}
