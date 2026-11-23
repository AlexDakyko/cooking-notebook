package com.cookingnotebook.controller;

import com.cookingnotebook.model.Recipe;
import com.cookingnotebook.service.RecipeService;
import com.cookingnotebook.exception.DatabaseException;
import com.cookingnotebook.exception.RecipeNotFoundException;
import com.cookingnotebook.exception.ValidationException;
import com.cookingnotebook.model.Category;
import com.cookingnotebook.repository.CategoryRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;

public class RecipeController {

    private Recipe selectedRecipe;
    private boolean hasUnsavedChanges;
    private boolean loadingRecipe;

    private final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    @FXML
    private Label modeLabel;

    @FXML
    private Label createdAtLabel;

    @FXML
    private Label updatedAtLabel;

    @FXML
    private TextField recipeTitleField;

    @FXML
    private TextArea descriptionArea;

    @FXML
    private TextArea instructionsArea;

    @FXML
    private TextField preparationTimeField;

    @FXML
    private TextField servingsField;

    @FXML
    private ComboBox<Category> categoryComboBox;

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
    private CategoryRepository categoryRepository;

    public RecipeController() {
        this(new RecipeService());
    }

    public RecipeController(RecipeService recipeService) {
        this(recipeService, null);
    }

    public RecipeController(
            RecipeService recipeService,
            CategoryRepository categoryRepository) {
        this.recipeService = recipeService;
        this.categoryRepository = categoryRepository;
    }

    private final ObservableList<Recipe> recipes =
            FXCollections.observableArrayList();

    private final ObservableList<Recipe> filteredRecipes =
            FXCollections.observableArrayList();

    @FXML
    public void initialize() {

        recipeList.setItems(filteredRecipes);

        recipes.addAll(recipeService.loadRecipes());
        filteredRecipes.addAll(recipes);

        if (categoryRepository != null) {
            categoryComboBox.getItems().clear();
            categoryComboBox.getItems()
                    .addAll(categoryRepository.getAllCategories());
        }

        sortComboBox.getItems().addAll(
                "По названию А-Я",
                "По названию Я-А"
        );

        sortComboBox.setValue("По названию А-Я");

        sortComboBox.valueProperty()
                .addListener((obs, oldValue, newValue) ->
                        sortRecipes());

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
                        recipeList.getSelectionModel()
                                .select(oldValue);
                        loadingRecipe = false;
                        return;
                    }

                    loadRecipeToForm(newValue);
                });

        searchField.textProperty()
                .addListener((obs, oldValue, newValue) ->
                        filterRecipes());

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

        instructionsArea.textProperty()
                .addListener((obs, oldValue, newValue) ->
                        markUnsavedChange());
        preparationTimeField.textProperty()
                .addListener((obs, oldValue, newValue) ->
                        markUnsavedChange());
        servingsField.textProperty()
                .addListener((obs, oldValue, newValue) ->
                        markUnsavedChange());
        categoryComboBox.valueProperty()
                .addListener((obs, oldValue, newValue) ->
                        markUnsavedChange());

        hasUnsavedChanges = false;

        updateModeLabel();
        sortRecipes();
        clearDateLabels();
    }

    public boolean validateRecipeTitle(String title) {
        return title != null && !title.isBlank();
    }

    private int parseOptionalInteger(String value)
            throws NumberFormatException {
        if (value == null || value.trim().isEmpty()) {
            return 0;
        }
        return Integer.parseInt(value.trim());
    }

    @FXML
    public void saveRecipe(ActionEvent event) {

        if (!saveCurrentChanges()) {
            return;
        }

        resetEditor();
    }

    private boolean saveCurrentChanges() {
        String title = recipeTitleField.getText();

        if (!validateRecipeTitle(title)) {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Validation Error");
            alert.setHeaderText("Invalid Recipe Title");
            alert.setContentText(
                    "Recipe title cannot be empty or contain only whitespace.");

            alert.showAndWait();
            return false;
        }

        String description = descriptionArea.getText();
        String instructions = instructionsArea.getText();

        int preparationTime;
        int servings;
        try {
            preparationTime = parseOptionalInteger(
                    preparationTimeField.getText());
            servings = parseOptionalInteger(
                    servingsField.getText());
        } catch (NumberFormatException exception) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка ввода");
            alert.setHeaderText("Некорректные числовые поля");
            alert.setContentText(
                    "Время подготовки и количество порций должны быть целыми числами.");
            alert.showAndWait();
            return false;
        }

        Category category = categoryComboBox.getValue();
        int categoryId = category == null ? 0 : category.getId();

        try {
            if (selectedRecipe == null) {
                Recipe recipe = new Recipe(
                        title.trim(),
                        description,
                        instructions,
                        preparationTime,
                        servings,
                        categoryId,
                        0);

                recipeService.saveRecipe(recipe);
                recipes.add(recipe);

                if (searchField.getText() == null
                        || searchField.getText().isBlank()) {
                    filteredRecipes.add(recipe);
                }
                sortRecipes();
            } else {
                selectedRecipe.setTitle(title.trim());
                selectedRecipe.setDescription(description);
                selectedRecipe.setInstructions(instructions);
                selectedRecipe.setPreparationTime(preparationTime);
                selectedRecipe.setServings(servings);
                selectedRecipe.setCategoryId(categoryId);
                recipeService.updateRecipe(selectedRecipe);
                recipeList.refresh();
            }

            hasUnsavedChanges = false;
            updateModeLabel();
            return true;
        } catch (DatabaseException
                | RecipeNotFoundException
                | ValidationException exception) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка сохранения");
            alert.setHeaderText("Изменения не сохранены");
            alert.setContentText(exception.getMessage());
            alert.showAndWait();
            return false;
        }
    }

    @FXML
    public void deleteRecipe(ActionEvent event) {

        Recipe recipeToDelete =
                recipeList.getSelectionModel().getSelectedItem();

        if (recipeToDelete == null) {

            Alert alert =
                    new Alert(Alert.AlertType.WARNING);

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

        try {
            recipeService.deleteRecipe(recipeToDelete);
        } catch (DatabaseException | RecipeNotFoundException exception) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка удаления");
            alert.setHeaderText("Рецепт не удалён");
            alert.setContentText(exception.getMessage());
            alert.showAndWait();
            return;
        }

        recipes.remove(recipeToDelete);
        filteredRecipes.remove(recipeToDelete);

        resetEditor();
    }

    private void clearForm() {

        loadingRecipe = true;

        recipeTitleField.clear();
        descriptionArea.clear();
        instructionsArea.clear();
        preparationTimeField.clear();
        servingsField.clear();
        categoryComboBox.setValue(null);

        loadingRecipe = false;
    }

    private void markUnsavedChange() {
        if (loadingRecipe) {
            return;
        }
        hasUnsavedChanges = true;
        updateModeLabel();
    }

    private void loadRecipeToForm(Recipe recipe) {

        loadingRecipe = true;

        selectedRecipe = recipe;

        recipeTitleField.setText(recipe.getTitle());
        descriptionArea.setText(recipe.getDescription());
        instructionsArea.setText(
                recipe.getInstructions() == null
                        ? ""
                        : recipe.getInstructions());
        preparationTimeField.setText(
                recipe.getPreparationTime() > 0
                        ? String.valueOf(recipe.getPreparationTime())
                        : "");
        servingsField.setText(
                recipe.getServings() > 0
                        ? String.valueOf(recipe.getServings())
                        : "");

        categoryComboBox.setValue(
                categoryComboBox.getItems().stream()
                        .filter(category ->
                                category.getId() == recipe.getCategoryId())
                        .findFirst()
                        .orElse(null));

        updateDateLabels(recipe);

        hasUnsavedChanges = false;
        loadingRecipe = false;

        updateModeLabel();
    }

    private void resetEditor() {

        clearForm();

        selectedRecipe = null;
        hasUnsavedChanges = false;

        clearDateLabels();

        recipeList.getSelectionModel()
                .clearSelection();

        updateModeLabel();
    }

    private void filterRecipes() {

        filteredRecipes.clear();

        String searchText =
                searchField.getText() == null
                        ? ""
                        : searchField.getText()
                        .toLowerCase()
                        .trim();

        if (searchText.isEmpty()) {

            filteredRecipes.addAll(recipes);

        } else {

            recipes.stream()
                    .filter(recipe -> {

                        String title =
                                recipe.getTitle() == null
                                        ? ""
                                        : recipe.getTitle().toLowerCase();

                        String description =
                                recipe.getDescription() == null
                                        ? ""
                                        : recipe.getDescription().toLowerCase();

                        return title.contains(searchText)
                                || description.contains(searchText);
                    })
                    .forEach(filteredRecipes::add);
        }

        sortRecipes();
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

    private void updateDateLabels(Recipe recipe) {

        if (recipe.getCreatedAt() != null) {

            createdAtLabel.setText(
                    "Создан: "
                            + recipe.getCreatedAt()
                            .format(formatter));

        } else {

            createdAtLabel.setText("Создан: -");
        }

        if (recipe.getUpdatedAt() != null) {

            updatedAtLabel.setText(
                    "Изменён: "
                            + recipe.getUpdatedAt()
                            .format(formatter));

        } else {

            updatedAtLabel.setText("Изменён: -");
        }
    }

    private void clearDateLabels() {

        createdAtLabel.setText("Создан: -");
        updatedAtLabel.setText("Изменён: -");
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

            return saveCurrentChanges();
}

        if (result == discardButton) {
            hasUnsavedChanges = false;
            return true;
        }

        return false;
    }

    public boolean confirmWindowClose() {
        if (!hasUnsavedChanges) {
            return true;
        }

        if (confirmUnsavedChanges()) {
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