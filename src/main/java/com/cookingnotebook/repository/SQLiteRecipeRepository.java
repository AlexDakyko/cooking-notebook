package com.cookingnotebook.repository;

import com.cookingnotebook.database.DatabaseManager;
import com.cookingnotebook.model.Recipe;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

public class SQLiteRecipeRepository
        implements RecipeRepository {

    private final DatabaseManager databaseManager;

    public SQLiteRecipeRepository() {
        this.databaseManager =
                new DatabaseManager();
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

                String createdAt =
                        resultSet.getString("created_at");

                if (createdAt != null
                        && !createdAt.isBlank()) {

                    recipe.setCreatedAt(
                            LocalDateTime.parse(createdAt));
                }

                String updatedAt =
                        resultSet.getString("updated_at");

                if (updatedAt != null
                        && !updatedAt.isBlank()) {

                    recipe.setUpdatedAt(
                            LocalDateTime.parse(updatedAt));
                }

                recipes.add(recipe);
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Failed to load recipes",
                    e);
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
                    created_at,
                    updated_at
                )
                VALUES(?, ?, ?, ?)
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
                    recipe.getCreatedAt().toString());

            statement.setString(
                    4,
                    recipe.getUpdatedAt().toString());

            statement.executeUpdate();

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

            throw new RuntimeException(
                    "Failed to save recipe",
                    e);
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
                    recipe.getUpdatedAt().toString());

            statement.setInt(
                    4,
                    recipe.getId());

            statement.executeUpdate();

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Failed to update recipe",
                    e);
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

            statement.executeUpdate();

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Failed to delete recipe",
                    e);
        }
    }
}