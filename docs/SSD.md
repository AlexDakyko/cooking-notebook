# Software Specification Document (SSD)

## Project: Cooking Notebook

Version: 1.0

Status: Planning & Design Phase

Last Updated: October 2026

---

# 1. Product Overview

Cooking Notebook is a desktop application for storing, organizing, searching, and managing cooking recipes.

The application is intended for personal use and runs completely on the local computer without requiring internet access.

The main goal is to provide a convenient digital recipe notebook with support for ingredients, recipe categories, photos, searching, filtering, and future data import/export.

---

# 2. Target Users

### Primary User

A home cook who wants to:

- store recipes digitally;
- organize recipes by category;
- quickly search for recipes;
- attach photos to recipes;
- manage a growing personal collection of recipes.

### Usage Model

The application is designed as a:

- single-user desktop application;
- local-first application;
- offline application.

No user accounts or authentication are required.

---

# 3. Functional Requirements

## FR-001 Recipe Management

The system shall allow users to:

- create recipes;
- edit recipes;
- delete recipes;
- view recipe details;
- duplicate recipes.

Each recipe shall contain:

- title;
- description;
- cooking instructions;
- preparation time;
- number of servings;
- category;
- ingredient list;
- optional photo.

## FR-002 Ingredient Management

The system shall allow users to:

- add ingredients to recipes;
- update ingredients;
- remove ingredients.

Each ingredient shall contain:

- name;
- quantity;
- measurement unit.

## FR-003 Category Management

The system shall support recipe categorization.

Example categories:

- Breakfast
- Lunch
- Dinner
- Soup
- Dessert
- Drinks

Users shall be able to create additional custom categories.

## FR-004 Search and Filtering

The system shall support:

- search by recipe title;
- search by ingredient name;
- filtering by category.

## FR-005 Photo Support

The system shall allow users to attach photos to recipes.

Photo data shall be stored in the application database.

## FR-006 Data Persistence

All application data shall be stored permanently.

The system shall preserve:

- recipes;
- ingredients;
- categories;
- photos;
- application settings.

## FR-007 Import and Export

Future versions shall support:

- export recipes;
- import recipes;
- backup creation;
- backup restoration.

## FR-008 Application Settings

The system shall allow users to configure:

- application theme;
- appearance options;
- future preference settings.

---

# 4. Non-Functional Requirements

## NFR-001 Performance

The application should start within a few seconds.

## NFR-002 Reliability

The application shall not lose user data during normal operation.

## NFR-003 Maintainability

The codebase shall follow clean architecture principles.

## NFR-004 Usability

The user interface should be simple and intuitive.

## NFR-005 Offline Usage

The application must function without internet access.

## NFR-006 Portability

The application should run on modern Windows systems.

---

# 5. Technology Stack

- Java 21
- Maven
- JavaFX
- SQLite
- JUnit 5
- Git
- GitHub

---

# 6. High-Level Architecture

```text
JavaFX UI
    ↓
Service Layer
    ↓
Repository Layer
    ↓
SQLite Database
```

### JavaFX UI

Responsible for user interaction.

### Service Layer

Responsible for business logic and validation.

### Repository Layer

Responsible for database access and CRUD operations.

### Database Layer

Responsible for persistent storage.

---

# 7. Core Entities

## Recipe

- id
- title
- description
- instructions
- preparationTime
- servings
- categoryId
- photoId

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

# 8. Out of Scope

The following features are not included in the first version:

- cloud synchronization;
- multi-user support;
- online recipe sharing;
- mobile application;
- web application;
- social features;
- user authentication;
- external API integrations.

---

# 9. MVP Roadmap

## MVP-1

Core recipe management.

## MVP-2

SQLite persistence.

## MVP-3

Search and filtering.

## MVP-4

Photo support.

## MVP-5

Themes and settings.

## MVP-6

Import / Export.

---

# 10. Future Enhancements

- advanced search;
- import/export formats;
- printing recipes;
- shopping lists;
- nutrition information;
- Linux support;
- macOS support.

---

# 11. Development Workflow

SSD
→ Design
→ Tasks
→ Implementation
→ Testing
→ Commit

This document serves as the primary source of truth for the project.
