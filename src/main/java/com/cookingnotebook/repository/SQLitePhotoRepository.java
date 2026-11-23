package com.cookingnotebook.repository;

import com.cookingnotebook.database.DatabaseManager;
import com.cookingnotebook.exception.DatabaseException;
import com.cookingnotebook.exception.RecipeNotFoundException;
import com.cookingnotebook.model.Photo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SQLitePhotoRepository implements PhotoRepository {
    private final DatabaseManager databaseManager;

    public SQLitePhotoRepository(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    @Override
    public List<Photo> getByRecipeId(int recipeId) {
        List<Photo> photos = new ArrayList<>();
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                     SELECT id, recipe_id, file_name, mime_type, image_data
                     FROM photos
                     WHERE recipe_id = ?
                     """)) {
            statement.setInt(1, recipeId);
            try (ResultSet rows = statement.executeQuery()) {
                while (rows.next()) {
                    Photo photo = new Photo();
                    photo.setId(rows.getInt("id"));
                    photo.setRecipeId(rows.getInt("recipe_id"));
                    photo.setFileName(rows.getString("file_name"));
                    photo.setMimeType(rows.getString("mime_type"));
                    photo.setImageData(rows.getBytes("image_data"));
                    photos.add(photo);
                }
            }
        } catch (SQLException exception) {
            throw new DatabaseException("Failed to load photos", exception);
        }
        return photos;
    }

    @Override
    public void savePhoto(Photo photo) {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                     INSERT INTO photos(recipe_id, file_name, mime_type, image_data)
                     VALUES(?, ?, ?, ?)
                     """, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, photo.getRecipeId());
            statement.setString(2, photo.getFileName());
            statement.setString(3, photo.getMimeType());
            statement.setBytes(4, photo.getImageData());
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    photo.setId(keys.getInt(1));
                }
            }
        } catch (SQLException exception) {
            throw new DatabaseException("Failed to save photo", exception);
        }
    }

    @Override
    public void deletePhoto(Photo photo) {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "DELETE FROM photos WHERE id = ?")) {
            statement.setInt(1, photo.getId());
            if (statement.executeUpdate() != 1) {
                throw new RecipeNotFoundException(photo.getId());
            }
        } catch (SQLException exception) {
            throw new DatabaseException("Failed to delete photo", exception);
        }
    }
}
