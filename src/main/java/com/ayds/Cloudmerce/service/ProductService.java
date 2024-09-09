package com.ayds.Cloudmerce.service;

import static com.ayds.Cloudmerce.enums.ProductState.DELETED;
import static java.util.function.Predicate.not;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

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
        return productRepository.findByIdAndStateNot(productId, DELETED).map(ProductService::toProductDto);
    }

    public Optional<ProductDto> findProductForCustomer(long productId) {
        return productRepository.findByIdAndState(productId, ProductState.VISIBLE).map(ProductService::toProductDto);
    }

    public Page<ProductDto> findPagedProductsFilteredBy(PagedProductFilteredDto filters) {
        Optional<Specification<ProductEntity>> optSpecification = Optional.<Specification<ProductEntity>>empty();
        Function<Specification<ProductEntity>, Optional<Specification<ProductEntity>>> addFilter = (
                filter) -> optSpecification.map((spec) -> spec.and(filter)).or(() -> Optional.of(filter));

        Stream.of(
                filters.categoryIds()
                        .filter(Collection::isEmpty)
                        .map(productSpecification::byCategoriesWithId)
                        .or(() -> filters.categoryNames()
                                .filter(Collection::isEmpty)
                                .map(productSpecification::byCategoriesWithName)),
                filters.name()
                        .filter(not(ObjectUtils::isEmpty))
                        .map(productSpecification::byNameLike),
                filters.description()
                        .filter(not(ObjectUtils::isEmpty))
                        .map(productSpecification::byDescriptionLike),
                filters.price()
                        .map(productSpecification::byPriceBetween),
                filters.stock()
                        .map(productSpecification::byStockGreaterThanOrEqualTo),
                filters.state()
                        .filter(not(DELETED::equals))
                        .map(productSpecification::byState))
                .forEach(filter -> filter.ifPresent(addFilter::apply));

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

        product.name().filter(not(ObjectUtils::isEmpty)).ifPresent(dbProduct::setName);
        product.description().filter(not(ObjectUtils::isEmpty)).ifPresent(dbProduct::setDescription);
        product.price().ifPresent(dbProduct::setPrice);
        product.stock().ifPresent(dbProduct::setStock);
        product.state().filter(not(DELETED::equals)).ifPresent(dbProduct::setState);

        if (product.categoryIds().isPresent() || product.categories().isPresent()) {
            boolean useIds = product.categoryIds().isPresent();
            Set<Long> ids = product.categoryIds().orElse(null);
            Set<String> names = product.categories().orElse(null);

            Collection<ProductCategoryEntity> productCategoriesRemoved = useIds
                    ? productCategoryRepository.findByProductIdAndCategoryIdNotIn(dbProduct.getId(), ids)
                    : productCategoryRepository.findByProductIdAndCategoryNameNotIn(dbProduct.getId(), names);

            productCategoriesRemoved = productCategoriesRemoved
                    .stream()
                    .filter(category -> {
                        if (useIds) {
                            if (ids.contains(category.getCategory().getId())) {
                                ids.remove(category.getCategory().getId());
                                return false;
                            }
                        } else if (names.contains(category.getCategory().getName())) {
                            names.remove(category.getCategory().getName());
                            return false;
                        }
                        return true;
                    })
                    .toList();

            productCategoryRepository.deleteAll(productCategoriesRemoved);

            List<CategoryEntity> categories = useIds
                    ? categoryRepository.findAllById(ids)
                    : categoryRepository.findAllByNameIn(names);

            if (ObjectUtils.isEmpty(categories) || (useIds ? ids.size() : names.size()) != categories.size()) {
                int diff = Math.abs((useIds ? ids.size() : names.size()) - categories.size());
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
        product.setState(DELETED);
        productRepository.save(product);
    }
}
