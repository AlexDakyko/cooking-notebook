package com.cookingnotebook.service;

import com.cookingnotebook.exception.ValidationException;
import com.cookingnotebook.model.Ingredient;
import com.cookingnotebook.repository.IngredientRepository;

import java.util.List;

public class IngredientService {
    private final IngredientRepository repository;

    public IngredientService(IngredientRepository repository) {
        this.repository = repository;
    }

    public List<Ingredient> getByRecipeId(int recipeId) {
        return repository.getByRecipeId(recipeId);
    }

    public void saveIngredient(Ingredient ingredient) {
        validateIngredient(ingredient);
        repository.saveIngredient(ingredient);
    }

    public void updateIngredient(Ingredient ingredient) {
        validateIngredient(ingredient);
        repository.updateIngredient(ingredient);
    }

    public void deleteIngredient(Ingredient ingredient) {
        repository.deleteIngredient(ingredient);
    }

    private void validateIngredient(Ingredient ingredient) {
        if (ingredient == null
                || ingredient.getRecipeId() <= 0
                || ingredient.getName() == null
                || ingredient.getName().trim().isEmpty()) {
            throw new ValidationException(
                    "Название ингредиента и выбранный рецепт обязательны");
        }
        if (ingredient.getQuantity() != null
                && ingredient.getQuantity() < 0) {
            throw new ValidationException(
                    "Количество ингредиента не может быть отрицательным");
        }
    }
}
