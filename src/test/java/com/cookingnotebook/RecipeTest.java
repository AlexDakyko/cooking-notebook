package com.cookingnotebook;

import com.cookingnotebook.controller.RecipeController;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RecipeTest {

    @Test
    public void testValidateRecipeTitleNull() {

        RecipeController controller = new RecipeController();

        assertFalse(
                controller.validateRecipeTitle(null),
                "Should return false for null title");
    }

    @Test
    public void testValidateRecipeTitleEmpty() {

        RecipeController controller = new RecipeController();

        assertFalse(
                controller.validateRecipeTitle(""),
                "Should return false for empty title");
    }

    @Test
    public void testValidateRecipeTitleWhitespace() {

        RecipeController controller = new RecipeController();

        assertFalse(
                controller.validateRecipeTitle("   "),
                "Should return false for whitespace-only title");
    }

    @Test
    public void testValidateRecipeTitleValid() {

        RecipeController controller = new RecipeController();

        assertTrue(
                controller.validateRecipeTitle("Sample Recipe"),
                "Should return true for valid title");
    }
}