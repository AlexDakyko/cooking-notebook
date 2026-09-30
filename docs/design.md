# Design

**Project:** Cooking Notebook  
**Version:** 0.1  
**Date:** 2026-09-30  
**Status:** Draft from `docs/SSD.md` v0.3 — read the SSD before changing this file.

This file is **how** we will build the app. If design and the SSD disagree, **fix the SSD first**, then this file, then tasks.

---

## 1. Approach

- **Desktop client:** Java 21 + JavaFX + Maven.
- **System of record:** one **SQLite** file. Recipes, tags, settings, and **photo bytes** live in tables (photos as `BLOB`).
- **Files on disk** are only for **import/export** (user-chosen `.txt` / images) and for the SQLite file itself.
- **Layers:** UI (JavaFX) → services → repositories (JDBC). No business rules only in controllers.

```text
JavaFX UI  →  RecipeService / SettingsService  →  JDBC repositories  →  SQLite file
```

---

## 2. SQLite file

- Default path (to confirm in code): user data directory, e.g. under the user’s home, file name `cooking-notebook.db`.
- Create tables on first launch (simple `CREATE TABLE IF NOT EXISTS` in v1; migrations tool later if the schema grows).
- Backup: copy `cooking-notebook.db` while the app is closed.

---

## 3. Schema

Identifiers are integers. Renaming a title never breaks photo rows.

```sql
CREATE TABLE IF NOT EXISTS recipes (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  title TEXT NOT NULL,
  notes TEXT,
  created_at TEXT NOT NULL,
  updated_at TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS ingredients (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  recipe_id INTEGER NOT NULL,
  position INTEGER NOT NULL,
  text TEXT NOT NULL,
  FOREIGN KEY (recipe_id) REFERENCES recipes(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS steps (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  recipe_id INTEGER NOT NULL,
  position INTEGER NOT NULL,
  text TEXT NOT NULL,
  FOREIGN KEY (recipe_id) REFERENCES recipes(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS tags (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  recipe_id INTEGER NOT NULL,
  name TEXT NOT NULL,
  FOREIGN KEY (recipe_id) REFERENCES recipes(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS photos (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  recipe_id INTEGER NOT NULL,
  position INTEGER NOT NULL,
  file_name TEXT NOT NULL,
  mime_type TEXT NOT NULL,
  bytes BLOB NOT NULL,
  FOREIGN KEY (recipe_id) REFERENCES recipes(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS settings (
  key TEXT PRIMARY KEY,
  value TEXT NOT NULL
);
```

- Enable foreign keys: `PRAGMA foreign_keys = ON`.
- Skin: row `key = 'skin'`, `value = 'light'` or `'dark'`.
- Timestamps: ISO-8601 UTC strings for simplicity.

---

## 4. UI (version 1)

One window:

- **Left:** list of recipe titles (from `SELECT id, title FROM recipes ORDER BY updated_at DESC`).
- **Right:** editor (title, ingredients, steps, notes, tags, photo thumbnails).
- **Menu or toolbar:** New, Save, Delete, Import txt, Import photos, Export txt, Export photos, Settings (skin).

Copy-paste uses normal JavaFX text controls (FR-6).

---

## 5. Import / export

| Action | Behavior |
|--------|----------|
| Import `.txt` | Read UTF-8 text; map to title (first line) + body (rest) or all body into notes if empty — pick one mapping in implementation and test it. Persist via repositories, not leftover files. |
| Import photos | Read file bytes into `photos.bytes`. |
| Export `.txt` | Write current recipe fields to a user-chosen path. |
| Export photos | Write each blob to the chosen folder using `file_name`. |

Temporary files must not become a second database.

---

## 6. Testing

- **JUnit 5** + in-memory or temp-file SQLite for repository tests (CRUD, cascade delete, settings skin).
- Do **not** require a display for repository tests.
- JavaFX UI tests: optional later; not blocking version 1 data tests.

Map tests to requirement IDs in test names where practical, e.g. `saveRecipe_persistsTitle_FR4`.

---

## 7. Libraries (planned)

| Library | Role |
|---------|------|
| JavaFX | UI and CSS skins |
| SQLite JDBC (e.g. org.xerial:sqlite-jdbc) | Database |
| JUnit 5 | Tests |

No Spring in version 1.

---

## 8. Risks

| Risk | Mitigation |
|------|------------|
| Large photos inflate the `.db` file | Accept in v1; document backup size |
| SQLite native bits on Windows | Use a well-known JDBC driver that bundles natives |
| JavaFX tests on CI without UI | Keep domain tests headless |

---

## 9. History

| Version | Date | Change |
|---------|------|--------|
| 0.1 | 2026-09-30 | SQLite schema and layers from requirements v0.2 |
