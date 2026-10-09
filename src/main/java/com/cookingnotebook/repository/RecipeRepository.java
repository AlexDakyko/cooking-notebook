package com.cookingnotebook.repository;

import com.cookingnotebook.model.Recipe;
import java.util.List;

public interface RecipeRepository {
    List<Recipe> getAllRecipes();
    void saveRecipe(Recipe recipe);
    void deleteRecipe(Recipe recipe);
}