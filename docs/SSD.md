# Software Specification Document (SSD)

**Project:** Cooking Notebook  
**Version:** 0.3  
**Date:** 2026-09-30  
**Status:** Source of truth for **what** the product must do. Code starts when tasks in `docs/tasks.md` are implemented.

Do not implement features that are not in this SSD (or a later version of it).

Related files: [`design.md`](design.md) (how), [`tasks.md`](tasks.md) (work order).

---

## 1. Product

Cooking Notebook is a **personal recipe notebook**: a **Java desktop** app on this computer.

The user can write recipes, copy-paste text, upload and download **txt** and **photos**, change **skins**, and add features later **without losing data**.

**One user on this PC.** No login in version 1. No internet required.

**Persistence:** all app data is stored in a **local database** (see FR-DATA). Import/export files are only a way to move data in and out; they are not the system of record.

---

## 2. Users

| User | Need |
|------|------|
| Home cook | Keep recipes, notes, and photos in one place |
| Beginner developer | Small project that grows in clear steps |

---

## 3. Functional requirements

IDs are stable. Tasks and tests should refer to them (e.g. `FR-1`).

### Data and notebook

| ID | Requirement |
|----|----------------|
| **FR-DATA** | Persist **all** durable data in a **local database**: recipes, ingredients, steps, notes, tags, photos (image bytes), timestamps, and user settings (including selected skin). No JSON/file tree as the primary store. |
| **FR-1** | Create a new recipe (default title such as “Untitled recipe”). |
| **FR-2** | Open a recipe from a list of saved recipes. |
| **FR-3** | Edit title, ingredients, steps, notes, and tags. |
| **FR-4** | Save changes so they survive closing the app (database commit). |
| **FR-5** | Delete a recipe only after confirmation; delete related photos and text in the database. |
| **FR-6** | Copy, cut, and paste **plain text** in title, ingredients, steps, and notes (Ctrl+C / Ctrl+V / Ctrl+X). |

### Upload and download

| ID | Requirement |
|----|----------------|
| **FR-7** | Upload a `.txt` file: create a new recipe or fill the current one from that text. Content is then stored in the database. |
| **FR-8** | Upload photo files (at least `.jpg` / `.jpeg` and `.png`): attach them to the current recipe; store image bytes in the database. |
| **FR-9** | Reject unsupported file types with a clear message; the app must not crash. |
| **FR-10** | Download / export the current recipe as a `.txt` file chosen by the user. |
| **FR-11** | Download / export attached photos as image files to a folder the user chooses. |

### Skins

| ID | Requirement |
|----|----------------|
| **FR-12** | Provide at least **Light** and **Dark** skins. |
| **FR-13** | Remember the selected skin in the database and apply it on the next launch. |

### Quality of behavior

| ID | Requirement |
|----|----------------|
| **FR-14** | Show a readable error if a photo blob cannot be displayed; do not crash. |
| **FR-15** | Do not delete or overwrite files outside what the user picked in import/export dialogs. |

---

## 4. Recipe content

| Field | Required | Notes |
|-------|----------|--------|
| Title | Yes | Shown in the list |
| Ingredients | No | List of lines |
| Steps | No | Numbered or free text |
| Notes | No | Tips, variants |
| Tags | No | Free text tags (e.g. `dessert`, `quick`) |
| Photos | No | One or more images in the database |
| Created / updated | Yes | Set automatically |

---

## 5. Non-functional requirements

| ID | Requirement |
|----|----------------|
| **NFR-1** | Language: Java 21. UI: JavaFX. Build: Maven. |
| **NFR-2** | Database: local **SQLite** file on this PC (no server, no cloud). |
| **NFR-3** | App usable without a network connection. |
| **NFR-4** | Backup = copy the SQLite file (and document where it lives). |
| **NFR-5** | Automated tests for database and domain logic (JUnit). UI tests may come later. |

---

## 6. Out of scope (version 1)

- Cloud sync, accounts, multi-user
- Public website or social features
- Nutrition, shopping-list AI, video
- Mobile apps
- Zip “recipe pack” export (nice later, not required now)

---

## 7. Future (not version 1)

Search/filters, servings scaler, print view, extra import formats, cookbooks/collections, step timer, unit conversion.

These must still use the **same database** when added.

---

## 8. Open decisions (defaults if unset)

| Topic | Default until you change it |
|-------|-----------------------------|
| Save | Explicit **Save** button (plus save on confirmed navigation if needed) |
| Tags | Free text, stored in the database |
| Photo size | Store as uploaded; no compression required in v1 |

---

## 9. History

| Version | Date | Change |
|---------|------|--------|
| 0.1 | 2026-09-30 | First SSD draft |
| 0.2 | 2026-09-30 | All durable data in a local database |
| 0.3 | 2026-09-30 | SSD is the canonical “what” document (not `requirements.md`) |
