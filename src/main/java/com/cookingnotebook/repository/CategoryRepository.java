package com.cookingnotebook.repository;

import com.cookingnotebook.model.Category;

import java.util.List;

public interface CategoryRepository {
    List<Category> getAllCategories();
    void saveCategory(Category category);
    void updateCategory(Category category);
    void deleteCategory(Category category);
}
