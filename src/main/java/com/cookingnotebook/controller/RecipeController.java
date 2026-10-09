package com.cookingnotebook.controller;

import com.cookingnotebook.model.Recipe;
import com.cookingnotebook.service.RecipeService;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.io.IOException;

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

    public void initialize() {
        recipeService = new RecipeService();
        recipeTable.setItems(recipeService.getAllRecipes());
    }

    public void saveRecipe(ActionEvent event) {
        String title = recipeTitleField.getText();
        if (!title.isEmpty()) {
            Recipe recipe = new Recipe(title, "", "", 0, 0, 1, 1, null);
            recipeService.saveRecipe(recipe);
            recipeTable.refresh();
            recipeTitleField.clear();
        }
    }

    public void deleteRecipe(ActionEvent event) {
        Recipe selectedRecipe = recipeTable.getSelectionModel().getSelectedItem();
        if (selectedRecipe != null) {
            recipeService.deleteRecipe(selectedRecipe);
            recipeTable.getItems().remove(selectedRecipe);
        }
    }
}