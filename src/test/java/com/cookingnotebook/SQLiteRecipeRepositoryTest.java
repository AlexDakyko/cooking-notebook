package com.cookingnotebook;

import com.cookingnotebook.database.DatabaseManager;
import com.cookingnotebook.exception.RecipeNotFoundException;
import com.cookingnotebook.model.Recipe;
import com.cookingnotebook.repository.SQLiteRecipeRepository;
import com.cookingnotebook.model.Ingredient;
import com.cookingnotebook.model.Photo;
import com.cookingnotebook.model.Category;
import com.cookingnotebook.repository.SQLiteIngredientRepository;
import com.cookingnotebook.repository.SQLiteCategoryRepository;
import com.cookingnotebook.repository.SQLitePhotoRepository;
import com.cookingnotebook.repository.SQLiteSettingsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SQLiteRecipeRepositoryTest {

    @TempDir
    Path tempDirectory;

    private DatabaseManager databaseManager;
    private SQLiteRecipeRepository repository;

    @BeforeEach
    void setUp() throws Exception {
        Path databaseFile = tempDirectory.resolve("recipes.db");
        databaseManager =
                new DatabaseManager("jdbc:sqlite:" + databaseFile.toAbsolutePath());
        databaseManager.initializeDatabase();
        repository = new SQLiteRecipeRepository(databaseManager);
    }

    @Test
    void initializationCreatesRecipesTable() throws Exception {
        try (Connection connection = databaseManager.getConnection();
             ResultSet tables = connection.getMetaData()
                     .getTables(null, null, "recipes", null)) {
            assertEquals(true, tables.next());
        }
    }

    @Test
    void saveUpdateAndReloadRecipe() {
        Recipe recipe = new Recipe();
        recipe.setTitle("  Borscht  ");
        recipe.setDescription("A red soup");

        repository.saveRecipe(recipe);

        assertNotEquals(0, recipe.getId());
        assertEquals(1, repository.getAllRecipes().size());

        Recipe reloaded = repository.getAllRecipes().get(0);
        assertEquals("  Borscht  ", reloaded.getTitle());
        assertEquals("A red soup", reloaded.getDescription());

        reloaded.setTitle("Updated Borscht");
        repository.updateRecipe(reloaded);

        assertEquals(
                "Updated Borscht",
                repository.getAllRecipes().get(0).getTitle());
    }

    @Test
    void deleteAndUnknownUpdateAndDeleteAreHandled() {
        Recipe recipe = new Recipe();
        recipe.setTitle("To delete");
        repository.saveRecipe(recipe);

        repository.deleteRecipe(recipe);
        assertEquals(0, repository.getAllRecipes().size());

        Recipe missing = new Recipe();
        missing.setId(-100);
        missing.setTitle("Missing");

        assertThrows(
                RecipeNotFoundException.class,
                () -> repository.updateRecipe(missing));
        assertThrows(
                RecipeNotFoundException.class,
                () -> repository.deleteRecipe(missing));
    }

    @Test
    void persistsCompleteRecipeFields() {
        Recipe recipe = new Recipe();
        recipe.setTitle("Soup");
        recipe.setDescription("Description");
        recipe.setInstructions("Boil and serve");
        recipe.setPreparationTime(30);
        recipe.setServings(4);

        repository.saveRecipe(recipe);
        recipe.setInstructions("Updated instructions");
        recipe.setPreparationTime(25);
        recipe.setServings(6);
        repository.updateRecipe(recipe);

        Recipe reloaded = repository.getAllRecipes().get(0);
        assertEquals("Updated instructions", reloaded.getInstructions());
        assertEquals(25, reloaded.getPreparationTime());
        assertEquals(6, reloaded.getServings());
    }

    @Test
    void upgradesLegacyDatabaseAndCreatesAllTables() throws Exception {
        Path legacyFile = tempDirectory.resolve("legacy.db");
        DatabaseManager legacyManager =
                new DatabaseManager("jdbc:sqlite:" + legacyFile.toAbsolutePath());

        try (Connection connection = legacyManager.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("""
                    CREATE TABLE recipes (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        title TEXT NOT NULL,
                        description TEXT,
                        created_at TEXT,
                        updated_at TEXT,
                        category TEXT
                    )
                    """);
            statement.execute("""
                    INSERT INTO recipes(title, description, created_at, updated_at, category)
                    VALUES('Legacy Soup', 'Old description', '2026-01-01T10:00',
                           '2026-01-02T10:00', 'Soup')
                    """);
        }

        legacyManager.initializeDatabase();

        SQLiteRecipeRepository legacyRepository =
                new SQLiteRecipeRepository(legacyManager);
        Recipe legacyRecipe = legacyRepository.getAllRecipes().get(0);
        assertEquals("Legacy Soup", legacyRecipe.getTitle());
        assertEquals("Old description", legacyRecipe.getDescription());
        assertNotEquals(0, legacyRecipe.getCategoryId());

        try (Connection connection = legacyManager.getConnection();
             Statement statement = connection.createStatement();
             ResultSet ingredients = connection.getMetaData()
                     .getTables(null, null, "ingredients", null);
             ResultSet photos = connection.getMetaData()
                     .getTables(null, null, "photos", null);
             ResultSet settings = connection.getMetaData()
                     .getTables(null, null, "settings", null)) {
            assertEquals(true, ingredients.next());
            assertEquals(true, photos.next());
            assertEquals(true, settings.next());
        }
    }

    @Test
    void persistsCategoryIngredientPhotoAndSettings() {
        Category category = new Category("Dessert");
        SQLiteCategoryRepository categoryRepository =
                new SQLiteCategoryRepository(databaseManager);
        categoryRepository.saveCategory(category);

        Recipe recipe = new Recipe();
        recipe.setTitle("Cake");
        recipe.setDescription("Sweet");
        recipe.setCategoryId(category.getId());
        repository.saveRecipe(recipe);

        Ingredient ingredient = new Ingredient();
        ingredient.setRecipeId(recipe.getId());
        ingredient.setName("Flour");
        ingredient.setQuantity(250.0);
        ingredient.setUnit("g");
        SQLiteIngredientRepository ingredientRepository =
                new SQLiteIngredientRepository(databaseManager);
        ingredientRepository.saveIngredient(ingredient);

        assertEquals(
                "Flour",
                ingredientRepository.getByRecipeId(recipe.getId()).get(0).getName());

        Photo photo = new Photo();
        photo.setRecipeId(recipe.getId());
        photo.setFileName("cake.jpg");
        photo.setMimeType("image/jpeg");
        photo.setImageData(new byte[]{1, 2, 3});
        SQLitePhotoRepository photoRepository =
                new SQLitePhotoRepository(databaseManager);
        photoRepository.savePhoto(photo);

        assertEquals(
                1,
                photoRepository.getByRecipeId(recipe.getId()).size());

        SQLiteSettingsRepository settingsRepository =
                new SQLiteSettingsRepository(databaseManager);
        settingsRepository.set("theme", "dark");
        assertEquals("dark", settingsRepository.get("theme").orElse(""));
    }

    @Test
    void deletingRecipeCascadeDeletesIngredientsAndPhotos() {
        Recipe recipe = new Recipe();
        recipe.setTitle("Cascade");
        repository.saveRecipe(recipe);

        Ingredient ingredient = new Ingredient();
        ingredient.setRecipeId(recipe.getId());
        ingredient.setName("Water");
        SQLiteIngredientRepository ingredientRepository =
                new SQLiteIngredientRepository(databaseManager);
        ingredientRepository.saveIngredient(ingredient);

        Photo photo = new Photo();
        photo.setRecipeId(recipe.getId());
        photo.setImageData(new byte[]{1});
        SQLitePhotoRepository photoRepository =
                new SQLitePhotoRepository(databaseManager);
        photoRepository.savePhoto(photo);

        repository.deleteRecipe(recipe);

        assertEquals(0, ingredientRepository.getByRecipeId(recipe.getId()).size());
        assertEquals(0, photoRepository.getByRecipeId(recipe.getId()).size());
    }
}
