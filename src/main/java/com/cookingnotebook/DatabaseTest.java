package com.cookingnotebook.database;

import com.cookingnotebook.model.Recipe;
import com.cookingnotebook.repository.SQLiteRecipeRepository;

public class DatabaseTest {

    public static void main(String[] args) {

    SQLiteRecipeRepository repository =
            new SQLiteRecipeRepository();

    repository.getAllRecipes()
            .forEach(recipe ->
                    System.out.println(
                            recipe.getId()
                                    + " | "
                                    + recipe.getTitle()
                                    + " | "
                                    + recipe.getDescription()));
}

}