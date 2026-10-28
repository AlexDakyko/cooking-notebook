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

        String createTableSql = """
                CREATE TABLE IF NOT EXISTS recipes (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    title TEXT NOT NULL,
                    description TEXT,
                    created_at TEXT,
                    updated_at TEXT
                )
                """;

        try (
                Connection connection = getConnection();
                Statement statement =
                        connection.createStatement()
        ) {

            statement.execute(createTableSql);

            statement.execute(
                    """
                    ALTER TABLE recipes
                    ADD COLUMN created_at TEXT
                    """
            );

        } catch (SQLException ignored) {
        }

        try (
                Connection connection = getConnection();
                Statement statement =
                        connection.createStatement()
        ) {

            statement.execute(
                    """
                    ALTER TABLE recipes
                    ADD COLUMN updated_at TEXT
                    """
            );

        } catch (SQLException ignored) {
        }
    }
}