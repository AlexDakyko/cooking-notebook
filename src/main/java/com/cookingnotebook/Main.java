package com.cookingnotebook;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.control.Alert;
import com.cookingnotebook.database.DatabaseManager;
import com.cookingnotebook.controller.RecipeController;
import com.cookingnotebook.repository.SQLiteRecipeRepository;
import com.cookingnotebook.repository.SQLiteCategoryRepository;
import com.cookingnotebook.repository.SQLiteIngredientRepository;
import com.cookingnotebook.repository.SQLitePhotoRepository;
import com.cookingnotebook.service.RecipeService;
import java.sql.SQLException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        DatabaseManager databaseManager = new DatabaseManager();

        try {
            databaseManager.initializeDatabase();
        } catch (SQLException exception) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка запуска");
            alert.setHeaderText("Не удалось открыть базу данных");
            alert.setContentText(
                    "Приложение не может продолжить работу. "
                            + "Подробности: " + exception.getMessage());
            alert.showAndWait();
            primaryStage.close();
            return;
        }

        RecipeService recipeService =
                new RecipeService(
                        new SQLiteRecipeRepository(databaseManager));
        SQLiteCategoryRepository categoryRepository =
                new SQLiteCategoryRepository(databaseManager);
        SQLiteIngredientRepository ingredientRepository =
                new SQLiteIngredientRepository(databaseManager);
        SQLitePhotoRepository photoRepository =
                new SQLitePhotoRepository(databaseManager);

        FXMLLoader loader =
                new FXMLLoader(
                        getClass().getResource("/recipe-list.fxml"));
        loader.setControllerFactory(type -> {
            if (type == RecipeController.class) {
                return new RecipeController(
                        recipeService,
                        categoryRepository,
                        ingredientRepository,
                        photoRepository);
            }
            try {
                return type.getDeclaredConstructor().newInstance();
            } catch (ReflectiveOperationException exception) {
                throw new RuntimeException(exception);
            }
        });

        Parent root = loader.load();

        Scene scene = new Scene(root, 800, 600);

        primaryStage.setTitle("Cooking Notebook");
        primaryStage.setScene(scene);
        RecipeController controller =
                (RecipeController) loader.getController();
        primaryStage.setOnCloseRequest(
                event -> handleWindowClose(controller, event));
        primaryStage.show();
    }

    private void handleWindowClose(
            RecipeController controller,
            WindowEvent event) {
        if (!controller.confirmWindowClose()) {
            event.consume();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}