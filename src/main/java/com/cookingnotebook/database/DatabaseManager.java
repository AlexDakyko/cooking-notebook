package com.cookingnotebook.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DatabaseManager {

    private final String databaseUrl;

    public DatabaseManager() {
        this(createApplicationDatabaseUrl());
    }

    public DatabaseManager(String databaseUrl) {
        this.databaseUrl = databaseUrl;
    }

    private static String createApplicationDatabaseUrl() {
        Path directory = Paths.get(
                System.getProperty("user.home"),
                ".cooking-notebook");

        try {
            Files.createDirectories(directory);
        } catch (Exception exception) {
            throw new IllegalStateException(
                    "Unable to create application data directory: " + directory,
                    exception);
        }

        Path database = directory.resolve("cooking-notebook.db");
        Path legacyDatabase =
                Paths.get("cooking-notebook.db").toAbsolutePath();

        if (!Files.exists(database) && Files.exists(legacyDatabase)) {
            try {
                Files.copy(legacyDatabase, database);
            } catch (Exception exception) {
                throw new IllegalStateException(
                        "Unable to migrate existing database to: " + database,
                        exception);
            }
        }

        return "jdbc:sqlite:" + database.toAbsolutePath();
    }

    public Connection getConnection() throws SQLException {
        Connection connection =
                DriverManager.getConnection(databaseUrl);
        try (Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON");
        }
        return connection;
    }

    public String getDatabaseUrl() {
        return databaseUrl;
    }

    public void initializeDatabase() throws SQLException {
        new DatabaseInitializer(this).initialize();
    }
}