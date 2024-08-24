package com.ayds.Cloudmerce.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.ayds.Cloudmerce.model.dto.PagedProductFilteredDto;
import com.ayds.Cloudmerce.model.dto.ProductRegisterDto;
import com.ayds.Cloudmerce.model.dto.ProductUpdateDto;
import com.ayds.Cloudmerce.model.entity.CategoryEntity;
import com.ayds.Cloudmerce.model.entity.ImageEntity;
import com.ayds.Cloudmerce.model.entity.ProductEntity;
import com.ayds.Cloudmerce.service.CategoryService;
import com.ayds.Cloudmerce.service.FileStorageService;
import com.ayds.Cloudmerce.service.ImageService;
import com.ayds.Cloudmerce.service.ProductService;

@Controller
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private FileStorageService fileStorageService;

    @GetMapping("/")
    public ResponseEntity<?> getPagedProductsFiltered(PagedProductFilteredDto productFilters) {
        Page<ProductEntity> pagedProducts = productService.findPagedProductsFilteredBy(productFilters);
        return ResponseEntity.ok().body(pagedProducts);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<?> getProduct(@PathVariable("productId") long productId) {
        Optional<ProductEntity> product = productService.findProduct(productId);

        return product.map(ResponseEntity.ok()::body)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/")
    public ResponseEntity<?> addProduct(@RequestPart("logo-file") List<MultipartFile> imageFiles,
            @RequestPart ProductRegisterDto product) {
        List<ImageEntity> images = IntStream.range(1, imageFiles.size() + 1)
                .boxed()
                .map(i -> fileStorageService.store(product.name() + "_" + i, imageFiles.get(i - 1)))
                .map(imageService::saveImage)
                .toList();

        List<CategoryEntity> categories = categoryService.findAllCategoriesById(product.categories());

        ProductEntity savedProduct = productService.saveProduct(product, categories, images);
        return ResponseEntity.ok().body(savedProduct);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<?> updateProduct(@PathVariable long productId, @RequestBody ProductUpdateDto product) {
        ProductEntity resProduct = productService.updateProduct(productId, product);
        return ResponseEntity.ok().body(resProduct);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<?> deleteProduct(@PathVariable long productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.ok().build();
    }
}
