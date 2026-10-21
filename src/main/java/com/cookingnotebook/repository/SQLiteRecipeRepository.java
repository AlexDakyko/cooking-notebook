package com.cookingnotebook.repository;

import com.cookingnotebook.database.DatabaseManager;
import com.cookingnotebook.model.Recipe;

import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import java.sql.ResultSet;
import java.sql.Statement;


public class SQLiteRecipeRepository
        implements RecipeRepository {

    private final DatabaseManager databaseManager;

    public SQLiteRecipeRepository() {
        this.databaseManager =
                new DatabaseManager();
    }

    @Override
public List<Recipe> getAllRecipes() {

    List<Recipe> recipes = new ArrayList<>();

    String sql = "SELECT id, title, description FROM recipes";

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

    String sql =
            "INSERT INTO recipes(title, description) VALUES(?, ?)";

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

        statement.executeUpdate();

        try (ResultSet keys =
                     statement.getGeneratedKeys()) {

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
	
	@Override
public void updateRecipe(Recipe recipe) {

    String sql =
            """
            UPDATE recipes
            SET title = ?,
                description = ?
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

        statement.setInt(
                3,
                recipe.getId());

        statement.executeUpdate();

    } catch (SQLException e) {

        throw new RuntimeException(
                "Failed to update recipe",
                e);
    }
}
}