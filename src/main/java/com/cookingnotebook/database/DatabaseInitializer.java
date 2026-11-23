package com.cookingnotebook.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {
    private final DatabaseManager databaseManager;

    public DatabaseInitializer(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public void initialize() throws SQLException {
        try (Connection connection = databaseManager.getConnection()) {
            connection.setAutoCommit(false);
            try (Statement statement = connection.createStatement()) {
                statement.execute("""
                        CREATE TABLE IF NOT EXISTS schema_migrations (
                            version INTEGER PRIMARY KEY,
                            applied_at TEXT NOT NULL
                        )
                        """);

                boolean legacyRecipesExist =
                        tableExists(connection, "recipes");
                int version = getSchemaVersion(connection);

                if (version < 1) {
                    statement.execute("""
                            CREATE TABLE IF NOT EXISTS categories (
                                id INTEGER PRIMARY KEY AUTOINCREMENT,
                                name TEXT NOT NULL UNIQUE
                            )
                            """);
                    seedCategories(connection);
                    statement.execute("""
                            CREATE TABLE IF NOT EXISTS recipes (
                                id INTEGER PRIMARY KEY AUTOINCREMENT,
                                title TEXT NOT NULL,
                                description TEXT,
                                instructions TEXT,
                                preparation_time INTEGER,
                                servings INTEGER,
                                category_id INTEGER,
                                created_at TEXT NOT NULL,
                                updated_at TEXT NOT NULL,
                                FOREIGN KEY(category_id) REFERENCES categories(id)
                            )
                            """);
                    recordMigration(connection, 1);
                }

                if (legacyRecipesExist) {
                    ensureCompleteRecipeSchema(connection);
                }

                createSupportingTables(connection);
                connection.commit();
            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    private void createSupportingTables(Connection connection)
            throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS ingredients (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        recipe_id INTEGER NOT NULL,
                        name TEXT NOT NULL,
                        quantity REAL,
                        unit TEXT,
                        FOREIGN KEY(recipe_id) REFERENCES recipes(id) ON DELETE CASCADE
                    )
                    """);
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS photos (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        recipe_id INTEGER NOT NULL,
                        file_name TEXT,
                        mime_type TEXT,
                        image_data BLOB NOT NULL,
                        FOREIGN KEY(recipe_id) REFERENCES recipes(id) ON DELETE CASCADE
                    )
                    """);
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS settings (
                        key TEXT PRIMARY KEY,
                        value TEXT NOT NULL
                    )
                    """);
        }
    }

    private void ensureCompleteRecipeSchema(Connection connection)
            throws SQLException {
        if (!tableExists(connection, "recipes")) {
            return;
        }

        try (Statement statement = connection.createStatement()) {
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS categories (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        name TEXT NOT NULL UNIQUE
                    )
                    """);
        }
        seedCategories(connection);

        addColumnIfMissing(
                connection,
                "recipes",
                "instructions",
                "TEXT");
        addColumnIfMissing(
                connection,
                "recipes",
                "preparation_time",
                "INTEGER");
        addColumnIfMissing(
                connection,
                "recipes",
                "servings",
                "INTEGER");
        addColumnIfMissing(
                connection,
                "recipes",
                "category_id",
                "INTEGER");
        addColumnIfMissing(
                connection,
                "recipes",
                "created_at",
                "TEXT");
        addColumnIfMissing(
                connection,
                "recipes",
                "updated_at",
                "TEXT");

        migrateLegacyCategoryNames(connection);
        recordMigration(connection, 1);
    }

    private void addColumnIfMissing(
            Connection connection,
            String table,
            String column,
            String type) throws SQLException {
        try (ResultSet columns = connection.getMetaData()
                .getColumns(null, null, table, column)) {
            if (columns.next()) {
                return;
            }
        }
        try (Statement statement = connection.createStatement()) {
            statement.execute(
                    "ALTER TABLE " + table
                            + " ADD COLUMN " + column
                            + " " + type);
        }
    }

    private void migrateLegacyCategoryNames(Connection connection)
            throws SQLException {
        if (!columnExists(connection, "recipes", "category")) {
            return;
        }

        try (Statement select = connection.createStatement();
             ResultSet rows = select.executeQuery(
                     "SELECT id, category FROM recipes "
                             + "WHERE category IS NOT NULL")) {
            while (rows.next()) {
                Integer categoryId =
                        migrateCategory(connection, rows.getString("category"));
                if (categoryId == null) {
                    continue;
                }
                try (PreparedStatement update = connection.prepareStatement(
                        "UPDATE recipes SET category_id = ? WHERE id = ?")) {
                    update.setInt(1, categoryId);
                    update.setInt(2, rows.getInt("id"));
                    update.executeUpdate();
                }
            }
        }
    }

    private boolean columnExists(
            Connection connection,
            String table,
            String column) throws SQLException {
        try (ResultSet columns = connection.getMetaData()
                .getColumns(null, null, table, column)) {
            return columns.next();
        }
    }

    private void migrateLegacyRecipeTable(Connection connection)
            throws SQLException {
        try (Statement statement = connection.createStatement();
             ResultSet columns = connection.getMetaData()
                     .getColumns(null, null, "recipes", "instructions")) {
            if (columns.next()) {
                return;
            }
        }

        try (Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = OFF");
            statement.execute("ALTER TABLE recipes RENAME TO recipes_legacy");
            statement.execute("""
                    CREATE TABLE recipes (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        title TEXT NOT NULL,
                        description TEXT,
                        instructions TEXT,
                        preparation_time INTEGER,
                        servings INTEGER,
                        category_id INTEGER,
                        created_at TEXT NOT NULL,
                        updated_at TEXT NOT NULL,
                        FOREIGN KEY(category_id) REFERENCES categories(id)
                    )
                    """);

            try (Statement legacy = connection.createStatement();
                 ResultSet rows = legacy.executeQuery("""
                         SELECT id, title, description, created_at, updated_at,
                                category
                         FROM recipes_legacy
                         """)) {
                while (rows.next()) {
                    Integer categoryId = migrateCategory(
                            connection,
                            rows.getString("category"));
                    try (PreparedStatement insert = connection.prepareStatement("""
                            INSERT INTO recipes(
                                id, title, description, instructions,
                                preparation_time, servings, category_id,
                                created_at, updated_at
                            ) VALUES(?, ?, ?, NULL, NULL, NULL, ?, ?, ?)
                            """)) {
                        insert.setInt(1, rows.getInt("id"));
                        insert.setString(2, rows.getString("title"));
                        insert.setString(3, rows.getString("description"));
                        if (categoryId == null) {
                            insert.setNull(4, java.sql.Types.INTEGER);
                        } else {
                            insert.setInt(4, categoryId);
                        }
                        insert.setString(
                                5,
                                nullSafeTimestamp(rows.getString("created_at")));
                        insert.setString(
                                6,
                                nullSafeTimestamp(rows.getString("updated_at")));
                        insert.executeUpdate();
                    }
                }
            }

            statement.execute("DROP TABLE recipes_legacy");
            statement.execute("PRAGMA foreign_keys = ON");
        }
    }

    private Integer migrateCategory(
            Connection connection,
            String categoryName) throws SQLException {
        if (categoryName == null || categoryName.isBlank()) {
            return null;
        }

        try (PreparedStatement select = connection.prepareStatement(
                "SELECT id FROM categories WHERE name = ?");
             PreparedStatement insert = connection.prepareStatement(
                     "INSERT OR IGNORE INTO categories(name) VALUES(?)",
                     Statement.RETURN_GENERATED_KEYS)) {
            select.setString(1, categoryName.trim());
            try (ResultSet result = select.executeQuery()) {
                if (result.next()) {
                    return result.getInt("id");
                }
            }

            insert.setString(1, categoryName.trim());
            insert.executeUpdate();
            try (ResultSet keys = insert.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : null;
            }
        }
    }

    private String nullSafeTimestamp(String timestamp) {
        return timestamp == null || timestamp.isBlank()
                ? java.time.LocalDateTime.now()
                        .format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                : timestamp;
    }

    private boolean tableExists(Connection connection, String tableName)
            throws SQLException {
        try (ResultSet tables = connection.getMetaData()
                .getTables(null, null, tableName, null)) {
            return tables.next();
        }
    }

    private int getSchemaVersion(Connection connection)
            throws SQLException {
        try (Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery(
                     "SELECT COALESCE(MAX(version), 0) FROM schema_migrations")) {
            return result.next() ? result.getInt(1) : 0;
        }
    }

    private void recordMigration(Connection connection, int version)
            throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("""
                INSERT OR IGNORE INTO schema_migrations(version, applied_at)
                VALUES(?, datetime('now'))
                """)) {
            statement.setInt(1, version);
            statement.executeUpdate();
        }
    }

    private void seedCategories(Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT OR IGNORE INTO categories(name) VALUES(?)")) {
            for (String name : new String[]{
                    "Завтрак",
                    "Обед",
                    "Ужин",
                    "Суп",
                    "Десерт",
                    "Напитки"
            }) {
                statement.setString(1, name);
                statement.executeUpdate();
            }
        }
    }
}
