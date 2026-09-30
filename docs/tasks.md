# Tasks

**Project:** Cooking Notebook  
**Date:** 2026-09-30  
**Status:** Backlog — implement **in order**. Check a box only when that task is done, tested (if it has tests), and errors from that slice are fixed.

Derived from [`SSD.md`](SSD.md) and [`design.md`](design.md). If a task conflicts with those files, **stop and update docs first**.

Workflow: read SSD → read design → implement the next unchecked task → tests → run tests → fix → commit that slice (when the user wants a git commit, or per project rules).

---

## Phase 0 — Documentation (current)

- [x] T0.1 Write `docs/SSD.md` (what, including FR-DATA / SQLite)
- [x] T0.2 Write `docs/design.md` (how)
- [x] T0.3 Write this `docs/tasks.md`
- [x] T0.4 Add Cursor rules for the spec workflow and “data lives in the DB”

---

## Phase 1 — Runnable project

- [ ] T1.1 Maven project, Java 21, JavaFX, JUnit 5, SQLite JDBC
- [ ] T1.2 App starts and shows an empty window
- [ ] T1.3 Open (or create) the SQLite file and run schema from design
- [ ] T1.4 Tests: database file opens; tables exist
- [ ] T1.5 Run tests; fix failures

---

## Phase 2 — Recipes in the database (FR-1 … FR-5, FR-DATA)

- [ ] T2.1 Recipe repository: insert, get by id, list, update, delete (cascade)
- [ ] T2.2 Ingredients, steps, tags saved and loaded with the recipe
- [ ] T2.3 UI: list + editor + New / Save / Delete with confirm
- [ ] T2.4 Tests for CRUD and cascade delete (FR-4, FR-5)
- [ ] T2.5 Run tests; fix failures

---

## Phase 3 — Copy-paste, import, export (FR-6 … FR-11, FR-14, FR-15)

- [ ] T3.1 Text fields support standard copy/cut/paste (FR-6)
- [ ] T3.2 Import `.txt` into DB (FR-7, FR-9)
- [ ] T3.3 Import photos as BLOBs (FR-8, FR-9)
- [ ] T3.4 Export `.txt` and photos to user-chosen paths (FR-10, FR-11, FR-15)
- [ ] T3.5 Tests for import/export mapping and photo blob round-trip
- [ ] T3.6 Run tests; fix failures

---

## Phase 4 — Skins (FR-12, FR-13)

- [ ] T4.1 Light and dark CSS
- [ ] T4.2 Persist `skin` in `settings` table; apply on startup
- [ ] T4.3 Tests: setting survives a new repository session
- [ ] T4.4 Run tests; fix failures

---

## Phase 5 — Polish

- [ ] T5.1 Missing/unreadable photo: message, no crash (FR-14)
- [ ] T5.2 README: how to run, where the `.db` file is, how to back it up
- [ ] T5.3 Full test run; fix remaining errors

---

## Done when

All version-1 boxes above are checked, tests pass, and git has commits for the slices you asked to record.
