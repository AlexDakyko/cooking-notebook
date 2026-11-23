package com.cookingnotebook.repository;

import com.cookingnotebook.model.Photo;

import java.util.List;

public interface PhotoRepository {
    List<Photo> getByRecipeId(int recipeId);
    void savePhoto(Photo photo);
    void deletePhoto(Photo photo);
}
