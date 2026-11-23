package com.cookingnotebook.repository;

import com.cookingnotebook.model.Ingredient;

import java.util.List;

public interface IngredientRepository {
    List<Ingredient> getByRecipeId(int recipeId);
    void saveIngredient(Ingredient ingredient);
    void updateIngredient(Ingredient ingredient);
    void deleteIngredient(Ingredient ingredient);
}
