# StudyFlow

**A focused Android academic productivity app for students.**

StudyFlow helps secondary-school, college, and university students organise
their academic responsibilities in one place — tasks, subjects, study sessions,
progress, and reminders — with offline-first local storage.

Built with **Kotlin**, **Jetpack Compose**, and **Room (SQLite)**.

---

## Table of Contents

1. [Overview](#overview)
2. [Features](#features)
3. [Tech Stack](#tech-stack)
4. [Project Structure](#project-structure)
5. [Architecture](#architecture)
6. [Database Schema](#database-schema)
7. [Getting Started](#getting-started)
8. [Building & Running](#building--running)
9. [Testing the App](#testing-the-app)
10. [Version Compatibility](#version-compatibility)
11. [Roadmap](#roadmap)
12. [Known Limitations](#known-limitations)
13. [Author](#author)
14. [License](#license)

---

## Overview

Existing student planners (My Study Life, Student Task Planner, Tractivity)
demonstrate that successful academic tools combine **task management**,
**scheduling**, **study tracking**, and **progress feedback**. StudyFlow
distils these into a focused, offline-first app without the bloat of
commercial alternatives.

The app was designed around three principles:

- **Simplicity** — a small number of clearly organised features
- **Offline-first** — everything works without internet, local SQLite is the source of truth
- **Student-focused** — tasks, subjects, study sessions, and progress in one place

---

## Features

### Authentication (Local)
- Email + password registration with **salted SHA-256 hashing**
- Login with input validation and friendly error messages
- Persistent sessions (auto-login on app restart)
- Logout with full session cleanup

### Tasks
- Create, edit, complete, and delete academic tasks
- Fields: title, description, subject, due date, priority (Low / Med / High)
- Filter by **All / Upcoming / Completed**
- Visual priority badges and strike-through on completed items
- Delete confirmation dialog

### Subjects
- Create, edit, and delete subjects
- Fields: name, code, colour (8-colour palette), optional lecturer
- Colour-coded list for quick identification

### Study Sessions
- Log study sessions with duration (15 / 25 / 45 min quick-start)
- Optional subject association and notes
- **Today's total** and **weekly total** stat tiles
- Recent sessions list with timestamps

### Dashboard (Home)
- Live counters for **Pending** and **Completed** tasks
- Navigation to Tasks, Subjects, Study Sessions, and Settings
- Quick access to Logout

### Settings
- Display name
- Daily study goal (30 / 60 / 90 / 120 min)
- Preferred language (English / isiZulu / Sesotho)
- Theme (Dark / Light)

### UI / UX
- StudyFlow brand: dark blue (`#173B67`) + green (`#2E9B68`)
- Animated splash: glow → logo fade/scale → app name → tagline
- Consistent Compose Material 3 design language
- Empty states, loading indicators, and inline validation everywhere

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin 2.0.21 |
| UI | Jetpack Compose + Material 3 |
| Navigation | Navigation Compose 2.8.0 |
| Local persistence | Room 2.6.1 (SQLite) |
| Async | Kotlin Coroutines + Flow |
| DI | Manual (Application-scoped singletons) |
| Annotation processing | KSP (Kotlin Symbol Processing) |
| Build | Gradle 8.x + AGP 8.5.2 |
| Min SDK | 26 (Android 8.0 Oreo) |
| Target SDK | 34 (Android 14) |

**No Firebase, no network dependency — the app is fully self-contained.**

---

## Project Structure

```
StudyFlow/
├── app/
│   ├── build.gradle.kts
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/example/studyflow/
│       │   ├── MainActivity.kt
│       │   ├── StudyFlowApp.kt
│       │   ├── navigation/
│       │   │   ├── Routes.kt
│       │   │   └── StudyFlowNavHost.kt
│       │   ├── data/
│       │   │   ├── local/
│       │   │   │   ├── AppDatabase.kt
│       │   │   │   ├── Converters.kt
│       │   │   │   ├── UserEntity.kt / UserDao.kt
│       │   │   │   ├── SessionEntity.kt / SessionDao.kt
│       │   │   │   ├── TaskEntity.kt / TaskDao.kt
│       │   │   │   ├── SubjectEntity.kt / SubjectDao.kt
│       │   │   │   └── StudySessionEntity.kt / StudySessionDao.kt
│       │   │   ├── repository/
│       │   │   │   ├── AuthRepository.kt
│       │   │   │   ├── PasswordHasher.kt
│       │   │   │   ├── TaskRepository.kt
│       │   │   │   ├── SubjectRepository.kt
│       │   │   │   └── StudySessionRepository.kt
│       │   │   └── prefs/
│       │   │       └── SessionManager.kt
│       │   └── ui/
│       │       ├── theme/
│       │       ├── components/
│       │       │   ├── StudyFlowLogo.kt
│       │       │   └── ColorUtils.kt
│       │       └── screens/
│       │           ├── splash/SplashScreen.kt
│       │           ├── login/LoginScreen.kt
│       │           ├── register/RegisterScreen.kt
│       │           ├── home/HomeScreen.kt
│       │           ├── tasks/
│       │           ├── subjects/
│       │           ├── study/
│       │           └── settings/
│       └── res/
│           ├── drawable/
│           ├── mipmap-anydpi-v26/
│           ├── values/
│           ├── values-night/
│           └── xml/
├── gradle/libs.versions.toml
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
└── README.md
```

---

## Architecture

StudyFlow follows a **lightweight MVVM + Repository** pattern, all in Kotlin.

```
┌──────────────────────────┐
│      Compose UI          │  Screens, sheets, dialogs
│  (TasksScreen, etc.)     │  Pure declarative UI
└────────────┬─────────────┘
             │ collects StateFlow
             ▼
┌──────────────────────────┐
│       ViewModels         │  TaskViewModel, SubjectViewModel,
│  (AndroidViewModel)      │  StudySessionViewModel, SettingsViewModel
└────────────┬─────────────┘
             │ calls suspend functions
             ▼
┌──────────────────────────┐
│      Repositories        │  AuthRepository, TaskRepository,
│                          │  SubjectRepository, StudySessionRepository
└────────────┬─────────────┘
             │ Room DAO calls
             ▼
┌──────────────────────────┐
│       Room / SQLite      │  AppDatabase ("studyflow.db")
│    + SharedPreferences   │  SessionManager (fast auth check)
└──────────────────────────┘
```

**Key patterns used:**

- **Single source of truth:** SQLite via Room.
- **Flow-based observation:** DAO returns `Flow<List<T>>`; ViewModels expose `StateFlow`.
- **Session-scoped data:** every query filters by `userId`, so multiple accounts
  on the same device don't leak data.
- **Manual DI:** `StudyFlowApp` holds lazy singletons for the database, session
  manager, and repositories. ViewModels get them via `AndroidViewModel(app)`.

---

## Database Schema

Database: `studyflow.db` (Room v3)

### `users`
| Column | Type | Notes |
|---|---|---|
| `id` | Long (PK) | autoGenerate |
| `email` | String (unique index) | normalised to lowercase |
| `displayName` | String? | optional |
| `passwordHash` | String | SHA-256 of password + salt |
| `salt` | String | per-user random |
| `createdAt` | Long | epoch millis |
| `preferredLanguage` | String | "en" / "zu" / "st" |
| `dailyStudyGoalMinutes` | Int | 30 / 60 / 90 / 120 |
| `theme` | String | "dark" / "light" |

### `session`
| Column | Type | Notes |
|---|---|---|
| `id` | Int (PK) | always 1 — single active session |
| `userId` | Long | FK → users.id |
| `email` | String | for display |
| `loggedInAt` | Long | epoch millis |

### `tasks`
| Column | Type | Notes |
|---|---|---|
| `id` | Long (PK) | autoGenerate |
| `userId` | Long | FK → users.id (CASCADE) |
| `title` | String | required |
| `description` | String? | optional |
| `subject` | String? | free text (will become FK later) |
| `dueDate` | Long? | epoch millis |
| `priority` | TaskPriority | LOW / MEDIUM / HIGH |
| `isCompleted` | Boolean | |
| `completedAt` | Long? | |
| `createdAt`, `updatedAt` | Long | |
| `syncStatus` | SyncStatus | SYNCED / PENDING / FAILED |

### `subjects`
| Column | Type | Notes |
|---|---|---|
| `id` | Long (PK) | autoGenerate |
| `userId` | Long | FK → users.id (CASCADE) |
| `name` | String | required |
| `code` | String? | e.g. "CS101" |
| `colourHex` | String | "#RRGGBB" |
| `lecturer` | String? | |
| `createdAt` | Long | |

### `study_sessions`
| Column | Type | Notes |
|---|---|---|
| `id` | Long (PK) | autoGenerate |
| `userId` | Long | FK → users.id (CASCADE) |
| `subjectId` | Long? | FK → subjects.id (SET NULL) |
| `startedAt` | Long | epoch millis |
| `durationMinutes` | Int | |
| `notes` | String? | |

---

## Getting Started

### Prerequisites

- **Android Studio** Hedgehog (2023.1.1) or newer
- **JDK 17** (bundled with recent Android Studio)
- **Android SDK 34**
- A physical Android device (recommended) or an emulator running **API 26+**

### Import

1. Open **Android Studio**
2. **File → Open** and select the `StudyFlow/` project folder
3. Wait for the initial **Gradle Sync** to complete
4. Accept any SDK download prompts

---

## Building & Running

### Debug build

```bash
./gradlew :app:assembleDebug
```

Install on a connected device:

```bash
./gradlew :app:installDebug
```

Or from Android Studio: click the green **Run ▶** button.

### Release build

```bash
./gradlew :app:assembleRelease
```

Output APK:
```
app/build/outputs/apk/release/app-release-unsigned.apk
```

> Release signing is not configured yet.

### Clean build

```bash
./gradlew clean :app:assembleDebug
```

---

## Testing the App

### First-time flow

1. **Launch** → splash plays (~1.6 s)
2. **Register** → enter any email + a password ≥ 6 chars
3. **Auto-login** → lands on Home

### Manual test checklist

| Area | Steps |
|---|---|
| **Auth** | Register → Log out → Log back in. Force-close and reopen — should auto-login. |
| **Tasks** | Add 3 tasks with different priorities. Toggle one complete. Filter to **Done**. Delete one. |
| **Subjects** | Add 2 subjects with different colours. Tap to edit. Delete one. |
| **Study** | Log a 25 min session. Verify **Today** counter updates. Delete the session. |
| **Settings** | Change daily goal to 90, language to isiZulu, save. |
| **Persistence** | Force-close app and reopen — all data must still be there. |

### Unit tests

```bash
./gradlew :app:testDebugUnitTest
```

### Instrumented tests

```bash
./gradlew :app:connectedDebugAndroidTest
```

---

## Version Compatibility

StudyFlow uses a stable, known-good combination. **Do not upgrade any of
these in isolation** — they must move together.

| Component | Version | Notes |
|---|---|---|
| Android Gradle Plugin | 8.5.2 | |
| Gradle | 8.7 (wrapper) | Must be 8.x for AGP 8.5 |
| Kotlin | 2.0.21 | Compatible with AGP 8.5 |
| KSP | 2.0.21-1.0.28 | Must match Kotlin version exactly |
| Compose BOM | 2024.09.00 | |
| Room | 2.6.1 | |
| Min SDK | 26 | |
| Compile / Target SDK | 34 | |
| JVM target | 17 | |

### Common pitfalls

- **Do NOT accept the "Upgrade to Gradle 9.x"** recommendation from Android Studio.
  AGP 8.5.2 does not support Gradle 9.x.
- **Do NOT use kapt** — StudyFlow uses **KSP** for Room's annotation processing.
  If you see `kaptDebugKotlin` in the build log, you've regressed.
- **Do NOT change the KSP version** independently of the Kotlin version — they
  must always match.

---

## Roadmap

- [ ] **Task ↔ Subject foreign key linking** (replace free-text `subject` on `TaskEntity`)
- [ ] **Notifications** for approaching deadlines (WorkManager + AlarmManager)
- [ ] **Gamification** — points, streaks, achievements screen
- [ ] **Weekly dashboard charts** (study minutes per day)
- [ ] **Full multilingual resources** (`values-zu/strings.xml`, `values-st/strings.xml`)
- [ ] **Google Single Sign-On** (final PoE version)
- [ ] **REST API integration** — Node.js + Express on Render, MongoDB Atlas, Retrofit + offline sync
- [ ] **Firebase Cloud Messaging** for remote push notifications
- [ ] **Cloud study resources** (Firebase Cloud Storage for PDF/image attachments)
- [ ] **Dark/Light theme switching** at runtime
- [ ] **Data export/import** (JSON backup of tasks and sessions)

---

## Known Limitations

- Passwords use salted SHA-256 for the prototype; production should use
  **bcrypt** or **Argon2**.
- `fallbackToDestructiveMigration()` is enabled — schema changes wipe the DB.
- Language and theme preferences are stored per-user but **not yet applied**
  to the UI at runtime.
- Task `subject` is a free-text string; the Subjects module exists but is not
  yet wired as a foreign key from tasks.
- Only one active session at a time (single-row `session` table).
- No network layer yet — the REST API integration is on the roadmap.

---
