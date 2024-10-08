package com.ayds.Cloudmerce.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ayds.Cloudmerce.enums.ProductState;
import com.ayds.Cloudmerce.model.dto.PagedProductFilteredDto;
import com.ayds.Cloudmerce.model.dto.ProductDto;
import com.ayds.Cloudmerce.model.dto.ProductRegisterDto;
import com.ayds.Cloudmerce.model.dto.ProductUpdateDto;
import com.ayds.Cloudmerce.service.FileStorageService;
import com.ayds.Cloudmerce.service.ProductService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private FileStorageService fileStorageService;

    @GetMapping("/purchasable")
    public ResponseEntity<PagedModel<ProductDto>> getPagedProductsFilteredForCustomer(
            @Valid PagedProductFilteredDto productFilters) {
        productFilters.withStock(Optional.of(1L));
        productFilters.withState(Optional.of(ProductState.VISIBLE));

        Page<ProductDto> pagedProducts = productService.findPagedProductsFilteredBy(productFilters);
        return ResponseEntity.ok(new PagedModel<>(pagedProducts));
    }

    @GetMapping
    public ResponseEntity<PagedModel<ProductDto>> getPagedProductsFiltered(
            @Valid PagedProductFilteredDto productFilters) {
        Page<ProductDto> pagedProducts = productService.findPagedProductsFilteredBy(productFilters);
        return ResponseEntity.ok(new PagedModel<>(pagedProducts));
    }

    @GetMapping("/purchasable/{productId}")
    public ResponseEntity<ProductDto> getProductForCustomer(@PathVariable @Positive long productId) {
        Optional<ProductDto> product = productService.findProductForCustomer(productId);

        return ResponseEntity.of(product);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductDto> getProduct(@PathVariable @Positive long productId) {
        Optional<ProductDto> product = productService.findProduct(productId);

        return ResponseEntity.of(product);
    }

    @PostMapping("/complete")
    public ResponseEntity<ProductDto> addProduct(@RequestPart("images") @NotEmpty List<MultipartFile> imageFiles,
            @RequestPart @Valid ProductRegisterDto product) {
        List<String> imageUrls = IntStream.range(1, imageFiles.size() + 1)
                .boxed()
                .map(i -> fileStorageService.store(product.name() + "_" + i, imageFiles.get(i - 1)))
                .toList();

        ProductDto savedProduct = productService.saveProduct(product, imageUrls);
        return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
    }

    @PostMapping
    public ResponseEntity<ProductDto> addProduct(@RequestBody @Valid ProductRegisterDto product) {
        ProductDto savedProduct = productService.saveProduct(product, null);
        return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable @Positive long productId,
            @RequestBody @Valid ProductUpdateDto product) {
        ProductDto resProduct = productService.updateProduct(productId, product);
        return ResponseEntity.ok(resProduct);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable @Positive long productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.ok().build();
    }
}
