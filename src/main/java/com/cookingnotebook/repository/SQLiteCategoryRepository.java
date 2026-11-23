package com.cookingnotebook.repository;

import com.cookingnotebook.database.DatabaseManager;
import com.cookingnotebook.exception.DatabaseException;
import com.cookingnotebook.exception.RecipeNotFoundException;
import com.cookingnotebook.model.Category;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SQLiteCategoryRepository implements CategoryRepository {
    private final DatabaseManager databaseManager;

    public SQLiteCategoryRepository(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    @Override
    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        try (Connection connection = databaseManager.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rows = statement.executeQuery(
                     "SELECT id, name FROM categories ORDER BY name")) {
            while (rows.next()) {
                Category category = new Category();
                category.setId(rows.getInt("id"));
                category.setName(rows.getString("name"));
                categories.add(category);
            }
        } catch (SQLException exception) {
            throw new DatabaseException("Failed to load categories", exception);
        }
        return categories;
    }

    @Override
    public void saveCategory(Category category) {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO categories(name) VALUES(?)",
                     Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, category.getName().trim());
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    category.setId(keys.getInt(1));
                }
            }
        } catch (SQLException exception) {
            throw new DatabaseException("Failed to save category", exception);
        }
    }

    @Override
    public void updateCategory(Category category) {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "UPDATE categories SET name = ? WHERE id = ?")) {
            statement.setString(1, category.getName().trim());
            statement.setInt(2, category.getId());
            if (statement.executeUpdate() != 1) {
                throw new RecipeNotFoundException(category.getId());
            }
        } catch (SQLException exception) {
            throw new DatabaseException("Failed to update category", exception);
        }
    }

    @Override
    public void deleteCategory(Category category) {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "DELETE FROM categories WHERE id = ?")) {
            statement.setInt(1, category.getId());
            if (statement.executeUpdate() != 1) {
                throw new RecipeNotFoundException(category.getId());
            }
        } catch (SQLException exception) {
            throw new DatabaseException("Failed to delete category", exception);
        }
    }
}
