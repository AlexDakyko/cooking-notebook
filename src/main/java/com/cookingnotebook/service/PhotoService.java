package com.cookingnotebook.service;

import com.cookingnotebook.exception.ValidationException;
import com.cookingnotebook.model.Photo;
import com.cookingnotebook.repository.PhotoRepository;

import java.util.List;

public class PhotoService {
    private final PhotoRepository repository;

    public PhotoService(PhotoRepository repository) {
        this.repository = repository;
    }

    public List<Photo> getByRecipeId(int recipeId) {
        return repository.getByRecipeId(recipeId);
    }

    public void savePhoto(Photo photo) {
        if (photo == null
                || photo.getRecipeId() <= 0
                || photo.getImageData() == null
                || photo.getImageData().length == 0) {
            throw new ValidationException("Photo data is invalid");
        }
        repository.savePhoto(photo);
    }

    public void deletePhoto(Photo photo) {
        repository.deletePhoto(photo);
    }
}
