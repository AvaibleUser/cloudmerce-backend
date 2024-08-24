package com.ayds.Cloudmerce.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ayds.Cloudmerce.model.dto.CategoryRegisterDto;
import com.ayds.Cloudmerce.model.dto.CategoryUpdateDto;
import com.ayds.Cloudmerce.model.entity.CategoryEntity;
import com.ayds.Cloudmerce.service.CategoryService;

@Controller
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/")
    public ResponseEntity<List<CategoryEntity>> getAllCategories() {
        List<CategoryEntity> categories = categoryService.findAllCategories();

        return ResponseEntity.ok().body(categories);
    }

    @PostMapping("/")
    public ResponseEntity<?> addCategory(@RequestBody CategoryRegisterDto category) {
        CategoryEntity savedCategory = categoryService.saveCategory(category);

        return ResponseEntity.ok().body(savedCategory);
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<?> updateCategory(@PathVariable long categoryId, @RequestBody CategoryUpdateDto category) {
        CategoryEntity updatedCategory = categoryService.updateCategory(categoryId, category);

        return ResponseEntity.ok().body(updatedCategory);
    }
}
