# Design Document

**Project:** Cooking Notebook
**Version:** 1.0
**Status:** Approved
**Based on:** SSD v1.0

---

# 1. Purpose

This document describes how the Cooking Notebook application will be implemented.

SSD defines WHAT the application must do.

This document defines HOW the application will be built.

---

# 2. Technical Approach

## Technology Stack

- Java 21
- Maven
- JavaFX
- SQLite
- JDBC
- JUnit 5
- Git
- GitHub

## Architectural Style

Layered Architecture:

```text
JavaFX UI
    ↓
Service Layer
    ↓
Repository Layer
    ↓
SQLite Database
```

Business logic must exist only in the Service Layer.

---

# 3. Package Structure

```text
src/main/java/com/alexdakyko/cookingnotebook
│
├── model
├── service
├── repository
├── database
├── ui
└── config
```

## model

- Recipe
- Ingredient
- Category
- Photo
- ApplicationSettings

## service

- RecipeService
- PhotoService
- SettingsService

## repository

- RecipeRepository
- IngredientRepository
- CategoryRepository
- PhotoRepository
- SettingsRepository

## database

- DatabaseManager
- DatabaseInitializer

## ui

- MainWindow
- RecipeEditorView
- SettingsView

---

# 4. Core Entities

## Recipe

- id
- title
- description
- instructions
- preparationTime
- servings
- categoryId

## Ingredient

- id
- recipeId
- name
- quantity
- unit

## Category

- id
- name

## Photo

- id
- recipeId
- imageData

## ApplicationSettings

- theme
- language
- windowSize

---

# 5. Entity Relationships

```text
Category (1)
    |
    |---> (N) Recipe

Recipe (1)
    |
    |---> (N) Ingredient

Recipe (1)
    |
    |---> (N) Photo
```

---

# 6. Database Design

## SQLite File

Database file:

```text
cooking-notebook.db
```

All application data is stored in this database.

Foreign keys must be enabled:

```sql
PRAGMA foreign_keys = ON;
```

---

## Categories Table

```sql
CREATE TABLE categories (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL UNIQUE
);
```

## Recipes Table

```sql
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
);
```

## Ingredients Table

```sql
CREATE TABLE ingredients (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    recipe_id INTEGER NOT NULL,
    name TEXT NOT NULL,
    quantity REAL,
    unit TEXT,
    FOREIGN KEY(recipe_id) REFERENCES recipes(id) ON DELETE CASCADE
);
```

## Photos Table

```sql
CREATE TABLE photos (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    recipe_id INTEGER NOT NULL,
    file_name TEXT,
    mime_type TEXT,
    image_data BLOB NOT NULL,
    FOREIGN KEY(recipe_id) REFERENCES recipes(id) ON DELETE CASCADE
);
```

## Settings Table

```sql
CREATE TABLE settings (
    key TEXT PRIMARY KEY,
    value TEXT NOT NULL
);
```

---

# 7. User Interface Design

Single main window.

## Left Panel

Recipe list:

- recipe title
- category
- search box

## Right Panel

Recipe editor:

- title
- description
- instructions
- ingredients
- category
- servings
- preparation time
- photos

## Toolbar

- New Recipe
- Save Recipe
- Delete Recipe
- Import
- Export
- Settings

---

# 8. Import And Export

## Import

Supported:

- TXT
- Images

Images are stored in the SQLite database as BLOB data.

## Export

Supported:

- TXT
- Images

Temporary files must never become a second data storage mechanism.

---

# 9. Testing Strategy

Framework:

- JUnit 5

Focus:

- Repository tests
- Service tests
- Database tests

UI tests are optional for Version 1.

Repository tests must run without a graphical display.

---

# 10. Risks

| Risk | Mitigation |
|--------|--------|
| Large photos increase database size | Accept for V1 |
| SQLite driver issues | Use stable JDBC driver |
| JavaFX testing complexity | Focus on service and repository tests |

---

# 11. Development Rules

1. SSD is the source of truth.
2. Design must follow SSD.
3. Tasks must follow Design.
4. Code must follow Tasks.
5. Every feature requires tests.
6. All data must be stored in SQLite.

---

# 12. Version History

| Version | Description |
|-----------|-----------|
| 1.0 | First approved design aligned with SSD v1.0 |
