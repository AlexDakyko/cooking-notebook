package com.cookingnotebook.repository;

import com.cookingnotebook.model.Photo;

import java.util.ArrayList;
import java.util.List;

public class InMemoryPhotoRepository implements PhotoRepository {
    private final List<Photo> photos = new ArrayList<>();
    private int nextId = 1;

    @Override
    public List<Photo> getByRecipeId(int recipeId) {
        List<Photo> matches = new ArrayList<>();
        for (Photo photo : photos) {
            if (photo.getRecipeId() == recipeId) {
                matches.add(photo);
            }
        }
        return matches;
    }

    @Override
    public void savePhoto(Photo photo) {
        if (photo.getId() == 0) {
            photo.setId(nextId++);
        }
        photos.add(photo);
    }

    @Override
    public void deletePhoto(Photo photo) {
        photos.removeIf(candidate -> candidate.getId() == photo.getId());
    }
}
