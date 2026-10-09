package com.cookingnotebook.service;

import com.cookingnotebook.model.Recipe;
import java.util.ArrayList;
import java.util.List;

public class RecipeService {
    private List<Recipe> recipes = new ArrayList<>();

    public void saveRecipe(Recipe recipe) {
        recipes.add(recipe);
    }

    public List<Recipe> getAllRecipes() {
        return recipes;
    }

    public void deleteRecipe(Recipe recipe) {
        recipes.remove(recipe);
    }
}