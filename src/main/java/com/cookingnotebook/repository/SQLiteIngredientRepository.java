package com.cookingnotebook.repository;

import com.cookingnotebook.database.DatabaseManager;
import com.cookingnotebook.exception.DatabaseException;
import com.cookingnotebook.exception.RecipeNotFoundException;
import com.cookingnotebook.model.Ingredient;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class SQLiteIngredientRepository implements IngredientRepository {
    private final DatabaseManager databaseManager;

    public SQLiteIngredientRepository(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    @Override
    public List<Ingredient> getByRecipeId(int recipeId) {
        List<Ingredient> ingredients = new ArrayList<>();
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT id, recipe_id, name, quantity, unit "
                             + "FROM ingredients WHERE recipe_id = ? ORDER BY id")) {
            statement.setInt(1, recipeId);
            try (ResultSet rows = statement.executeQuery()) {
                while (rows.next()) {
                    Ingredient ingredient = new Ingredient();
                    ingredient.setId(rows.getInt("id"));
                    ingredient.setRecipeId(rows.getInt("recipe_id"));
                    ingredient.setName(rows.getString("name"));
                    double quantity = rows.getDouble("quantity");
                    ingredient.setQuantity(rows.wasNull() ? null : quantity);
                    ingredient.setUnit(rows.getString("unit"));
                    ingredients.add(ingredient);
                }
            }
        } catch (SQLException exception) {
            throw new DatabaseException("Failed to load ingredients", exception);
        }
        return ingredients;
    }

    @Override
    public void saveIngredient(Ingredient ingredient) {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                     INSERT INTO ingredients(recipe_id, name, quantity, unit)
                     VALUES(?, ?, ?, ?)
                     """, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, ingredient.getRecipeId());
            statement.setString(2, ingredient.getName());
            setQuantity(statement, 3, ingredient.getQuantity());
            statement.setString(4, ingredient.getUnit());
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    ingredient.setId(keys.getInt(1));
                }
            }
        } catch (SQLException exception) {
            throw new DatabaseException("Failed to save ingredient", exception);
        }
    }

    @Override
    public void updateIngredient(Ingredient ingredient) {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                     UPDATE ingredients
                     SET name = ?, quantity = ?, unit = ?
                     WHERE id = ? AND recipe_id = ?
                     """)) {
            statement.setString(1, ingredient.getName());
            setQuantity(statement, 2, ingredient.getQuantity());
            statement.setString(3, ingredient.getUnit());
            statement.setInt(4, ingredient.getId());
            statement.setInt(5, ingredient.getRecipeId());
            if (statement.executeUpdate() != 1) {
                throw new RecipeNotFoundException(ingredient.getId());
            }
        } catch (SQLException exception) {
            throw new DatabaseException("Failed to update ingredient", exception);
        }
    }

    @Override
    public void deleteIngredient(Ingredient ingredient) {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "DELETE FROM ingredients WHERE id = ?")) {
            statement.setInt(1, ingredient.getId());
            if (statement.executeUpdate() != 1) {
                throw new RecipeNotFoundException(ingredient.getId());
            }
        } catch (SQLException exception) {
            throw new DatabaseException("Failed to delete ingredient", exception);
        }
    }

    private void setQuantity(
            PreparedStatement statement,
            int index,
            Double quantity) throws SQLException {
        if (quantity == null) {
            statement.setNull(index, Types.REAL);
        } else {
            statement.setDouble(index, quantity);
        }
    }
}
