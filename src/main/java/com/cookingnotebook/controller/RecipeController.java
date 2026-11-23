package com.cookingnotebook.controller;

import com.cookingnotebook.model.Recipe;
import com.cookingnotebook.service.RecipeService;
import com.cookingnotebook.exception.DatabaseException;
import com.cookingnotebook.exception.RecipeNotFoundException;
import com.cookingnotebook.exception.ValidationException;
import com.cookingnotebook.model.Category;
import com.cookingnotebook.model.Ingredient;
import com.cookingnotebook.model.Photo;
import com.cookingnotebook.repository.CategoryRepository;
import com.cookingnotebook.repository.IngredientRepository;
import com.cookingnotebook.repository.PhotoRepository;
import com.cookingnotebook.service.IngredientService;
import com.cookingnotebook.service.PhotoService;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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
    private Button newRecipeButton;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> sortComboBox;

    @FXML
    private ComboBox<Category> categoryFilterComboBox;

    @FXML
    private ListView<Ingredient> ingredientList;

    @FXML
    private ListView<Photo> photoList;

    @FXML
    private ImageView recipePhotoView;

    @FXML
    private TextField ingredientNameField;

    @FXML
    private TextField ingredientQuantityField;

    @FXML
    private TextField ingredientUnitField;

    @FXML
    private ListView<Recipe> recipeList;

    private RecipeService recipeService;
    private CategoryRepository categoryRepository;
    private IngredientService ingredientService;
    private PhotoService photoService;
    private final ObservableList<Ingredient> ingredients =
            FXCollections.observableArrayList();
    private final ObservableList<Photo> photos =
            FXCollections.observableArrayList();
    private final java.util.List<Ingredient> draftIngredients =
            new java.util.ArrayList<>();
    private final java.util.List<Photo> draftPhotos =
            new java.util.ArrayList<>();

    public RecipeController() {
        this(new RecipeService());
    }

    public RecipeController(RecipeService recipeService) {
        this(recipeService, null);
    }

    public RecipeController(
            RecipeService recipeService,
            CategoryRepository categoryRepository) {
        this(recipeService, categoryRepository, null, null);
    }

    public RecipeController(
            RecipeService recipeService,
            CategoryRepository categoryRepository,
            IngredientRepository ingredientRepository) {
        this(recipeService, categoryRepository,
                ingredientRepository, null);
    }

    public RecipeController(
            RecipeService recipeService,
            CategoryRepository categoryRepository,
            IngredientRepository ingredientRepository,
            PhotoRepository photoRepository) {
        this.recipeService = recipeService;
        this.categoryRepository = categoryRepository;
        this.ingredientService =
                ingredientRepository == null
                        ? null
                        : new IngredientService(ingredientRepository);
        this.photoService =
                photoRepository == null
                        ? null
                        : new PhotoService(photoRepository);
    }

    private final ObservableList<Recipe> recipes =
            FXCollections.observableArrayList();

    private final ObservableList<Recipe> filteredRecipes =
            FXCollections.observableArrayList();

    @FXML
    public void initialize() {

        recipeList.setItems(filteredRecipes);
        ingredientList.setItems(ingredients);
        photoList.setItems(photos);
        photoList.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, oldValue, photo) ->
                        displayPhoto(photo));

        recipes.addAll(recipeService.loadRecipes());
        filteredRecipes.addAll(recipes);

        if (categoryRepository != null) {
            categoryComboBox.getItems().clear();
            categoryComboBox.getItems()
                    .addAll(categoryRepository.getAllCategories());
            categoryFilterComboBox.getItems().clear();
            categoryFilterComboBox.getItems()
                    .addAll(categoryRepository.getAllCategories());
            categoryFilterComboBox.setValue(null);
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

        categoryFilterComboBox.valueProperty()
                .addListener((obs, oldValue, newValue) ->
                        filterRecipes());

        ingredientList.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, oldValue, ingredient) -> {
                    if (ingredient == null) {
                        clearIngredientFields();
                        return;
                    }
                    ingredientNameField.setText(ingredient.getName());
                    ingredientQuantityField.setText(
                            ingredient.getQuantity() == null
                                    ? ""
                                    : ingredient.getQuantity().toString());
                    ingredientUnitField.setText(
                            ingredient.getUnit() == null
                                    ? ""
                                    : ingredient.getUnit());
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
    }

    @FXML
    public void newRecipe(ActionEvent event) {
        if (!confirmUnsavedChanges()) {
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
                filterRecipes();
                selectedRecipe = recipe;
                persistDraftDetails();
                loadRecipeToForm(recipe);
            } else {
                selectedRecipe.setTitle(title.trim());
                selectedRecipe.setDescription(description);
                selectedRecipe.setInstructions(instructions);
                selectedRecipe.setPreparationTime(preparationTime);
                selectedRecipe.setServings(servings);
                selectedRecipe.setCategoryId(categoryId);
                recipeService.updateRecipe(selectedRecipe);
                filterRecipes();
                updateDateLabels(selectedRecipe);
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

    @FXML
    public void addIngredient(ActionEvent event) {
        if (ingredientService == null) {
            showWarning(
                    "Ингредиенты",
                    "Хранилище ингредиентов недоступно");
            return;
        }

        try {
            Ingredient ingredient = readIngredientFromFields();
            if (selectedRecipe == null) {
                if (ingredient.getName().isBlank()) {
                    throw new ValidationException(
                            "Название ингредиента обязательно");
                }
                if (ingredient.getQuantity() != null
                        && ingredient.getQuantity() < 0) {
                    throw new ValidationException(
                            "Количество ингредиента не может быть отрицательным");
                }
                ingredient.setRecipeId(0);
                draftIngredients.add(ingredient);
                ingredients.add(ingredient);
                clearIngredientFields();
                return;
            }
            ingredient.setRecipeId(selectedRecipe.getId());
            ingredientService.saveIngredient(ingredient);
            ingredients.add(ingredient);
            loadIngredients(selectedRecipe);
        } catch (ValidationException
                | DatabaseException
                | NumberFormatException exception) {
            showError(
                    "Ошибка ингредиента",
                    exception.getMessage());
        }
    }

    @FXML
    public void updateIngredient(ActionEvent event) {
        Ingredient selected =
                ingredientList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning(
                    "Ингредиенты",
                    "Выберите ингредиент");
            return;
        }
        if (ingredientService == null) {
            showWarning(
                    "Ингредиенты",
                    "Хранилище ингредиентов недоступно");
            return;
        }

        try {
            Ingredient updated = readIngredientFromFields();
            if (selected.getId() == 0) {
                updated.setId(0);
                updated.setRecipeId(0);
                draftIngredients.remove(selected);
                draftIngredients.add(updated);
                int index = ingredients.indexOf(selected);
                if (index >= 0) {
                    ingredients.set(index, updated);
                }
                return;
            }
            updated.setId(selected.getId());
            updated.setRecipeId(selected.getRecipeId());
            ingredientService.updateIngredient(updated);
            loadIngredients(selectedRecipe);
        } catch (ValidationException
                | DatabaseException
                | NumberFormatException exception) {
            showError(
                    "Ошибка ингредиента",
                    exception.getMessage());
        }
    }

    @FXML
    public void deleteIngredient(ActionEvent event) {
        Ingredient selected =
                ingredientList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning(
                    "Ингредиенты",
                    "Выберите ингредиент");
            return;
        }
        if (ingredientService == null) {
            showWarning(
                    "Ингредиенты",
                    "Хранилище ингредиентов недоступно");
            return;
        }

        try {
            if (selected.getId() == 0) {
                ingredients.remove(selected);
                draftIngredients.remove(selected);
                clearIngredientFields();
                return;
            }
            ingredientService.deleteIngredient(selected);
            ingredients.remove(selected);
            clearIngredientFields();
        } catch (DatabaseException
                | RecipeNotFoundException exception) {
            showError(
                    "Ошибка удаления",
                    exception.getMessage());
        }
    }

    @FXML
    public void importPhoto(ActionEvent event) {
        if (photoService == null) {
            showWarning(
                    "Фото",
                    "Хранилище фото недоступно");
            return;
        }

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Выбрать фото");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                        "Изображения",
                        "*.jpg",
                        "*.jpeg",
                        "*.png",
                        "*.gif"));
        Window owner = recipeList.getScene() == null
                ? null
                : recipeList.getScene().getWindow();
        java.io.File file = chooser.showOpenDialog(owner);
        if (file == null) {
            return;
        }

        try {
            byte[] imageData = Files.readAllBytes(file.toPath());
            Photo photo = new Photo();
            photo.setRecipeId(
                    selectedRecipe == null
                            ? 0
                            : selectedRecipe.getId());
            photo.setFileName(file.getName());
            photo.setMimeType(detectMimeType(file));
            photo.setImageData(imageData);
            if (selectedRecipe == null) {
                if (!photoService.isSupportedMimeType(photo.getMimeType())) {
                    throw new ValidationException(
                            "Поддерживаются только JPG, PNG и GIF");
                }
                if (imageData.length > 10 * 1024 * 1024) {
                    throw new ValidationException(
                            "Размер фото не должен превышать 10 МБ");
                }
                draftPhotos.add(photo);
                photos.add(photo);
            } else {
                photoService.savePhoto(photo);
                loadPhotos(selectedRecipe);
            }
            photoList.getSelectionModel()
                    .select(
                            photos.stream()
                                    .filter(candidate ->
                                            candidate.getId() == photo.getId())
                                    .findFirst()
                                    .orElse(null));
            showInfo(
                    "Фото",
                    "Фото импортировано");
        } catch (IOException
                | ValidationException
                | DatabaseException exception) {
            showError(
                    "Ошибка импорта фото",
                    exception.getMessage());
        }
    }

    @FXML
    public void deletePhoto(ActionEvent event) {
        Photo selected =
                photoList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning(
                    "Фото",
                    "Выберите фото");
            return;
        }
        if (photoService == null) {
            showWarning(
                    "Фото",
                    "Хранилище фото недоступно");
            return;
        }

        try {
            if (selected == null) {
                return;
            }
            if (selected.getId() == 0) {
                photos.remove(selected);
                draftPhotos.remove(selected);
                recipePhotoView.setImage(null);
                return;
            }
            photoService.deletePhoto(selected);
            photos.remove(selected);
            recipePhotoView.setImage(null);
        } catch (DatabaseException
                | RecipeNotFoundException exception) {
            showError(
                    "Ошибка удаления",
                    exception.getMessage());
        }
    }

    @FXML
    public void exportPhoto(ActionEvent event) {
        Photo selected =
                photoList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning(
                    "Фото",
                    "Выберите фото");
            return;
        }

        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Выбрать папку для экспорта");
        Window owner = recipeList.getScene() == null
                ? null
                : recipeList.getScene().getWindow();
        java.io.File directory = chooser.showDialog(owner);
        if (directory == null) {
            return;
        }

        Path target = directory.toPath()
                .resolve(selected.getFileName());
        try {
            Files.write(target, selected.getImageData());
            showInfo(
                    "Экспорт фото",
                    "Фото сохранено: " + target);
        } catch (IOException exception) {
            showError(
                    "Ошибка экспорта фото",
                    exception.getMessage());
        }
    }

    private String detectMimeType(java.io.File file) {
        String name =
                file.getName().toLowerCase(java.util.Locale.ROOT);
        if (name.endsWith(".png")) {
            return "image/png";
        }
        if (name.endsWith(".gif")) {
            return "image/gif";
        }
        return "image/jpeg";
    }

    private void displayPhoto(Photo photo) {
        if (photo == null
                || photo.getImageData() == null) {
            recipePhotoView.setImage(null);
            return;
        }
        try {
            recipePhotoView.setImage(
                    new Image(
                            new ByteArrayInputStream(
                                    photo.getImageData())));
        } catch (RuntimeException exception) {
            recipePhotoView.setImage(null);
            showError(
                    "Ошибка отображения фото",
                    "Файл не является корректным изображением");
        }
    }

    private void loadPhotos(Recipe recipe) {
        if (photoService == null || recipe == null) {
            photos.clear();
            recipePhotoView.setImage(null);
            return;
        }
        try {
            photos.setAll(
                    photoService.getByRecipeId(recipe.getId()));
        } catch (DatabaseException exception) {
            showError(
                    "Ошибка загрузки фото",
                    exception.getMessage());
        }
    }

    private Ingredient readIngredientFromFields()
            throws NumberFormatException {
        Ingredient ingredient = new Ingredient();
        ingredient.setName(
                ingredientNameField.getText() == null
                        ? ""
                        : ingredientNameField.getText().trim());
        String quantityText =
                ingredientQuantityField.getText();
        if (quantityText != null
                && !quantityText.trim().isEmpty()) {
            ingredient.setQuantity(
                    Double.parseDouble(quantityText.trim()));
        }
        ingredient.setUnit(
                ingredientUnitField.getText() == null
                        ? ""
                        : ingredientUnitField.getText().trim());
        return ingredient;
    }

    private void loadIngredients(Recipe recipe) {
        if (ingredientService == null || recipe == null) {
            clearIngredients();
            return;
        }
        try {
            ingredients.setAll(
                    ingredientService.getByRecipeId(recipe.getId()));
        } catch (DatabaseException exception) {
            showError(
                    "Ошибка загрузки",
                    exception.getMessage());
        }
    }

    private void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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
        clearIngredients();
        photos.clear();
        recipePhotoView.setImage(null);
        draftIngredients.clear();
        draftPhotos.clear();
    }

    private void clearIngredientFields() {
        ingredientNameField.clear();
        ingredientQuantityField.clear();
        ingredientUnitField.clear();
    }

    private void clearIngredients() {
        ingredientList.getSelectionModel().clearSelection();
        ingredients.clear();
        clearIngredientFields();
    }

    private void persistDraftDetails() {
        if (ingredientService != null) {
            for (Ingredient ingredient : draftIngredients) {
                ingredient.setRecipeId(selectedRecipe.getId());
                ingredientService.saveIngredient(ingredient);
            }
            draftIngredients.clear();
            loadIngredients(selectedRecipe);
        }

        if (photoService != null) {
            for (Photo photo : draftPhotos) {
                photo.setRecipeId(selectedRecipe.getId());
                photoService.savePhoto(photo);
            }
            draftPhotos.clear();
            loadPhotos(selectedRecipe);
        }
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
        draftIngredients.clear();
        draftPhotos.clear();

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

        loadIngredients(recipe);
        loadPhotos(recipe);

        updateDateLabels(recipe);

        hasUnsavedChanges = false;
        loadingRecipe = false;

        updateModeLabel();
    }

    public void resetEditor() {
        loadingRecipe = true;
        recipeList.getSelectionModel().clearSelection();
        selectedRecipe = null;
        clearForm();
        clearDateLabels();
        hasUnsavedChanges = false;
        loadingRecipe = false;
        updateModeLabel();
    }

    public ListView<Recipe> getRecipeListForTesting() {
        return recipeList;
    }

    private void filterRecipes() {

        filteredRecipes.clear();

        String searchText =
                searchField.getText() == null
                        ? ""
                        : searchField.getText()
                        .toLowerCase()
                        .trim();
        Category selectedCategory =
                categoryFilterComboBox.getValue();

        recipes.stream()
                .filter(recipe -> {
                    if (selectedCategory != null
                            && recipe.getCategoryId()
                            != selectedCategory.getId()) {
                        return false;
                    }

                    if (searchText.isEmpty()) {
                        return true;
                    }

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