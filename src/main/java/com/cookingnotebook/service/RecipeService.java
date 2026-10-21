package com.cookingnotebook.service;

import com.cookingnotebook.model.Recipe;
import com.cookingnotebook.repository.InMemoryRecipeRepository;
import com.cookingnotebook.repository.RecipeRepository;

import java.util.List;
import com.cookingnotebook.repository.SQLiteRecipeRepository;

public class RecipeService {

    private final RecipeRepository repository;

    public RecipeService() {
        repository = new SQLiteRecipeRepository();
    }

    public void saveRecipe(Recipe recipe) {
        repository.saveRecipe(recipe);
    }

    public List<Recipe> getAllRecipes() {
        return repository.getAllRecipes();
    }

    public void deleteRecipe(Recipe recipe) {
        repository.deleteRecipe(recipe);
    }
	
	public List<Recipe> loadRecipes() {
    return repository.getAllRecipes();
}

public void updateRecipe(Recipe recipe) {
    repository.updateRecipe(recipe);
}

}