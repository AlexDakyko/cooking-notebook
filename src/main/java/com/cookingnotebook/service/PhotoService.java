package com.cookingnotebook.service;

import com.cookingnotebook.exception.ValidationException;
import com.cookingnotebook.model.Photo;
import com.cookingnotebook.repository.PhotoRepository;

import java.util.List;

public class PhotoService {
    private static final long MAX_PHOTO_SIZE = 10 * 1024 * 1024;
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
            throw new ValidationException("Данные фото отсутствуют");
        }
        if (photo.getImageData().length > MAX_PHOTO_SIZE) {
            throw new ValidationException(
                    "Размер фото не должен превышать 10 МБ");
        }
        if (!isSupportedMimeType(photo.getMimeType())) {
            throw new ValidationException(
                    "Поддерживаются только JPG, PNG и GIF");
        }
        repository.savePhoto(photo);
    }

    public void deletePhoto(Photo photo) {
        repository.deletePhoto(photo);
    }

    public boolean isSupportedMimeType(String mimeType) {
        return "image/jpeg".equalsIgnoreCase(mimeType)
                || "image/png".equalsIgnoreCase(mimeType)
                || "image/gif".equalsIgnoreCase(mimeType);
    }
}
