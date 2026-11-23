package com.cookingnotebook;

import com.cookingnotebook.exception.ValidationException;
import com.cookingnotebook.model.Ingredient;
import com.cookingnotebook.repository.InMemoryIngredientRepository;
import com.cookingnotebook.service.IngredientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class IngredientServiceTest {
    private IngredientService service;

    @BeforeEach
    void setUp() {
        service = new IngredientService(
                new InMemoryIngredientRepository());
    }

    @Test
    void validatesIngredient() {
        Ingredient missingName = new Ingredient();
        missingName.setRecipeId(1);
        assertThrows(
                ValidationException.class,
                () -> service.saveIngredient(missingName));

        Ingredient negativeQuantity = new Ingredient();
        negativeQuantity.setRecipeId(1);
        negativeQuantity.setName("Milk");
        negativeQuantity.setQuantity(-1.0);
        assertThrows(
                ValidationException.class,
                () -> service.saveIngredient(negativeQuantity));
    }

    @Test
    void savesAndUpdatesIngredient() {
        Ingredient ingredient = new Ingredient();
        ingredient.setRecipeId(1);
        ingredient.setName("Milk");
        ingredient.setQuantity(500.0);
        ingredient.setUnit("ml");

        service.saveIngredient(ingredient);
        assertEquals(1, service.getByRecipeId(1).size());

        ingredient.setQuantity(250.0);
        service.updateIngredient(ingredient);
        assertEquals(
                250.0,
                service.getByRecipeId(1).get(0).getQuantity());
    }
}
