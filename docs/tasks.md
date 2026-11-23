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

- [x] T1.1 Create Maven project
- [x] T1.2 Configure Java 21
- [x] T1.3 Add JavaFX dependencies
- [x] T1.4 Add SQLite JDBC dependency
- [x] T1.5 Add JUnit 5 dependency
- [x] T1.6 Create project package structure
- [x] T1.7 Create application entry point
- [x] T1.8 Verify application starts
- [ ] T1.9 Commit project setup

---

# Phase 2 – Database Foundation

- [x] T2.1 Create DatabaseManager
- [x] T2.2 Create DatabaseInitializer
- [x] T2.3 Enable SQLite foreign keys
- [x] T2.4 Create categories table
- [x] T2.5 Create recipes table
- [x] T2.6 Create ingredients table
- [x] T2.7 Create photos table
- [x] T2.8 Create settings table
- [x] T2.9 Verify database creation on first launch
- [x] T2.10 Create database initialization tests
- [x] T2.11 Run tests and fix failures
- [ ] T2.12 Commit database foundation

---

# Phase 3 – Domain Model

- [x] T3.1 Create Recipe entity
- [x] T3.2 Create Ingredient entity
- [x] T3.3 Create Category entity
- [x] T3.4 Create Photo entity
- [x] T3.5 Create ApplicationSettings entity
- [x] T3.6 Create model tests

---

# Phase 4 – Repository Layer

- [x] T4.1 Create CategoryRepository
- [x] T4.2 Create RecipeRepository
- [x] T4.3 Create IngredientRepository
- [x] T4.4 Create PhotoRepository
- [x] T4.5 Create SettingsRepository
- [x] T4.6 Implement recipe CRUD operations
- [x] T4.7 Implement category CRUD operations
- [x] T4.8 Implement ingredient persistence
- [x] T4.9 Implement photo persistence
- [x] T4.10 Implement settings persistence
- [x] T4.11 Create repository tests
- [x] T4.12 Verify cascade delete behavior
- [x] T4.13 Run tests and fix failures
- [ ] T4.14 Commit repository layer

---

# Phase 5 – Service Layer

- [x] T5.1 Create RecipeService
- [x] T5.2 Create PhotoService
- [x] T5.3 Create SettingsService
- [x] T5.4 Add validation rules
- [x] T5.5 Add search by recipe title
- [x] T5.6 Add filtering by category
- [x] T5.7 Create service tests
- [x] T5.8 Run tests and fix failures
- [ ] T5.9 Commit service layer

---

# Phase 6 – User Interface

- [x] T6.1 Create MainWindow
- [x] T6.2 Create recipe list panel
- [x] T6.3 Create recipe editor panel
- [x] T6.4 Create toolbar actions
- [x] T6.5 Implement New Recipe feature
- [x] T6.6 Implement Save Recipe feature
- [x] T6.7 Implement Delete Recipe feature
- [x] T6.8 Implement recipe search UI
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
