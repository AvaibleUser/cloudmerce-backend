package com.ayds.Cloudmerce.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.ayds.Cloudmerce.model.dto.CategoryDto;
import com.ayds.Cloudmerce.model.dto.CategoryRegisterDto;
import com.ayds.Cloudmerce.model.dto.CategoryUpdateDto;
import com.ayds.Cloudmerce.model.entity.CategoryEntity;
import com.ayds.Cloudmerce.model.exception.RequestConflictException;
import com.ayds.Cloudmerce.repository.CategoryRepository;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    private static CategoryDto toCategoryDto(CategoryEntity category) {
        return new CategoryDto(category.getId(), category.getName(), category.getDescription());
    }

    public List<CategoryDto> findAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(CategoryService::toCategoryDto)
                .toList();
    }

    public List<CategoryDto> findAllCategoriesById(Iterable<Long> categoryIds) {
        return categoryRepository.findAllById(categoryIds)
                .stream()
                .map(CategoryService::toCategoryDto)
                .toList();
    }

    public CategoryDto saveCategory(CategoryRegisterDto category) {
        return toCategoryDto(categoryRepository.save(new CategoryEntity(category.name(), category.description())));
    }

    public CategoryDto updateCategory(long categoryId, CategoryUpdateDto category) {
        CategoryEntity dbCategory = categoryRepository.findById(categoryId).get();
        dbCategory.setDescription(category.description());
        return toCategoryDto(categoryRepository.save(dbCategory));
    }

    public void deleteCategory(long categoryId) {
        CategoryEntity dbCategory = categoryRepository.findById(categoryId).get();
        if (!ObjectUtils.isEmpty(dbCategory.getProductCategories())) {
            throw new RequestConflictException("The category can't be deleted because have products associated");
        }
        categoryRepository.deleteById(categoryId);
    }
}
