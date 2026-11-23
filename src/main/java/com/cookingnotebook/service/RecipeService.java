package com.cookingnotebook.service;

import com.cookingnotebook.model.Recipe;
import com.cookingnotebook.exception.ValidationException;
import com.cookingnotebook.repository.RecipeRepository;
import com.cookingnotebook.repository.IngredientRepository;
import com.cookingnotebook.repository.CategoryRepository;

import java.util.ArrayList;
import java.util.List;
import com.cookingnotebook.repository.SQLiteRecipeRepository;
import java.util.Locale;

public class RecipeService {

    private final RecipeRepository repository;
    private final CategoryRepository categoryRepository;
    private final IngredientRepository ingredientRepository;

    public RecipeService() {
        this(new SQLiteRecipeRepository());
    }

    public RecipeService(RecipeRepository repository) {
        this(repository, null, null);
    }

    public RecipeService(
            RecipeRepository repository,
            CategoryRepository categoryRepository,
            IngredientRepository ingredientRepository) {
        this.repository = repository;
        this.categoryRepository = categoryRepository;
        this.ingredientRepository = ingredientRepository;
    }

    public void saveRecipe(Recipe recipe) {
        validateRecipe(recipe);
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
validateRecipe(recipe);
    repository.updateRecipe(recipe);
}

public void validateRecipe(Recipe recipe) {
    if (recipe == null
            || recipe.getTitle() == null
            || recipe.getTitle().trim().isEmpty()) {
        throw new ValidationException(
                "Название рецепта не может быть пустым");
    }
    if (recipe.getTitle().trim().length() > 255) {
        throw new ValidationException(
                "Название рецепта не может быть длиннее 255 символов");
    }
    if (recipe.getPreparationTime() < 0) {
        throw new ValidationException(
                "Время подготовки не может быть отрицательным");
    }
    if (recipe.getServings() < 0) {
        throw new ValidationException(
                "Количество порций не может быть отрицательным");
    }
}

public List<Recipe> searchByTitle(String searchText) {
    String query = searchText == null
            ? ""
            : searchText.trim().toLowerCase(Locale.ROOT);
    if (query.isEmpty()) {
        return new ArrayList<>(repository.getAllRecipes());
    }

    List<Recipe> matches = new ArrayList<>();
    for (Recipe recipe : repository.getAllRecipes()) {
        String title = recipe.getTitle() == null
                ? ""
                : recipe.getTitle().toLowerCase(Locale.ROOT);
        if (title.contains(query)) {
            matches.add(recipe);
        }
    }
    return matches;
}

public List<Recipe> filterByCategory(int categoryId) {
    List<Recipe> matches = new ArrayList<>();
    for (Recipe recipe : repository.getAllRecipes()) {
        if (recipe.getCategoryId() == categoryId) {
            matches.add(recipe);
        }
    }
    return matches;
}

public List<Recipe> filterByCategoryAndSearch(
        int categoryId,
        String searchText) {
    String query = searchText == null
            ? ""
            : searchText.trim().toLowerCase(Locale.ROOT);
    List<Recipe> matches = new ArrayList<>();

    for (Recipe recipe : repository.getAllRecipes()) {
        if (recipe.getCategoryId() != categoryId) {
            continue;
        }
        if (query.isEmpty()) {
            matches.add(recipe);
            continue;
        }
        String title = recipe.getTitle() == null
                ? ""
                : recipe.getTitle().toLowerCase(Locale.ROOT);
        if (title.contains(query)) {
            matches.add(recipe);
        }
    }
    return matches;
}

}