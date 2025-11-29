# 📝 Jotter

**Simple. Secure. Notes.**

Jotter is a modern, open-source Android note-taking application built with Jetpack Compose and Material Design 3. It focuses on speed, simplicity, and privacy, offering an offline-first experience with a beautiful, dynamic UI.

## ✨ Features

*   **Material You Design:** Fully compatible with Material 3 dynamic theming.
*   **Rich Note Taking:** Create and edit notes seamlessly.
*   **Offline First:** All data is stored locally using Room Database.
*   **Secure:** No cloud tracking or data mining. Your data stays on your device.
*   **Modern Navigation:** Smooth transitions using Jetpack Navigation Compose.

## 🛠️ Tech Stack

Jotter is built using **Modern Android Development (MAD)** standards:

| Category | Library |
| :--- | :--- |
| **Language** | [Kotlin](https://kotlinlang.org/) |
| **UI Toolkit** | [Jetpack Compose](https://developer.android.com/jetpack/compose) |
| **Design System** | [Material 3](https://m3.material.io/) |
| **Icons** | [Material Icons Extended](https://fonts.google.com/icons) |
| **Navigation** | [Navigation Compose](https://developer.android.com/jetpack/compose/navigation) |
| **Dependency Injection** | [Hilt](https://dagger.dev/hilt/) |
| **Local Database** | [Room](https://developer.android.com/training/data-storage/room) (w/ KTX support) |
| **Preferences** | [DataStore](https://developer.android.com/topic/libraries/architecture/datastore) |
| **Architecture** | MVVM (Model-View-ViewModel) |
| **Serialization** | Kotlinx Serialization & Gson |

## 🏗️ Architecture

This app follows the recommended MVVM structure:
*   **UI Layer:** Composable functions observing `StateFlow` from ViewModels (utilizing `lifecycle-runtime-compose`).
*   **Domain/Data Layer:** Repositories mediating between the UI and local data sources (Room/DataStore).
*   **Single Activity:** Uses a single `MainActivity` with Compose Navigation to handle screens.
