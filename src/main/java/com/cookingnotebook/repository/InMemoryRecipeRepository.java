package com.cookingnotebook.repository;

import com.cookingnotebook.model.Recipe;

import java.util.ArrayList;
import java.util.List;

public class InMemoryRecipeRepository implements RecipeRepository {
    private final List<Recipe> recipes = new ArrayList<>();
    private int nextId = 1;

    @Override
    public List<Recipe> getAllRecipes() {
        return new ArrayList<>(recipes);
    }

    @Override
    public void saveRecipe(Recipe recipe) {
        if (recipe.getId() == 0) {
            recipe.setId(nextId++);
        }
        recipes.add(recipe);
    }

    @Override
    public void updateRecipe(Recipe recipe) {
        for (int index = 0; index < recipes.size(); index++) {
            if (recipes.get(index).getId() == recipe.getId()) {
                recipes.set(index, recipe);
                return;
            }
        }
    }

    @Override
    public void deleteRecipe(Recipe recipe) {
        recipes.removeIf(candidate -> candidate.getId() == recipe.getId());
    }
}
