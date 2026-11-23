package com.cookingnotebook.repository;

import com.cookingnotebook.database.DatabaseManager;
import com.cookingnotebook.exception.DatabaseException;
import com.cookingnotebook.exception.RecipeNotFoundException;
import com.cookingnotebook.model.Recipe;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import java.util.ArrayList;
import java.util.List;

public class SQLiteRecipeRepository
        implements RecipeRepository {

    private final DatabaseManager databaseManager;

    public SQLiteRecipeRepository() {
        this(new DatabaseManager());
    }

    public SQLiteRecipeRepository(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    @Override
    public List<Recipe> getAllRecipes() {

        List<Recipe> recipes =
                new ArrayList<>();

        String sql =
                """
                SELECT
                    id,
                    title,
                    description,
                    instructions,
                    preparation_time,
                    servings,
                    category_id,
                    created_at,
                    updated_at
                FROM recipes
                """;

        try (
                Connection connection =
                        databaseManager.getConnection();

                Statement statement =
                        connection.createStatement();

                ResultSet resultSet =
                        statement.executeQuery(sql)
        ) {

            while (resultSet.next()) {

                Recipe recipe = new Recipe();

                recipe.setId(
                        resultSet.getInt("id"));

                recipe.setTitle(
                        resultSet.getString("title"));

                recipe.setDescription(
                        resultSet.getString("description"));
                recipe.setInstructions(
                        resultSet.getString("instructions"));
                recipe.setPreparationTime(
                        resultSet.getInt("preparation_time"));
                recipe.setServings(
                        resultSet.getInt("servings"));

                int categoryId =
                        resultSet.getInt("category_id");
                recipe.setCategoryId(
                        resultSet.wasNull() ? 0 : categoryId);

                String createdAt =
                        resultSet.getString("created_at");

                if (createdAt != null
                        && !createdAt.isBlank()) {

                    recipe.setCreatedAt(
                            parseTimestamp(createdAt));
                }

                String updatedAt =
                        resultSet.getString("updated_at");

                if (updatedAt != null
                        && !updatedAt.isBlank()) {

                    recipe.setUpdatedAt(
                            parseTimestamp(updatedAt));
                }

                recipes.add(recipe);
            }

        } catch (SQLException e) {

            throw new DatabaseException("Failed to load recipes", e);
        }

        return recipes;
    }

    @Override
    public void saveRecipe(Recipe recipe) {

        LocalDateTime now =
                LocalDateTime.now();

        recipe.setCreatedAt(now);
        recipe.setUpdatedAt(now);

        String sql =
                """
                INSERT INTO recipes(
                    title,
                    description,
                    instructions,
                    preparation_time,
                    servings,
                    category_id,
                    created_at,
                    updated_at
                )
                VALUES(?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (
                Connection connection =
                        databaseManager.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(
                                sql,
                                Statement.RETURN_GENERATED_KEYS)
        ) {

            statement.setString(
                    1,
                    recipe.getTitle());

            statement.setString(
                    2,
                    recipe.getDescription());

            statement.setString(
                    3,
                    recipe.getInstructions());

            statement.setInt(
                    4,
                    recipe.getPreparationTime());

            statement.setInt(
                    5,
                    recipe.getServings());

            if (recipe.getCategoryId() > 0) {
                statement.setInt(6, recipe.getCategoryId());
            } else {
                statement.setNull(6, java.sql.Types.INTEGER);
            }

            statement.setString(
                    7,
                    recipe.getCreatedAt().toString());

            statement.setString(
                    8,
                    recipe.getUpdatedAt().toString());

            int affectedRows = statement.executeUpdate();
            if (affectedRows != 1) {
                throw new DatabaseException(
                        "Failed to insert recipe",
                        new SQLException("No rows inserted"));
            }

            try (
                    ResultSet keys =
                            statement.getGeneratedKeys()
            ) {

                if (keys.next()) {

                    recipe.setId(
                            keys.getInt(1));
                }
            }

        } catch (SQLException e) {
            throw new DatabaseException("Failed to save recipe", e);
        }
    }

    @Override
    public void updateRecipe(Recipe recipe) {

        recipe.setUpdatedAt(
                LocalDateTime.now());

        String sql =
                """
                UPDATE recipes
                SET title = ?,
                    description = ?,
                    instructions = ?,
                    preparation_time = ?,
                    servings = ?,
                    category_id = ?,
                    updated_at = ?
                WHERE id = ?
                """;

        try (
                Connection connection =
                        databaseManager.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    recipe.getTitle());

            statement.setString(
                    2,
                    recipe.getDescription());

            statement.setString(
                    3,
                    recipe.getInstructions());

            statement.setInt(
                    4,
                    recipe.getPreparationTime());

            statement.setInt(
                    5,
                    recipe.getServings());

            if (recipe.getCategoryId() > 0) {
                statement.setInt(6, recipe.getCategoryId());
            } else {
                statement.setNull(6, java.sql.Types.INTEGER);
            }

            statement.setString(
                    7,
                    recipe.getUpdatedAt().toString());

            statement.setInt(
                    8,
                    recipe.getId());

            int affectedRows = statement.executeUpdate();

            if (affectedRows != 1) {
                throw new RecipeNotFoundException(recipe.getId());
            }

        } catch (SQLException e) {
            throw new DatabaseException("Failed to update recipe", e);
        }
    }

    @Override
    public void deleteRecipe(Recipe recipe) {

        String sql =
                "DELETE FROM recipes WHERE id = ?";

        try (
                Connection connection =
                        databaseManager.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(
                    1,
                    recipe.getId());

            int affectedRows = statement.executeUpdate();

            if (affectedRows != 1) {
                throw new RecipeNotFoundException(recipe.getId());
            }

        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete recipe", e);
        }
    }

    private LocalDateTime parseTimestamp(String timestamp) {
        try {
            return LocalDateTime.parse(timestamp);
        } catch (DateTimeParseException isoError) {
            try {
                return LocalDateTime.parse(
                        timestamp,
                        DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
            } catch (DateTimeParseException displayError) {
                throw new DatabaseException(
                        "Malformed timestamp in database: " + timestamp,
                        displayError);
            }
        }
    }
}