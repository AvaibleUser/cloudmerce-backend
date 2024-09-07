package com.ayds.Cloudmerce.service;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
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
import com.ayds.Cloudmerce.model.dto.ProductDto;
import com.ayds.Cloudmerce.model.dto.ProductRegisterDto;
import com.ayds.Cloudmerce.model.dto.ProductUpdateDto;
import com.ayds.Cloudmerce.model.entity.CategoryEntity;
import com.ayds.Cloudmerce.model.entity.ImageEntity;
// import com.ayds.Cloudmerce.model.entity.ImageEntity;
import com.ayds.Cloudmerce.model.entity.ProductCategoryEntity;
import com.ayds.Cloudmerce.model.entity.ProductEntity;
import com.ayds.Cloudmerce.model.exception.BadRequestException;
import com.ayds.Cloudmerce.model.exception.ValueNotFoundException;
// import com.ayds.Cloudmerce.model.entity.ProductImageEntity;
import com.ayds.Cloudmerce.repository.CategoryRepository;
import com.ayds.Cloudmerce.repository.ImageRepository;
// import com.ayds.Cloudmerce.repository.ImageRepository;
import com.ayds.Cloudmerce.repository.ProductCategoryRepository;
// import com.ayds.Cloudmerce.repository.ProductImageRepository;
import com.ayds.Cloudmerce.repository.ProductRepository;
import com.ayds.Cloudmerce.repository.specification.ProductSpecification;

@Service
public class ProductService {

    @Autowired
    private ProductSpecification productSpecification;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Autowired
    private ImageRepository imageRepository;

    private static ProductDto toProductDto(ProductEntity product, Collection<CategoryEntity> categories,
            Collection<ImageEntity> images) {
        return new ProductDto(product.getId(), product.getName(), product.getDescription(),
                product.getPrice(), product.getStock(), product.getState(), product.getCreationAt(),
                categories.stream()
                        .map(CategoryEntity::getName)
                        .toList(),
                images.stream()
                        .map(ImageEntity::getUrl)
                        .toList());
    }

    private static ProductDto toProductDto(ProductEntity product) {
        return toProductDto(product,
                product.getProductCategories()
                        .stream()
                        .map(ProductCategoryEntity::getCategory).toList(),
                product.getImages());
    }

    public Optional<ProductDto> findProduct(long productId) {
        return productRepository.findByIdAndStateNot(productId, ProductState.DELETED).map(ProductService::toProductDto);
    }

    public Optional<ProductDto> findProductForCustomer(long productId) {
        return productRepository.findByIdAndState(productId, ProductState.VISIBLE).map(ProductService::toProductDto);
    }

    public Page<ProductDto> findPagedProductsFilteredBy(PagedProductFilteredDto filters) {
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
        if (filters.state() != null && filters.state() != ProductState.DELETED) {
            addFilter.apply(productSpecification.byState(filters.state()));
        }

        PageRequest pageReq = PageRequest.of(
                filters.page().orElse(0),
                filters.size().filter(size -> size == 0).orElse(20),
                filters.sortedBy()
                        .map(sortedBy -> Sort.by(filters.descending()
                                .filter(Boolean::booleanValue)
                                .map(desc -> Direction.DESC)
                                .orElse(Direction.ASC),
                                sortedBy))
                        .orElse(Sort.unsorted()));

        return productRepository.findAll(optSpecification.orElse(Specification.where(null)), pageReq)
                .map(ProductService::toProductDto);
    }

    @Transactional
    public ProductDto saveProduct(ProductRegisterDto product, Collection<String> imageUrls) {
        if (ObjectUtils.isEmpty(product.categories()) && ObjectUtils.isEmpty(product.categoryIds())) {
            throw new BadRequestException("Se deben de asignar al menos una categoria al producto");
        }

        List<CategoryEntity> categories = !ObjectUtils.isEmpty(product.categoryIds())
                ? categoryRepository.findAllById(product.categoryIds())
                : categoryRepository.findAllByNameIn(product.categories());

        if (ObjectUtils.isEmpty(categories) || product.categories().size() != categories.size()) {
            int diff = Math.abs(product.categories().size() - categories.size());
            throw new ValueNotFoundException("Hay " + diff + " categorias que no se encontraron");
        }

        ProductEntity newProduct = productRepository.saveAndFlush(new ProductEntity(
                product.name(), product.description(), product.price(), product.stock()));

        productCategoryRepository.saveAll(categories.stream()
                .map(category -> new ProductCategoryEntity(category, newProduct))
                .toList());

        List<ImageEntity> images = List.of();
        if (!ObjectUtils.isEmpty(imageUrls)) { // TODO: cambiar por un error si no trae imagenes
            images = imageRepository.saveAll(imageUrls.stream()
                    .map(imageUrl -> new ImageEntity(imageUrl, newProduct))
                    .toList());
        }

        return toProductDto(newProduct, categories, images);
    }

    @Transactional
    public ProductDto updateProduct(long productId, ProductUpdateDto product) {
        ProductEntity dbProduct = productRepository.findById(productId).get();

        if (!ObjectUtils.isEmpty(product.name())) {
            dbProduct.setName(product.name());
        }
        if (!ObjectUtils.isEmpty(product.description())) {
            dbProduct.setDescription(product.description());
        }
        if (product.price() != null) {
            dbProduct.setPrice(product.price());
        }
        if (product.stock() != null) {
            dbProduct.setStock(product.stock());
        }
        if (product.state() != null && product.state() != ProductState.DELETED) {
            dbProduct.setState(product.state());
        }
        if (product.categoryIds() != null || product.categoryIds() != null) {
            if (product.categories().isEmpty() && product.categoryIds().isEmpty()) {
                throw new BadRequestException("The product must have at least one category");
            }
            boolean useIds = !ObjectUtils.isEmpty(product.categoryIds());
            Collection<ProductCategoryEntity> productCategoriesRemoved = useIds
                    ? productCategoryRepository.findByProductIdAndCategoryIdNotIn(dbProduct.getId(),
                            product.categoryIds())
                    : productCategoryRepository.findByProductIdAndCategoryNameNotIn(dbProduct.getId(),
                            product.categories());

            productCategoriesRemoved = productCategoriesRemoved
                    .stream()
                    .filter(category -> {
                        if (useIds) {
                            if (product.categoryIds().contains(category.getCategory().getId())) {
                                product.categoryIds().remove(category.getCategory().getId());
                                return false;
                            }
                        } else if (product.categories().contains(category.getCategory().getName())) {
                            product.categories().remove(category.getCategory().getName());
                            return false;
                        }
                        return true;
                    })
                    .toList();

            productCategoryRepository.deleteAll(productCategoriesRemoved);

            List<CategoryEntity> categories = useIds
                    ? categoryRepository.findAllById(product.categoryIds())
                    : categoryRepository.findAllByNameIn(product.categories());

            if (ObjectUtils.isEmpty(categories) || product.categories().size() != categories.size()) {
                int diff = Math.abs(product.categories().size() - categories.size());
                throw new ValueNotFoundException("There are " + diff + " categories not found");
            }

            List<ProductCategoryEntity> productCategories = productCategoryRepository.saveAll(categories.stream()
                    .map(category -> new ProductCategoryEntity(category, dbProduct))
                    .toList());

            dbProduct.setProductCategories(new HashSet<>(productCategories));
        }
        // if (product.imageUrls() != null) {
        // if (product.imageUrls().isEmpty()) {
        // throw new BadRequestException("The product must have at least one image");
        // }
        // List<CategoryEntity> categories =
        // categoryRepository.findAllByCategory(product.imageUrls());
        // dbProduct.setCategories(categories);
        // }

        return toProductDto(productRepository.save(dbProduct));
    }

    public void deleteProduct(long productId) {
        ProductEntity product = productRepository.findById(productId).get();
        product.setState(ProductState.DELETED);
        productRepository.save(product);
    }
}
