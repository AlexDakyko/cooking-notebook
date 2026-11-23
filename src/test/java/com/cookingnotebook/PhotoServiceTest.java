package com.cookingnotebook;

import com.cookingnotebook.exception.ValidationException;
import com.cookingnotebook.model.Photo;
import com.cookingnotebook.repository.InMemoryPhotoRepository;
import com.cookingnotebook.service.PhotoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PhotoServiceTest {
    private PhotoService service;
    private InMemoryPhotoRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryPhotoRepository();
        service = new PhotoService(repository);
    }

    @Test
    void validatesSupportedPhotoTypes() {
        Photo unsupported = new Photo();
        unsupported.setRecipeId(1);
        unsupported.setFileName("document.pdf");
        unsupported.setMimeType("application/pdf");
        unsupported.setImageData(new byte[]{1, 2, 3});

        assertThrows(
                ValidationException.class,
                () -> service.savePhoto(unsupported));
    }

    @Test
    void rejectsPhotosLargerThanTenMegabytes() {
        Photo oversized = new Photo();
        oversized.setRecipeId(1);
        oversized.setFileName("large.jpg");
        oversized.setMimeType("image/jpeg");
        oversized.setImageData(
                new byte[10 * 1024 * 1024 + 1]);

        assertThrows(
                ValidationException.class,
                () -> service.savePhoto(oversized));
    }

    @Test
    void storesAndLoadsPhotoBLOB() {
        byte[] bytes = new byte[]{-1, 0, 1, 2};
        Photo photo = new Photo();
        photo.setRecipeId(7);
        photo.setFileName("recipe.png");
        photo.setMimeType("image/png");
        photo.setImageData(bytes);

        service.savePhoto(photo);

        assertEquals(1, service.getByRecipeId(7).size());
        assertArrayEquals(
                bytes,
                service.getByRecipeId(7).get(0).getImageData());
    }
}
