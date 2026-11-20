# Copilot Notes - Cooking Notebook

Date: 2026-11-20

## Purpose

This file is a handoff for continuing the implementation and QA work on Cooking Notebook. It records the project purpose, current implementation state, architecture, functional gaps, defects, security findings, test backlog, and recommended refactoring order.

## Project Summary

Cooking Notebook is intended to be a local-first personal Java desktop recipe manager. The specification describes Java 21, Maven, JavaFX, SQLite, JUnit 5, and offline use.

The current executable implementation is a basic MVP that supports:

- Creating recipes with a title and description
- Editing recipes
- Deleting recipes with confirmation
- Searching by title or description
- Sorting by title ascending or descending
- Warn/confirm handling for unsaved edits before switching recipes
- Persistent storage in `cooking-notebook.db`
- Displaying created/updated timestamps

The interface is Russian-language in the FXML and controller alerts.

## Technology and Build

Dependencies and plugins are declared in `pom.xml`:

- Java 21
- JavaFX Controls and FXML 21.0.2
- SQLite JDBC 3.46.0.0
- JUnit Jupiter 5.10.2
- Maven Surefire 3.2.5
- JavaFX Maven plugin 0.0.8

Validation completed:

- `mvn test` passes
- `mvn clean test` passes
- Current tests run: 4
- Current test failures: 0
- Maven clean build also compiles all main and test sources successfully

Run the application with:

```powershell
mvn javafx:run
```

## Architecture

### Current flow

```text
JavaFX FXML UI
  -> RecipeController
  -> RecipeService
  -> RecipeRepository
  -> SQLiteRecipeRepository
  -> JDBC
  -> SQLite cooking-notebook.db
```

### Important files

- `src/main/java/com/cookingnotebook/Main.java`
  - JavaFX application entry point
  - Initializes the database
  - Loads `/recipe-list.fxml`
  - Opens an 800x600 window
- `src/main/resources/recipe-list.fxml`
  - Main recipe list and editor UI
  - Contains search, sorting, title, description, timestamps, save, and delete controls
- `src/main/java/com/cookingnotebook/controller/RecipeController.java`
  - Handles UI events and JavaFX state
  - Loads and updates recipes
  - Performs search and sorting
  - Tracks unsaved changes
  - Contains title validation
- `src/main/java/com/cookingnotebook/service/RecipeService.java`
  - Thin pass-through service
  - Constructs `SQLiteRecipeRepository` in its constructor
- `src/main/java/com/cookingnotebook/repository/RecipeRepository.java`
  - Repository interface for get/save/update/delete operations
- `src/main/java/com/cookingnotebook/repository/SQLiteRecipeRepository.java`
  - Implements CRUD using JDBC and parameterized SQL
- `src/main/java/com/cookingnotebook/database/DatabaseManager.java`
  - Opens the SQLite connection
  - Creates the initial recipes table
- `src/main/java/com/cookingnotebook/model/Recipe.java`
  - Domain model
  - Includes fields not yet persisted or used
- `src/test/java/com/cookingnotebook/RecipeTest.java`
  - Only title validation tests
- `src/main/java/com/cookingnotebook/DatabaseTest.java`
  - Manual database inspection helper, not a JUnit test
- `src/main/java/com/repository/InMemoryRecipeRepository.java`
  - Orphaned implementation in the wrong package

### Architectural assessment

- Dependencies are constructed internally instead of injected.
- The controller is coupled to JavaFX and also owns persistence-facing workflow logic.
- Business rules are mostly in `RecipeController`, not `RecipeService`.
- The service is not a real domain or validation layer.
- JDBC is isolated in the repository but database configuration is not configurable.
- The model and database schema do not match.
- The in-memory repository cannot be substituted cleanly and one update method is empty.
- Database initialization, repository operations, and startup are not test-isolated.

## Specification Versus Current Implementation

The project requirements and design are documented in:

- `docs/SSD.md`
- `docs/design.md`
- `docs/tasks.md`
- `docs/BEGINNER.md`

The specification describes a larger application, while the implementation has moved to an MVP but is incomplete relative to both the specification and design.

## Functional Gaps

Missing or not persisted:

1. Cooking instructions
2. Preparation time
3. Number of servings
4. Ingredients
5. Categories and custom category management
6. Recipe photos
7. Search by ingredient
8. Filtering by category
9. Duplicate recipe
10. Import/export of recipes
11. Import/export of photos
12. Backup and restore
13. Themes and settings
14. Copy-paste-specific behavior
15. Rich recipe detail fields
16. Database migrations and schema versioning
17. Unsaved-change confirmation on window shutdown
18. Application startup error screen
19. Stable sorting for equal titles
20. Database backup instructions in the application

The `Recipe` model contains instructions, preparation time, servings, category, and photo fields, but the actual repository and UI do not persist or edit these fields.

The database table created in `DatabaseManager` currently stores only:

- ID
- title
- description
- created timestamp
- updated timestamp
- category name

The design describes ingredients, steps, tags, photos, and settings tables, but none are implemented.

## Top 10 Defects

### 1. Database initialization failures are silently swallowed

`DatabaseManager.initializeDatabase()` catches `SQLException` and ignores it.

Impact:

- Application may start with an unavailable or incomplete database.
- Repository operations fail later with misleading symptoms.
- No startup error tells the user what happened.

Correction:

- Remove the silent catch.
- Throw a typed initialization exception.
- Log technical details.
- Show a clear JavaFX startup error.

### 2. Model and persisted schema are inconsistent

The model includes fields that SQL does not load or save.

Affected files:

- `src/main/java/com/cookingnotebook/model/Recipe.java`
- `src/main/java/com/cookingnotebook/repository/SQLiteRecipeRepository.java`

Correction:

- Establish one complete model/schema contract.
- Persist and load every field intentionally.
- Split the model into complete entities if one recipe object is no longer sufficient.

### 3. Timestamp parsing is strict and fragile

`SQLiteRecipeRepository` parses timestamps with `LocalDateTime.parse` into an ISO local datetime format.

Malformed, legacy, timezone-bearing, or differently formatted values can crash recipe loading.

Correction:

- Standardize timestamps.
- Use explicit formatters or migration conversion.
- Prefer ISO-8601 UTC strings and validate migrations.

### 4. Unsaved-change navigation logic is flawed

`RecipeController.confirmUnsavedChanges()` calls `saveRecipe(null)`.

That method mutates UI state and calls `resetEditor()`, which clears selection and form fields. This behavior mixes navigation and save logic.

Correction:

- Separate create, update, reset, and navigate operations.
- Save explicit model changes before navigation.
- Never reset the editor as a side effect of a navigation decision.

### 5. Update and delete do not verify affected rows

The repository calls `executeUpdate()` but ignores the result.

Stale IDs or missing records can appear successful.

Correction:

- Check affected-row count.
- Return a not-found/zero-update result.
- Raise a typed `RecipeNotFoundException`.
- Keep the UI and in-memory collections consistent.

### 6. Persistence failures are not handled by the UI

Repository methods wrap SQL errors in `RuntimeException`, but controller actions have no error handling.

Impact:

- Database failure can leave UI state inconsistent.
- Runtime exceptions may escape user interactions.

Correction:

- Add typed service/repository exceptions.
- Catch at controller/UI boundaries.
- Show concise alerts and log technical details.

### 7. Database location is dependent on working directory

`DatabaseManager` uses:

```text
jdbc:sqlite:cooking-notebook.db
```

Launching in different working directories can open or create different databases.

Correction:

- Resolve a stable user-data directory.
- Example: `<user.home>/.cooking-notebook/cooking-notebook.db`
- Make the database path injectable for tests.

### 8. No schema migration system

`CREATE TABLE IF NOT EXISTS` cannot safely upgrade existing databases.

Correction:

- Introduce ordered migration scripts or a migration framework.
- Track schema version.
- Test upgrading from every previous version.
- Back up before migrations.

### 9. In-memory repository is broken and misplaced

`src/main/java/com/repository/InMemoryRecipeRepository.java` is in package `com.repository` instead of `com.cookingnotebook.repository`.

Its `updateRecipe()` implementation is empty.

Correction:

- Move it to the repository package or remove it.
- Implement it fully for tests.
- Inject it through constructor or test configuration.

### 10. Test coverage is minimal

Current tests only verify title validation in `RecipeTest`.

Missing coverage includes CRUD, database integration, timestamps, search, sorting, unsaved changes, delete confirmation, startup, stale IDs, malformed data, and UI behavior.

Correction:

- Build tests around temporary SQLite databases.
- Use injectable repositories.
- Add TestFX for JavaFX behavior.

## Security Issues

### 1. No SQL injection risk from the current recipe fields

The CRUD implementation uses `PreparedStatement` and parameter binding. This is a positive finding.

### 2. SQLite database is unencrypted

Another process or OS user with file-system access can read or copy recipe text.

Actions:

- Document this clearly if the app remains local/plain SQLite.
- Restrict application-data directory permissions where supported.
- If confidentiality is a requirement, evaluate SQLCipher and threat-model it.

### 3. Database file permissions are uncontrolled

The code does not configure private directories or ACLs.

Correction:

- Create private application-data directories.
- Verify platform permissions.
- Do not rely on obscurity.

### 4. No integrity or corruption protection

The database has no startup integrity validation, locking policy, backup, or restore mechanism.

Correction:

- Validate startup.
- Add backup/restore.
- Add migration-safe backup before schema changes.

### 5. Technical errors need a secure presentation policy

Current exceptions contain SQL details and could later be written into logs or alerts.

Correction:

- Show concise user-facing messages.
- Log technical details separately.
- Never place recipe contents in logs unnecessarily.

### 6. Future import/photo handling needs validation

When import/export and photo support are implemented:

- Validate size and MIME type
- Validate or normalize file names
- Do not execute imported content
- Limit memory use
- Handle decompression safely
- Avoid path traversal
- Store photos using bounded formats and size limits

### 7. Dependency security

No dependency vulnerability process is visible.

Correction:

- Add dependency checks to CI.
- Regularly update JavaFX, SQLite JDBC, JUnit, and Maven plugins.

## Test Cases to Implement

### Domain and service tests

1. Null title is invalid
2. Empty title is invalid
3. Whitespace-only title is invalid
4. Valid title is accepted
5. Valid title is trimmed
6. Maximum title length
7. Save creates both timestamps
8. Update changes updated timestamp
9. Update preserves created timestamp
10. Delete existing recipe
11. Delete unknown ID
12. Update unknown ID
13. Null description is handled
14. Repository preserves complete model fields
15. Search is case-insensitive
16. Search matches title
17. Search matches description
18. Search ingredient support once implemented
18. Empty search returns all recipes
19. Sorting ascending is case-insensitive
20. Sorting descending is case-insensitive
21. Duplicate titles are handled consistently
22. Null model fields do not corrupt persistence
23. Repository failures become meaningful service exceptions

### SQLite integration tests

Use a temporary database and injected database URL.

1. Initialization creates the recipes table
2. Initialization is idempotent
3. Reopening reloads recipes
4. Insert assigns generated ID
5. Insert persists title and description
6. Insert persists timestamps
7. Insert persists complete model fields once schema is complete
8. Update persists complete model fields
9. Delete removes a record
10. Missing update affects zero rows
11. Missing delete affects zero rows
12. Malformed timestamps are handled
13. Schema migrations upgrade old databases
14. Re-running migrations is safe
15. Partial SQL changes roll back
16. Foreign keys are enabled
17. Cascade deletes work for ingredients/photos
18. Search behaves consistently
19. Each test uses isolated database state

### JavaFX/controller tests

Prefer TestFX.

1. Initialization loads existing recipes
2. Selecting a recipe fills title and description
3. Editing marks form unsaved
4. Loading a recipe does not mark it unsaved
5. Save creates a new recipe
6. Save updates an existing recipe
7. Invalid title shows validation alert
8. Discarding unsaved changes navigates away
9. Cancelling navigation leaves both recipes unchanged
10. Saving unsaved changes before navigation succeeds
11. Save failure keeps the editor usable
12. Delete confirmation is required
13. Cancelled delete keeps recipe
14. Confirmed delete removes recipe and clears editor
15. Delete without selection shows warning
16. Search updates results immediately
17. Clearing search restores all recipes
18. Sorting updates list order
19. Switching sort mode preserves filtering
20. Window close handles unsaved changes
21. Database failure shows an error instead of crashing

### Acceptance tests

1. Start with no database
2. Start with existing database
3. Create recipe, close, reopen, and see it
4. Delete recipe, close, reopen, and confirm absence
5. Search/sort without unexpected selection changes
6. Create recipe containing apostrophes and special characters
7. Resize window to small and large sizes
8. Enter a long title and description
9. Complete core workflow using keyboard
10. Verify validation feedback is understandable

### Future feature tests

1. Category CRUD
2. Ingredient CRUD
3. Photo file validation
4. Photo database storage
5. Import/export round trip
6. Backup/restore round trip
7. Theme persistence
8. Migration from every previous schema version
9. Corrupt database startup behavior
10. Encryption behavior if SQLCipher is adopted

## Refactoring Plan

### Phase 1: Stabilize database startup

1. Remove silent database initialization catches.
2. Add a stable application-data directory.
3. Add injected database URL/path configuration.
4. Add schema versioning and migrations.
5. Enable `PRAGMA foreign_keys = ON`.
6. Add startup failure handling in `Main`.
7. Replace `DatabaseTest` with JUnit integration tests.

### Phase 2: Fix dependency boundaries

1. Constructor-inject `RecipeRepository` into `RecipeService`.
2. Constructor-inject `RecipeService` into `RecipeController`.
3. Compose dependencies in `Main` or a dedicated application factory.
4. Move or remove the orphaned in-memory repository.
5. Add a complete in-memory repository for unit tests.
6. Make the controller independent of concrete SQLite classes.

Target shape:

```text
RecipeController
  -> RecipeService
  -> RecipeRepository
  -> SQLiteRecipeRepository
```

### Phase 3: Complete the domain contract

1. Choose whether the recipe model directly contains ingredients/photos or references them.
2. Implement the complete schema.
3. Add category, ingredient, photo, step, tag, and settings entities where required.
4. Explicitly map every persisted field.
5. Add migration tests.
6. Standardize timestamps, preferably ISO-8601 UTC.

### Phase 4: Correct application behavior

1. Split create, update, reset, and navigation methods.
2. Remove collection mutation from unsaved-change confirmation.
3. Check update/delete affected-row counts.
4. Add typed exceptions:
   - `DatabaseException`
   - `ValidationException`
   - `RecipeNotFoundException`
   - `SchemaInitializationException`
5. Add controller error alerts.
6. Handle unsaved changes on window close.
7. Make repository failures leave the UI in a recoverable state.

### Phase 5: Establish testing

1. Replace manual database inspection with JUnit integration tests.
2. Use temporary SQLite database files.
3. Add Mockito or lightweight fake repositories.
4. Add TestFX for JavaFX behavior.
5. Add `mvn clean test` to CI.
6. Add coverage reporting.
7. Keep repository tests headless and UI tests display-aware.

### Phase 6: Refactor controller and service responsibilities

1. Move validation and recipe create/update rules into `RecipeService`.
2. Introduce create/update commands or DTOs.
3. Move search/filter/sort to testable service/view-model methods.
4. Keep FXML field bindings in the UI layer.
5. Introduce view-model state for unsaved changes if controller size grows.

### Phase 7: Implement planned vertical feature slices

Implement in this order:

1. Instructions and servings
2. Preparation time
3. Ingredients
4. Categories
5. Photos
6. Recipe search/filter
7. Import/export
8. Themes/settings
9. Backup/restore
10. Packaging and release

For every feature:

- add migration
- update entity/model
- update repository
- update service
- update UI
- add unit/integration/UI tests
- update documentation

## Recommended Immediate Work

The first work session should proceed in this order:

1. Add a stable database path and test database configuration.
2. Remove silent database initialization exceptions.
3. Verify foreign keys and timestamp format.
4. Introduce migration/version support.
5. Constructor-inject repository/service dependencies.
6. Fix update/delete affected-row handling.
7. Refactor unsaved-change navigation.
8. Add temporary-database CRUD tests.
9. Add controller tests for save, delete, switch, search, and sort.
10. Only then implement missing domain fields/features.

## Resume Context

The project currently compiles and the current 4 title-validation tests pass. No code changes were made during the whole-project audit. The database `cooking-notebook.db` is real workspace state, but the database layer is not robust or test-isolated.

The next implementation priority is not to add the remaining product features first. The next priority is to correct database startup, model/schema consistency, dependency injection, error handling, row-count verification, and navigation state behavior.
