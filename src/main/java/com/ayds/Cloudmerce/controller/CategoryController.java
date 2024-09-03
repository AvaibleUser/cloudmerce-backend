package com.ayds.Cloudmerce.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ayds.Cloudmerce.model.dto.CategoryDto;
import com.ayds.Cloudmerce.model.dto.CategoryRegisterDto;
import com.ayds.Cloudmerce.model.dto.CategoryUpdateDto;
import com.ayds.Cloudmerce.service.CategoryService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryDto>> getAllCategories() {
        List<CategoryDto> categories = categoryService.findAllCategories();

        return ResponseEntity.ok(categories);
    }

    @PostMapping
    public ResponseEntity<CategoryDto> addCategory(@RequestBody @Valid CategoryRegisterDto category) {
        CategoryDto savedCategory = categoryService.saveCategory(category);

        return ResponseEntity.ok(savedCategory);
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryDto> updateCategory(@PathVariable @Positive long categoryId,
            @RequestBody @Valid CategoryUpdateDto category) {
        CategoryDto updatedCategory = categoryService.updateCategory(categoryId, category);

        return ResponseEntity.ok(updatedCategory);
    }
}
