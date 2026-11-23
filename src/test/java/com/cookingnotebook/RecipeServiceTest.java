package com.cookingnotebook;

import com.cookingnotebook.exception.ValidationException;
import com.cookingnotebook.model.Recipe;
import com.cookingnotebook.repository.InMemoryRecipeRepository;
import com.cookingnotebook.service.RecipeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RecipeServiceTest {
    private RecipeService service;
    private InMemoryRecipeRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryRecipeRepository();
        service = new RecipeService(repository);
    }

    @Test
    void validatesRequiredTitleAndNumericFields() {
        Recipe recipe = new Recipe();
        recipe.setTitle(" ");
        assertThrows(ValidationException.class,
                () -> service.saveRecipe(recipe));

        recipe.setTitle("Valid");
        recipe.setPreparationTime(-1);
        assertThrows(ValidationException.class,
                () -> service.saveRecipe(recipe));
    }

    @Test
    void searchesTitlesCaseInsensitivelyAndFiltersCategory() {
        Recipe soup = new Recipe();
        soup.setTitle("Tomato Soup");
        soup.setCategoryId(2);
        service.saveRecipe(soup);

        Recipe cake = new Recipe();
        cake.setTitle("Chocolate Cake");
        cake.setCategoryId(3);
        service.saveRecipe(cake);

        assertEquals(1, service.searchByTitle("SOUP").size());
        assertEquals(1, service.filterByCategory(3).size());
        assertEquals(2, service.searchByTitle("").size());
    }
}
