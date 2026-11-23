package com.cookingnotebook.exception;

public class RecipeNotFoundException extends RuntimeException {
    public RecipeNotFoundException(int id) {
        super("Recipe not found: " + id);
    }
}
