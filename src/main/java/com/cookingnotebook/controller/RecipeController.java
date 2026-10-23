package com.cookingnotebook.controller;

import com.cookingnotebook.model.Recipe;
import com.cookingnotebook.service.RecipeService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import java.util.Comparator;

public class RecipeController {

    private Recipe selectedRecipe;
    private boolean hasUnsavedChanges;
    private boolean loadingRecipe;

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
    private TextField searchField;
	
	@FXML
private ComboBox<String> sortComboBox;

    @FXML
    private ListView<Recipe> recipeList;

    private RecipeService recipeService;

    private final ObservableList<Recipe> recipes =
            FXCollections.observableArrayList();

    private final ObservableList<Recipe> filteredRecipes =
            FXCollections.observableArrayList();

    @FXML
    public void initialize() {

        recipeService = new RecipeService();

        recipeList.setItems(filteredRecipes);

        recipes.addAll(recipeService.loadRecipes());

        filteredRecipes.addAll(recipes);
		
		sortComboBox.getItems().addAll(
        "По названию А-Я",
        "По названию Я-А"
);

sortComboBox.setValue(
        "По названию А-Я");
		sortRecipes();

sortComboBox.valueProperty()
        .addListener((obs, oldValue, newValue) -> {

            sortRecipes();
        });


        recipeList.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, oldValue, newValue) -> {

                    if (newValue == null) {
                        return;
                    }

                    if (newValue == selectedRecipe) {
                        return;
                    }

                    if (!confirmUnsavedChanges()) {

                        loadingRecipe = true;
                        recipeList.getSelectionModel().select(oldValue);
                        loadingRecipe = false;

                        return;
                    }

                    loadingRecipe = true;

                    selectedRecipe = newValue;

                    recipeTitleField.setText(
                            newValue.getTitle());

                    descriptionArea.setText(
                            newValue.getDescription());

                    hasUnsavedChanges = false;

                    loadingRecipe = false;

                    updateModeLabel();
                });

        searchField.textProperty()
                .addListener((obs, oldValue, newValue) -> {

                    filteredRecipes.clear();

                    String searchText =
                            newValue == null
                                    ? ""
                                    : newValue.toLowerCase().trim();

                    if (searchText.isEmpty()) {

                        filteredRecipes.addAll(recipes);

                    } else {

                        recipes.stream()
                                .filter(recipe -> {

                                    String title =
                                            recipe.getTitle() == null
                                                    ? ""
                                                    : recipe.getTitle()
                                                            .toLowerCase();

                                    String description =
                                            recipe.getDescription() == null
                                                    ? ""
                                                    : recipe.getDescription()
                                                            .toLowerCase();

                                    return title.contains(searchText)
                                            || description.contains(searchText);
                                })
                                .forEach(filteredRecipes::add);
								sortRecipes();
                    }
                });

        recipeTitleField.textProperty()
                .addListener((obs, oldValue, newValue) -> {

                    if (loadingRecipe) {
                        return;
                    }

                    hasUnsavedChanges = true;

                    updateModeLabel();
                });

        descriptionArea.textProperty()
                .addListener((obs, oldValue, newValue) -> {

                    if (loadingRecipe) {
                        return;
                    }

                    hasUnsavedChanges = true;

                    updateModeLabel();
                });

        hasUnsavedChanges = false;

        updateModeLabel();
		sortRecipes();
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

            if (searchField.getText() == null
                    || searchField.getText().isBlank()) {

                filteredRecipes.add(recipe);
            }

            hasUnsavedChanges = false;

        } else {

            selectedRecipe.setTitle(title.trim());
            selectedRecipe.setDescription(description);

            recipeService.updateRecipe(selectedRecipe);

            recipeList.refresh();

            hasUnsavedChanges = false;
        }

        loadingRecipe = true;

        recipeTitleField.clear();
        descriptionArea.clear();

        loadingRecipe = false;

        hasUnsavedChanges = false;
        selectedRecipe = null;

        updateModeLabel();

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

        ButtonType deleteButtonType =
                new ButtonType(
                        "Удалить",
                        ButtonBar.ButtonData.OK_DONE);

        ButtonType cancelButtonType =
                new ButtonType(
                        "Отмена",
                        ButtonBar.ButtonData.CANCEL_CLOSE);

        Alert confirmAlert =
                new Alert(Alert.AlertType.CONFIRMATION);

        confirmAlert.setTitle("Удаление рецепта");
        confirmAlert.setHeaderText(null);

        confirmAlert.setContentText(
                "Вы уверены, что хотите удалить рецепт:\n\n"
                        + recipeToDelete.getTitle() + "?");

        confirmAlert.getButtonTypes().setAll(
                deleteButtonType,
                cancelButtonType);

        ButtonType result =
                confirmAlert.showAndWait()
                        .orElse(cancelButtonType);

        if (result != deleteButtonType) {
            return;
        }

        recipeService.deleteRecipe(recipeToDelete);

        recipes.remove(recipeToDelete);
        filteredRecipes.remove(recipeToDelete);

        loadingRecipe = true;

        recipeTitleField.clear();
        descriptionArea.clear();

        loadingRecipe = false;

        hasUnsavedChanges = false;
        selectedRecipe = null;

        recipeList.getSelectionModel().clearSelection();

        updateModeLabel();
    }
	
	private void sortRecipes() {

    if (sortComboBox.getValue() == null) {
        return;
    }

    if (sortComboBox.getValue()
            .equals("По названию А-Я")) {

        filteredRecipes.sort(
                Comparator.comparing(
                        Recipe::getTitle,
                        String.CASE_INSENSITIVE_ORDER));

    } else {

        filteredRecipes.sort(
                Comparator.comparing(
                        Recipe::getTitle,
                        String.CASE_INSENSITIVE_ORDER)
                        .reversed());
    }
}

    private boolean confirmUnsavedChanges() {

        if (!hasUnsavedChanges) {
            return true;
        }

        ButtonType saveButton =
                new ButtonType(
                        "Сохранить",
                        ButtonBar.ButtonData.YES);

        ButtonType discardButton =
                new ButtonType(
                        "Не сохранять",
                        ButtonBar.ButtonData.NO);

        ButtonType cancelButton =
                new ButtonType(
                        "Отмена",
                        ButtonBar.ButtonData.CANCEL_CLOSE);

        Alert alert =
                new Alert(Alert.AlertType.CONFIRMATION);

        alert.setTitle("Несохранённые изменения");
        alert.setHeaderText("Рецепт был изменён");

        alert.setContentText(
                "Сохранить изменения перед переключением?");

        alert.getButtonTypes().setAll(
                saveButton,
                discardButton,
                cancelButton);

        ButtonType result =
                alert.showAndWait()
                        .orElse(cancelButton);

        if (result == saveButton) {

            saveRecipe(null);

            return true;
        }

        if (result == discardButton) {

            hasUnsavedChanges = false;

            return true;
        }

        return false;
    }

    private void updateModeLabel() {

        String text;

        if (selectedRecipe == null) {
            text = "Новый рецепт";
        } else {
            text = "Редактирование рецепта";
        }

        if (hasUnsavedChanges) {
            text += " *";
        }

        modeLabel.setText(text);
    }
}