# Tasks

**Project:** Cooking Notebook
**Version:** 1.0
**Status:** Approved Backlog
**Based on:** SSD v1.0 and Design v1.0

---

# Workflow

Read SSD → Read Design → Implement next task → Write tests → Run tests → Fix issues → Commit.

A task can be marked complete only when:

- implementation is finished;
- related tests pass;
- defects for that task are fixed.

---

# Phase 0 – Documentation

- [x] T0.1 Create SSD.md
- [x] T0.2 Create design.md
- [x] T0.3 Create tasks.md
- [x] T0.4 Configure AI workflow rules

---

# Phase 1 – Project Setup

- [ ] T1.1 Create Maven project
- [ ] T1.2 Configure Java 21
- [ ] T1.3 Add JavaFX dependencies
- [ ] T1.4 Add SQLite JDBC dependency
- [ ] T1.5 Add JUnit 5 dependency
- [ ] T1.6 Create project package structure
- [ ] T1.7 Create application entry point
- [ ] T1.8 Verify application starts
- [ ] T1.9 Commit project setup

---

# Phase 2 – Database Foundation

- [ ] T2.1 Create DatabaseManager
- [ ] T2.2 Create DatabaseInitializer
- [ ] T2.3 Enable SQLite foreign keys
- [ ] T2.4 Create categories table
- [ ] T2.5 Create recipes table
- [ ] T2.6 Create ingredients table
- [ ] T2.7 Create photos table
- [ ] T2.8 Create settings table
- [ ] T2.9 Verify database creation on first launch
- [ ] T2.10 Create database initialization tests
- [ ] T2.11 Run tests and fix failures
- [ ] T2.12 Commit database foundation

---

# Phase 3 – Domain Model

- [ ] T3.1 Create Recipe entity
- [ ] T3.2 Create Ingredient entity
- [ ] T3.3 Create Category entity
- [ ] T3.4 Create Photo entity
- [ ] T3.5 Create ApplicationSettings entity
- [ ] T3.6 Create model tests
- [ ] T3.7 Commit domain model

---

# Phase 4 – Repository Layer

- [ ] T4.1 Create CategoryRepository
- [ ] T4.2 Create RecipeRepository
- [ ] T4.3 Create IngredientRepository
- [ ] T4.4 Create PhotoRepository
- [ ] T4.5 Create SettingsRepository
- [ ] T4.6 Implement recipe CRUD operations
- [ ] T4.7 Implement category CRUD operations
- [ ] T4.8 Implement ingredient persistence
- [ ] T4.9 Implement photo persistence
- [ ] T4.10 Implement settings persistence
- [ ] T4.11 Create repository tests
- [ ] T4.12 Verify cascade delete behavior
- [ ] T4.13 Run tests and fix failures
- [ ] T4.14 Commit repository layer

---

# Phase 5 – Service Layer

- [ ] T5.1 Create RecipeService
- [ ] T5.2 Create PhotoService
- [ ] T5.3 Create SettingsService
- [ ] T5.4 Add validation rules
- [ ] T5.5 Add search by recipe title
- [ ] T5.6 Add filtering by category
- [ ] T5.7 Create service tests
- [ ] T5.8 Run tests and fix failures
- [ ] T5.9 Commit service layer

---

# Phase 6 – User Interface

- [ ] T6.1 Create MainWindow
- [ ] T6.2 Create recipe list panel
- [ ] T6.3 Create recipe editor panel
- [ ] T6.4 Create toolbar actions
- [ ] T6.5 Implement New Recipe feature
- [ ] T6.6 Implement Save Recipe feature
- [ ] T6.7 Implement Delete Recipe feature
- [ ] T6.8 Implement recipe search UI
- [ ] T6.9 Implement category filter UI
- [ ] T6.10 Display recipe photos
- [ ] T6.11 Perform manual UI testing
- [ ] T6.12 Commit UI implementation

---

# Phase 7 – Import and Export

- [ ] T7.1 Import recipes from TXT
- [ ] T7.2 Export recipes to TXT
- [ ] T7.3 Import photos into database
- [ ] T7.4 Export photos to folder
- [ ] T7.5 Create import/export tests
- [ ] T7.6 Run tests and fix failures
- [ ] T7.7 Commit import/export features

---

# Phase 8 – Themes and Settings

- [ ] T8.1 Create Light theme
- [ ] T8.2 Create Dark theme
- [ ] T8.3 Save selected theme in settings table
- [ ] T8.4 Load theme on startup
- [ ] T8.5 Create settings tests
- [ ] T8.6 Run tests and fix failures
- [ ] T8.7 Commit themes and settings

---

# Phase 9 – Stabilization

- [ ] T9.1 Handle missing photos gracefully
- [ ] T9.2 Improve error messages
- [ ] T9.3 Complete README
- [ ] T9.4 Verify database backup procedure
- [ ] T9.5 Full regression test run
- [ ] T9.6 Fix remaining defects
- [ ] T9.7 Create Version 1.0 release tag

---

# Definition of Done

Version 1.0 is complete when:

- all tasks are completed;
- all automated tests pass;
- manual verification succeeds;
- documentation is updated;
- source code is committed to Git.
