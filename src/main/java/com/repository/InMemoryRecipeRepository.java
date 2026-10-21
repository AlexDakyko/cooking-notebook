package com.cookingnotebook.repository;

import com.cookingnotebook.model.Recipe;

import java.util.ArrayList;
import java.util.List;

public class InMemoryRecipeRepository
        implements RecipeRepository {

    private final List<Recipe> recipes =
            new ArrayList<>();

    @Override
    public List<Recipe> getAllRecipes() {
        return recipes;
    }

    @Override
    public void saveRecipe(Recipe recipe) {
        recipes.add(recipe);
    }

    @Override
    public void deleteRecipe(Recipe recipe) {
        recipes.remove(recipe);
    }
	@Override
public void updateRecipe(Recipe recipe) {
}

}