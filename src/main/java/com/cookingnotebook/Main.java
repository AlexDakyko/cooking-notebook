package com.cookingnotebook;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;
import com.cookingnotebook.database.DatabaseManager;

import com.cookingnotebook.model.Recipe;
import com.cookingnotebook.repository.SQLiteRecipeRepository;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        DatabaseManager databaseManager =
        new DatabaseManager();

databaseManager.initializeDatabase();


		Parent root = FXMLLoader.load(
                getClass().getResource("/recipe-list.fxml"));

        Scene scene = new Scene(root, 800, 600);

        primaryStage.setTitle("Cooking Notebook");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}