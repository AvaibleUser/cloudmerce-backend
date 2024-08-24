package com.ayds.Cloudmerce.service;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import com.ayds.Cloudmerce.enums.ProductState;
import com.ayds.Cloudmerce.model.dto.PagedProductFilteredDto;
import com.ayds.Cloudmerce.model.dto.ProductRegisterDto;
import com.ayds.Cloudmerce.model.dto.ProductUpdateDto;
import com.ayds.Cloudmerce.model.entity.CategoryEntity;
import com.ayds.Cloudmerce.model.entity.ImageEntity;
import com.ayds.Cloudmerce.model.entity.ProductCategoryEntity;
import com.ayds.Cloudmerce.model.entity.ProductEntity;
import com.ayds.Cloudmerce.model.entity.ProductImageEntity;
import com.ayds.Cloudmerce.repository.ProductCategoryRepository;
import com.ayds.Cloudmerce.repository.ProductImageRepository;
import com.ayds.Cloudmerce.repository.ProductRepository;
import com.ayds.Cloudmerce.repository.specification.ProductSpecification;

@Service
public class ProductService {

    @Autowired
    private ProductSpecification productSpecification;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Autowired
    private ProductImageRepository productImageRepository;

    public Optional<ProductEntity> findProduct(long productId) {
        return productRepository.findById(productId);
    }

    public Page<ProductEntity> findPagedProductsFilteredBy(PagedProductFilteredDto filters) {
        Optional<Specification<ProductEntity>> optSpecification = Optional.<Specification<ProductEntity>>empty();
        Function<Specification<ProductEntity>, Optional<Specification<ProductEntity>>> addFilter = (
                filter) -> optSpecification.map((spec) -> spec.and(filter)).or(() -> Optional.of(filter));

        if (ObjectUtils.isEmpty(filters.categories())) {
            addFilter.apply(productSpecification.byCategoriesWithId(filters.categories()));
        }
        if (ObjectUtils.isEmpty(filters.name())) {
            addFilter.apply(productSpecification.byNameLike(filters.name()));
        }
        if (ObjectUtils.isEmpty(filters.description())) {
            addFilter.apply(productSpecification.byDescriptionLike(filters.description()));
        }
        if (filters.price() != null) {
            addFilter.apply(productSpecification.byPriceBetween(filters.price().min(), filters.price().max()));
        }
        if (filters.stock() != null) {
            addFilter.apply(productSpecification.byStockGreaterThanOrEqualTo(filters.stock()));
        }
        if (filters.state() != null) {
            addFilter.apply(productSpecification.byState(filters.state()));
        }

        PageRequest pageReq = PageRequest.of(filters.page(), filters.size() == 0 ? 20 : filters.size(),
                Sort.by(filters.descending() ? Direction.DESC : Direction.ASC, filters.sortedBy()));

        return productRepository.findAll(optSpecification.orElse(Specification.where(null)), pageReq);
    }

    @Transactional
    public ProductEntity saveProduct(ProductRegisterDto product, Collection<CategoryEntity> categories,
            Collection<ImageEntity> images) {
        if (ObjectUtils.isEmpty(product.categories())) {
            throw new IllegalArgumentException("The product must have at least one category");
        }
        if (ObjectUtils.isEmpty(categories) || product.categories().size() != categories.size()) {
            int diff = Math.abs(product.categories().size() - categories.size());
            throw new IllegalArgumentException("There are " + diff + " categories not found");
        }
        if (ObjectUtils.isEmpty(product.images())) {
            throw new IllegalArgumentException("The product must have at least one category");
        }
        if (ObjectUtils.isEmpty(images) || product.images().size() != images.size()) {
            int diff = Math.abs(product.images().size() - images.size());
            throw new IllegalArgumentException("There are " + diff + " images not found");
        }
        ProductEntity newProduct = productRepository.save(new ProductEntity(
                product.name(), product.description(), product.price(), product.stock()));

        productCategoryRepository.saveAll(categories.stream()
                .map(category -> new ProductCategoryEntity(category, newProduct))
                .toList());

        productImageRepository.saveAll(images.stream()
                .map(image -> new ProductImageEntity(image, newProduct))
                .toList());

        return newProduct;
    }

    @Transactional
    public ProductEntity updateProduct(long productId, ProductUpdateDto product) {
        ProductEntity dbProduct = productRepository.findById(productId).get();

        if (product.name() != null) {
            dbProduct.setName(product.name());
        }
        if (product.description() != null) {
            dbProduct.setDescription(product.description());
        }
        if (product.price() != null) {
            dbProduct.setPrice(product.price());
        }
        if (product.stock() != null) {
            dbProduct.setStock(product.stock());
        }
        if (product.state() != null) {
            dbProduct.setState(product.state());
        }
        // if (product.categories() != null) {
        // if (product.categories().isEmpty()) {
        // throw new IllegalArgumentException("The product must have at least one
        // category");
        // }
        // Set<CategoryEntity> categories =
        // categoryRepository.findAllByCategory(product.categories());
        // dbProduct.setProductCategories(null);(categories);
        // }
        // if (product.imageUrls() != null) {
        // if (product.imageUrls().isEmpty()) {
        // throw new IllegalArgumentException("The product must have at least one
        // category");
        // }
        // Set<CategoryEntity> categories =
        // categoryRepository.findAllByCategory(product.imageUrls());
        // dbProduct.setCategories(categories);
        // }

        return productRepository.save(dbProduct);
    }

    public void deleteProduct(long productId) {
        ProductEntity product = productRepository.findById(productId).get();
        product.setState(ProductState.DELETED);
        productRepository.save(product);
    }
}
