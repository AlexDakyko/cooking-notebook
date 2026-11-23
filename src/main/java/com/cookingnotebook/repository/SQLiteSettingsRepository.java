package com.cookingnotebook.repository;

import com.cookingnotebook.database.DatabaseManager;
import com.cookingnotebook.exception.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class SQLiteSettingsRepository implements SettingsRepository {
    private final DatabaseManager databaseManager;

    public SQLiteSettingsRepository(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    @Override
    public Optional<String> get(String key) {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT value FROM settings WHERE key = ?")) {
            statement.setString(1, key);
            try (ResultSet result = statement.executeQuery()) {
                return result.next()
                        ? Optional.ofNullable(result.getString("value"))
                        : Optional.empty();
            }
        } catch (SQLException exception) {
            throw new DatabaseException("Failed to load setting", exception);
        }
    }

    @Override
    public void set(String key, String value) {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                     INSERT INTO settings(key, value) VALUES(?, ?)
                     ON CONFLICT(key) DO UPDATE SET value = excluded.value
                     """)) {
            statement.setString(1, key);
            statement.setString(2, value);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new DatabaseException("Failed to save setting", exception);
        }
    }

    @Override
    public Map<String, String> getAll() {
        Map<String, String> settings = new LinkedHashMap<>();
        try (Connection connection = databaseManager.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rows = statement.executeQuery(
                     "SELECT key, value FROM settings")) {
            while (rows.next()) {
                settings.put(
                        rows.getString("key"),
                        rows.getString("value"));
            }
        } catch (SQLException exception) {
            throw new DatabaseException("Failed to load settings", exception);
        }
        return settings;
    }
}
