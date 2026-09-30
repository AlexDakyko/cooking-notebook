# Cooking Notebook

A personal **Java** desktop app for recipes: write, copy-paste, import/export txt and photos, skins. **All saved data lives in a local SQLite database.**

Phase 0 is documentation and project rules. Code starts at Phase 1 in the task list.

## How work is done

1. Read [`docs/SSD.md`](docs/SSD.md)
2. Create or update [`docs/design.md`](docs/design.md)
3. Read design, create or update [`docs/tasks.md`](docs/tasks.md)
4. Implement the next task
5. Add tests, run them, fix failures
6. Commit when you ask (or when you want that workflow step)

Cursor enforces this in `.cursor/rules/`.

## Docs

| File | Purpose |
|------|---------|
| [SSD.md](docs/SSD.md) | Software specification — **what** the app must do |
| [design.md](docs/design.md) | **How** (SQLite schema, layers, UI) |
| [tasks.md](docs/tasks.md) | Ordered checklist |
| [BEGINNER.md](docs/BEGINNER.md) | Short tour |

## Planned stack

- Java 21, Maven, JavaFX
- SQLite (recipes, photos as BLOBs, settings)

## Status

| Item | Status |
|------|--------|
| SSD / design / tasks | Yes |
| Database as system of record (specified) | Yes |
| Java code | Not started |
