# Gemini Android

A lightweight, high-performance Android wrapper for Google Gemini, built with modern Jetpack Compose.

## 🚀 Features

- **Native Feel**: Custom CSS injection eliminates web-style selection highlights and "tap-to-zoom" glitches for a solid, native-app touch response.
- **Optimized Performance**: Hardware acceleration is forced on for the WebView layer, ensuring smooth 60fps scrolling through long conversations.
- **Graceful Error Handling**: Includes a custom native "No Connection" screen with an intelligent retry mechanism. The error overlay stays active until the page is fully reloaded, preventing UI "flicker".
- **Persistent Sessions**: Optimized Cookie and DOM storage management ensures you stay logged into your Google account even after closing the app.
- **Premium Aesthetic**: 
  - Fully immersive edge-to-edge layout.
  - System status and navigation bars are color-matched to a deep black.
  - High-end typography and layout spacing.
- **Ultra-Lightweight**: 
  - ProGuard/R8 enabled for deep code minification.
  - Resource filtering limits the APK to essential assets only.
- **Dark Mode Sync**: Automatically detects and applies system-wide dark mode settings to the Gemini interface.

## 🛠 Tech Stack

- **Framework**: Jetpack Compose
- **Language**: Kotlin
- **WebView Architecture**: Hybrid bridge with lifecycle-aware state management.
- **Build System**: Gradle (Kotlin DSL)
- **Minimum Requirements**: Android 6.0 (API 23+)

## 📦 How to Build

1. Clone the repository.
2. Open in Android Studio (Ladybug or newer recommended).
3. Connect your device or emulator.
4. Click `Run` or execute `./gradlew assembleRelease`.

## 📜 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---
*Disclaimer: This is an unofficial wrapper app for Google Gemini. All rights to the Gemini service belong to Google.*
