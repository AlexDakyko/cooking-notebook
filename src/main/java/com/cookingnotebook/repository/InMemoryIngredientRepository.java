package com.cookingnotebook.repository;

import com.cookingnotebook.model.Ingredient;

import java.util.ArrayList;
import java.util.List;

public class InMemoryIngredientRepository implements IngredientRepository {
    private final List<Ingredient> ingredients = new ArrayList<>();
    private int nextId = 1;

    @Override
    public List<Ingredient> getByRecipeId(int recipeId) {
        List<Ingredient> matches = new ArrayList<>();
        for (Ingredient ingredient : ingredients) {
            if (ingredient.getRecipeId() == recipeId) {
                matches.add(ingredient);
            }
        }
        return matches;
    }

    @Override
    public void saveIngredient(Ingredient ingredient) {
        if (ingredient.getId() == 0) {
            ingredient.setId(nextId++);
        }
        ingredients.add(ingredient);
    }

    @Override
    public void updateIngredient(Ingredient ingredient) {
        for (int index = 0; index < ingredients.size(); index++) {
            if (ingredients.get(index).getId() == ingredient.getId()) {
                ingredients.set(index, ingredient);
                return;
            }
        }
    }

    @Override
    public void deleteIngredient(Ingredient ingredient) {
        ingredients.removeIf(candidate ->
                candidate.getId() == ingredient.getId());
    }
}
