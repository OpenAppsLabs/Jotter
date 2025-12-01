# 📝 Jotter

**Simple. Secure. Notes.**

Jotter is a modern, open-source Android note-taking application built with Jetpack Compose and Material Design 3. It focuses on speed, simplicity, and privacy, offering an offline-first experience with a beautiful, dynamic UI.

---

## 📥 Download

Get the latest version of **Jotter**:

[![Get it on GitHub](https://img.shields.io/badge/Get%20it%20on-GitHub-000000?style=for-the-badge&logo=github&logoColor=white)](https://github.com/OpenAppsLabs/Jotter/releases/latest)
[![Download APK](https://img.shields.io/badge/Download-APK-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://github.com/OpenAppsLabs/Jotter/releases/latest/download/app-release.apk)

---

## ✨ Features

* **Material You Design:** Fully compatible with Material 3 dynamic theming.  
* **Rich Note Taking:** Create and edit notes seamlessly.  
* **Offline First:** All data is stored locally using Room Database.  
* **Dark Mode & True Dark Mode:** Fully optimized dark theme support.  
* **Lock Notes:** Secure individual notes with a PIN or pattern.  
* **Import & Export Notes:** Backup and restore your notes easily.  
* **Dynamic Colors:** App adapts to your system colors.  
* **Trash & Archive:** Organize your notes without losing data.  
* **Haptics Feedback:** Subtle feedback for interactions.  
* **Multiple View Modes:** List, grid, or compact views for your notes.  

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

---

## 🏗️ Architecture

This app follows the recommended MVVM structure:

* **UI Layer:** Composable functions observing `StateFlow` from ViewModels (utilizing `lifecycle-runtime-compose`).  
* **Domain/Data Layer:** Repositories mediating between the UI and local data sources (Room/DataStore).  
* **Single Activity:** Uses a single `MainActivity` with Compose Navigation to handle screens.  

---

## 🤝 Contributing

We welcome contributions! To keep the project organized and maintainable, please follow these guidelines:

1. **Fork the repository** and create a branch
2. **Work on a single feature or fix per branch/PR.** Avoid mixing multiple features or unrelated changes in one Pull Request.
3. **Write clear commit messages**
4. **Ensure code quality:** Follow Kotlin coding conventions and Compose best practices.
5. **Test your changes locally** before submitting.
6. **Open a Pull Request** against the main branch of this repository. Include a description of your changes and any relevant screenshots or notes.

## 🐛 Bug Reporting

If you encounter any bugs, issues, or unexpected behavior while using Jotter, please feel free to [open an issue](https://github.com/yourusername/jotter/issues) on this repository.  

When reporting a bug, please include:

- Steps to reproduce the issue
- Expected behavior
- Actual behavior
- Device/Android version
- Screenshots (if applicable)

This helps me fix problems faster and improve the app for everyone.
I welcome all constructive feedback and suggestions.
This app is still in progress.

If you find Jotter useful, please consider ⭐ starring the repository to help others discover it.

## 📄 License

Jotter is licensed under the [GNU GPL v3.0](https://www.gnu.org/licenses/gpl-3.0.en.html).

## Stargazers over time
[![Stargazers over time](https://starchart.cc/OpenAppsLabs/Jotter.svg?variant=adaptive)](https://starchart.cc/OpenAppsLabs/Jotter)
