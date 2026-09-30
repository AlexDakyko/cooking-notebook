# Beginner notes

## What we did

1. Created the `cooking-notebook` folder
2. Wrote the **SSD** (what the app must do), **design** (how), and **tasks** (order of work)
3. Decided: **everything you save lives in a database** (SQLite file on this PC)
4. Added **Cursor rules** so the assistant follows: SSD → design → tasks → code → tests → fix → commit (when you ask)

Application **code is not started yet**. Next coding step is Phase 1 in `docs/tasks.md` (window + database file).

## Words

| Term | Meaning |
|------|---------|
| **SSD** | Software Specification Document — what the product must do (the main spec) |
| **Design** | How we will build it (JavaFX, SQLite tables, tests) |
| **Tasks** | Checklist in order |
| **Database (DB)** | One file (`cooking-notebook.db`) that stores recipes, photos, and skin setting |
| **Skin** | Light/dark look of the windows |

## What you will need later (not needed only to read docs)

- JDK 21
- Maven (or a wrapper we add in Phase 1)
- Cursor (you have this)

## Suggested next step

Ask to **implement Phase 1** in `docs/tasks.md` (Maven + empty window + SQLite schema).
