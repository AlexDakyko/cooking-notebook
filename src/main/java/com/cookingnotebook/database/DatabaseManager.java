package com.cookingnotebook.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    private static final String DATABASE_URL =
            "jdbc:sqlite:cooking-notebook.db";

    public Connection getConnection() throws SQLException {

        return DriverManager.getConnection(
                DATABASE_URL);
    }

    public void initializeDatabase() {

        String sql = """
                CREATE TABLE IF NOT EXISTS recipes (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    title TEXT NOT NULL,
                    description TEXT
                )
                """;

        try (
                Connection connection = getConnection();
                Statement statement =
                        connection.createStatement()
        ) {

            statement.execute(sql);

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Failed to initialize database",
                    e);
        }
    }
}