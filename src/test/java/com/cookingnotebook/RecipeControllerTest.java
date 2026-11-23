package com.cookingnotebook;

import com.cookingnotebook.controller.RecipeController;
import com.cookingnotebook.model.Ingredient;
import com.cookingnotebook.model.Recipe;
import com.cookingnotebook.repository.InMemoryIngredientRepository;
import com.cookingnotebook.repository.InMemoryPhotoRepository;
import com.cookingnotebook.repository.InMemoryRecipeRepository;
import com.cookingnotebook.service.RecipeService;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import javafx.application.Platform;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(ApplicationExtension.class)
class RecipeControllerTest {
    private InMemoryRecipeRepository recipeRepository;
    private InMemoryIngredientRepository ingredientRepository;
    private RecipeController controller;

    private void moveEditorToBottom(FxRobot robot) {
        ScrollPane scrollPane = robot.lookup(".scroll-pane")
                .queryAs(ScrollPane.class);
        Platform.runLater(() -> scrollPane.setVvalue(1.0));
        WaitForAsyncUtils.waitForFxEvents();
    }

    @BeforeEach
    void resetRepositories() {
        if (recipeRepository != null) {
            recipeRepository.clear();
        }
        if (ingredientRepository != null) {
            ingredientRepository.clear();
        }
        if (controller != null) {
            Platform.runLater(() -> {
                controller.resetEditor();
                controller.getRecipeListForTesting()
                        .getItems()
                        .clear();
            });
            WaitForAsyncUtils.waitForFxEvents();
        }
    }

    @Start
    void start(Stage stage) throws Exception {
        recipeRepository = new InMemoryRecipeRepository();
        ingredientRepository = new InMemoryIngredientRepository();
        controller = new RecipeController(
                new RecipeService(recipeRepository),
                null,
                ingredientRepository,
                new InMemoryPhotoRepository());

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/recipe-list.fxml"));
        loader.setControllerFactory(type ->
                type == RecipeController.class ? controller : null);
        Parent root = loader.load();
        stage.setScene(new Scene(root, 1400, 1000));
        stage.show();
    }

    @Test
    void savesNewRecipeAndKeepsItSelected(FxRobot robot) {
        TextField title = robot.lookup("#recipeTitleField")
                .queryAs(TextField.class);
        Platform.runLater(() -> {
            title.setText("Test Soup");
            controller.saveRecipe(null);
        });
        WaitForAsyncUtils.waitForFxEvents();

        assertEquals(1, recipeRepository.getAllRecipes().size());
        assertEquals(
                "Test Soup",
                recipeRepository.getAllRecipes().get(0).getTitle());
        assertEquals(
                "Test Soup",
                title.getText());

        ListView<Recipe> recipes = robot.lookup("#recipeList")
                .queryAs(ListView.class);
        assertFalse(recipes.getItems().isEmpty());
    }

    @Test
    void stagesIngredientBeforeSaveAndPersistsIt(FxRobot robot) {
        TextField title = robot.lookup("#recipeTitleField")
                .queryAs(TextField.class);
        title.setText("");
        TextField ingredientName = robot.lookup("#ingredientNameField")
                .queryAs(TextField.class);
        TextField ingredientQuantity = robot.lookup("#ingredientQuantityField")
                .queryAs(TextField.class);
        TextField ingredientUnit = robot.lookup("#ingredientUnitField")
                .queryAs(TextField.class);
        Platform.runLater(() -> {
            title.setText("Cake");
            ingredientName.setText("Flour");
            ingredientQuantity.setText("250");
            ingredientUnit.setText("g");
            controller.addIngredient(null);
        });
        WaitForAsyncUtils.waitForFxEvents();

        ListView<Ingredient> staged =
                robot.lookup("#ingredientList")
                        .queryAs(ListView.class);
        assertEquals(1, staged.getItems().size());

        Platform.runLater(() ->
                controller.saveRecipe(null));
        WaitForAsyncUtils.waitForFxEvents();

        assertEquals(1, recipeRepository.getAllRecipes().size());
        assertEquals(
                1,
                ingredientRepository
                        .getByRecipeId(
                                recipeRepository
                                        .getAllRecipes()
                                        .get(0)
                                        .getId())
                        .size());
    }

    @Test
    void rejectsWhitespaceTitle() {
        assertFalse(controller.validateRecipeTitle("   "));
    }

    @Test
    void newRecipeClearsEveryEditorField(FxRobot robot) {
        TextField title = robot.lookup("#recipeTitleField")
                .queryAs(TextField.class);
        TextArea description = robot.lookup("#descriptionArea")
                .queryAs(TextArea.class);
        TextArea instructions = robot.lookup("#instructionsArea")
                .queryAs(TextArea.class);
        TextField preparationTime = robot.lookup("#preparationTimeField")
                .queryAs(TextField.class);
        TextField servings = robot.lookup("#servingsField")
                .queryAs(TextField.class);

        Platform.runLater(() -> {
            title.setText("Old recipe");
            description.setText("Old description");
            instructions.setText("Old instructions");
            preparationTime.setText("30");
            servings.setText("4");
            controller.saveRecipe(null);
        });
        WaitForAsyncUtils.waitForFxEvents();

        Platform.runLater(() -> controller.newRecipe(null));
        WaitForAsyncUtils.waitForFxEvents();

        assertEquals("", title.getText());
        assertEquals("", description.getText());
        assertEquals("", instructions.getText());
        assertEquals("", preparationTime.getText());
        assertEquals("", servings.getText());
        assertTrue(controller.validateRecipeTitle("New"));
    }

    @Test
    void switchingRecipesLoadsSelectedValuesAndUpdateRefreshesTimestamp(
            FxRobot robot) {
        Recipe first = new Recipe();
        first.setTitle("First");
        first.setDescription("First description");
        first.setInstructions("First instructions");
        first.setPreparationTime(10);
        first.setServings(2);
        Recipe second = new Recipe();
        second.setTitle("Second");
        second.setDescription("Second description");
        second.setInstructions("Second instructions");
        second.setPreparationTime(20);
        second.setServings(5);

        Platform.runLater(() -> {
            recipeRepository.saveRecipe(first);
            recipeRepository.saveRecipe(second);
            controller.resetEditor();
            ListView<Recipe> list =
                    controller.getRecipeListForTesting();
            list.getItems().setAll(first, second);
            list.getSelectionModel().select(first);
        });
        WaitForAsyncUtils.waitForFxEvents();

        TextField title = robot.lookup("#recipeTitleField")
                .queryAs(TextField.class);
        TextArea description = robot.lookup("#descriptionArea")
                .queryAs(TextArea.class);
        assertEquals("First", title.getText());
        assertEquals("First description", description.getText());

        Platform.runLater(() -> {
            title.setText("First updated");
            controller.saveRecipe(null);
        });
        WaitForAsyncUtils.waitForFxEvents();

        Label updated = robot.lookup("#updatedAtLabel")
                .queryAs(Label.class);
        assertFalse(updated.getText().contains("-"));
        assertEquals(
                "First updated",
                recipeRepository.getAllRecipes().get(0).getTitle());
    }

    @Test
    void newRecipeDoesNotLeavePreviousRecipeSelected(FxRobot robot) {
        Recipe existing = new Recipe();
        existing.setTitle("Existing");
        existing.setDescription("Existing description");
        recipeRepository.saveRecipe(existing);

        Platform.runLater(() -> {
            controller.resetEditor();
            TextField title = robot.lookup("#recipeTitleField")
                    .queryAs(TextField.class);
            controller.getRecipeListForTesting()
                    .getItems()
                    .setAll(existing);
            controller.getRecipeListForTesting()
                    .getSelectionModel()
                    .select(existing);
            controller.newRecipe(null);
        });
        WaitForAsyncUtils.waitForFxEvents();

        assertEquals(
                "",
                robot.lookup("#recipeTitleField")
                        .queryAs(TextField.class)
                        .getText());
        assertNull(
                controller.getRecipeListForTesting()
                        .getSelectionModel()
                        .getSelectedItem());
    }
}
