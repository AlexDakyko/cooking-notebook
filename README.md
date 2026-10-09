# Cooking Notebook

A personal **Java 21 desktop application** for managing recipes.

Users can create, edit, organize, search, import, and export recipes, attach photos, and customize the application appearance with skins/themes.

**All data is stored locally in an SQLite database.**

Phase 0 focuses on documentation, architecture, and project rules. Development starts in Phase 1 as defined in the task list.

---

## Vision

Cooking Notebook is a local-first desktop application focused on simplicity, privacy, and long-term maintainability.

The application must work without internet access and keep all user data on the user's computer.

---

## How Work Is Done

1. Read docs/SSD.md
2. Create or update docs/design.md
3. Read design and create or update docs/tasks.md
4. Implement the next task
5. Add tests, run them, and fix failures
6. Commit changes when appropriate

AI workflows and project rules are defined in `.cursor/rules/`.

These rules may be followed by Cursor, Continue, Ollama-assisted workflows, or any compatible AI coding assistant.

---

## Documentation

| File | Purpose |
|------|---------|
| docs/SSD.md | Software Specification — what the application must do |
| docs/design.md | Technical design — architecture, database schema, layers, and UI decisions |
| docs/tasks.md | Ordered implementation checklist |
| docs/BEGINNER.md | Beginner-friendly project overview |

---

## Planned Technology Stack

- Java 21
- Maven
- JavaFX
- SQLite
- JUnit 5

---

## Project Status

| Item | Status |
|--------|--------|
| SSD | Completed |
| Design | Completed |
| Tasks | Completed |
| Java Code | Not Started |

---

## Data Storage

The SQLite database is the single source of truth for application data.

The database stores:

- Recipes
- Categories
- Photos (BLOB)
- Application settings
- Future metadata required by the project

No cloud storage is planned.

---

## Development Roadmap

### Phase 0

Documentation and project planning:

- SSD
- Design
- Tasks
- AI workflow setup

### Phase 1

Project bootstrap:

- Maven project structure
- Build configuration
- Dependency management
- Initial tests

### Phase 2+

Implementation of:

- Database layer
- Business logic
- JavaFX user interface
- Import/export functionality
- Photo management
- Themes and skins
- Search and filtering
- Packaging and release

---

## License

Personal learning project.
