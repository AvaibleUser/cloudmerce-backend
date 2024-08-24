package com.ayds.Cloudmerce.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.ayds.Cloudmerce.model.dto.CategoryRegisterDto;
import com.ayds.Cloudmerce.model.dto.CategoryUpdateDto;
import com.ayds.Cloudmerce.model.entity.CategoryEntity;
import com.ayds.Cloudmerce.repository.CategoryRepository;

@Service
public class CategoryService {

    @Value("${config.min-length-category}")
    private int minLengthCategory = 5;

    @Autowired
    private CategoryRepository categoryRepository;

    public List<CategoryEntity> findAllCategories() {
        return categoryRepository.findAll();
    }

    public List<CategoryEntity> findAllCategoriesById(Iterable<Long> categoryIds) {
        return categoryRepository.findAllById(categoryIds);
    }

    public CategoryEntity saveCategory(CategoryRegisterDto category) {
        if (category.name().length() < minLengthCategory) {
            throw new IllegalArgumentException(
                    String.format("The name of the category must be at least %d characters", minLengthCategory));
        }
        return categoryRepository.save(new CategoryEntity(category.name(), category.description()));
    }

    public CategoryEntity updateCategory(long categoryId, CategoryUpdateDto category) {
        CategoryEntity dbCategory = categoryRepository.findById(categoryId).get();
        dbCategory.setDescription(category.description());
        return categoryRepository.save(dbCategory);
    }

    public void deleteCategory(long categoryId) {
        CategoryEntity dbCategory = categoryRepository.findById(categoryId).get();
        if (!ObjectUtils.isEmpty(dbCategory.getProductCategories())) {
            throw new IllegalArgumentException("The category can't be deleted because have products associated");
        }
        categoryRepository.deleteById(categoryId);
    }
}
