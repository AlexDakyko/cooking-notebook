package com.cookingnotebook.controller;

import com.cookingnotebook.model.Recipe;
import com.cookingnotebook.service.RecipeService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class RecipeController {

    private Recipe selectedRecipe;

    @FXML
    private Label modeLabel;

    @FXML
    private TextField recipeTitleField;

    @FXML
    private TextArea descriptionArea;

    @FXML
    private Button saveButton;

    @FXML
    private Button deleteButton;

    @FXML
    private ListView<Recipe> recipeList;

    private RecipeService recipeService;

    private final ObservableList<Recipe> recipes =
            FXCollections.observableArrayList();

    @FXML
    public void initialize() {

        recipeService = new RecipeService();

        recipeList.setItems(recipes);
		recipes.addAll(
        recipeService.loadRecipes());

        recipeList.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, oldValue, newValue) -> {

                    if (newValue != null) {

                        selectedRecipe = newValue;

                        modeLabel.setText("Редактирование рецепта");

                        recipeTitleField.setText(
                                newValue.getTitle());

                        descriptionArea.setText(
                                newValue.getDescription());
                    }
                });
    }

    public boolean validateRecipeTitle(String title) {
        return title != null && !title.isBlank();
    }

    @FXML
    public void saveRecipe(ActionEvent event) {

        String title = recipeTitleField.getText();

        if (!validateRecipeTitle(title)) {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Validation Error");
            alert.setHeaderText("Invalid Recipe Title");
            alert.setContentText(
                    "Recipe title cannot be empty or contain only whitespace.");

            alert.showAndWait();
            return;
        }

        String description = descriptionArea.getText();

        if (selectedRecipe == null) {

            Recipe recipe = new Recipe(
                    title.trim(),
                    description,
                    "",
                    0,
                    0,
                    1,
                    1
            );

            recipeService.saveRecipe(recipe);

            recipes.add(recipe);

        } else {

            selectedRecipe.setTitle(title.trim());
selectedRecipe.setDescription(description);

recipeService.updateRecipe(selectedRecipe);

recipeList.refresh();
        }

        recipeTitleField.clear();
        descriptionArea.clear();

        selectedRecipe = null;

        modeLabel.setText("Новый рецепт");

        recipeList.getSelectionModel().clearSelection();
    }

    @FXML
    public void deleteRecipe(ActionEvent event) {

        Recipe recipeToDelete =
                recipeList.getSelectionModel().getSelectedItem();

        if (recipeToDelete == null) {

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Удаление рецепта");
            alert.setHeaderText(null);
            alert.setContentText("Сначала выберите рецепт.");

            alert.showAndWait();
            return;
        }

        recipeService.deleteRecipe(recipeToDelete);

        recipes.remove(recipeToDelete);

        recipeTitleField.clear();
        descriptionArea.clear();

        this.selectedRecipe = null;

        recipeList.getSelectionModel().clearSelection();

        modeLabel.setText("Новый рецепт");
    }
}