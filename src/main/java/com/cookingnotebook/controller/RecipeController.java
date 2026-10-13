package com.cookingnotebook.controller;

import com.cookingnotebook.model.Recipe;
import com.cookingnotebook.service.RecipeService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class RecipeController {

    @FXML
    private TableView<Recipe> recipeTable;

    @FXML
    private TextField recipeTitleField;

    @FXML
    private Button saveButton;

    @FXML
    private Button deleteButton;

    private RecipeService recipeService;

    @FXML
    public void initialize() {
        recipeService = new RecipeService();
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

        Recipe recipe = new Recipe(
                title.trim(),
                "",
                "",
                0,
                0,
                1,
                1
        );

        recipeService.saveRecipe(recipe);

        recipeTitleField.clear();
    }

    @FXML
    public void deleteRecipe(ActionEvent event) {

        Recipe selectedRecipe =
                recipeTable.getSelectionModel().getSelectedItem();

        if (selectedRecipe != null) {
            recipeService.deleteRecipe(selectedRecipe);
        }
    }
}
