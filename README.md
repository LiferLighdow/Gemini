<p align="center">
  <img src="app/src/main/ic_launcher-playstore.png" width="128" height="128" />
</p>

# <p align="center">Gemini</p>

<p align="center">
  <a href="https://android-arsenal.com/api?level=23"><img src="https://img.shields.io/badge/API-23%2B-brightgreen.svg?style=flat" alt="Android API" /></a>
  <a href="https://opensource.org/licenses/MIT"><img src="https://img.shields.io/badge/License-MIT-yellow.svg" alt="License: MIT" /></a>
  <a href="https://www.android.com/"><img src="https://img.shields.io/badge/Platform-Android-blue.svg" alt="Platform" /></a>
</p>

A premium, high-performance Android wrapper for **Google Gemini**, meticulously engineered with **Jetpack Compose**. This project aims to bridge the gap between web and native by providing a fluid, distraction-free AI assistant experience.

---

## 💎 Engineering Highlights

### ⚡ Native-Grade Interaction
Most WebView apps feel like "websites in a box." This project fixes that through:
*   **Touch Optimization**: Custom CSS injection layers that eliminate web-typical "blue selection highlights" and tap-to-zoom glitches, providing immediate touch response.
*   **Hardware Acceleration**: Explicitly forced GPU rendering for the WebView layer, ensuring 60fps scrolling through long conversations.
*   **Edge-to-Edge Immersion**: Fully utilizes the modern Android windowing system, extending content behind system bars for a seamless "infinity screen" feel.

### 🛡️ Resilience & Persistence
*   **Stateful Error Handling**: Features a native "No Connection" interceptor. Unlike standard browsers that show a white screen on failure, this app presents a professional native recovery interface with intelligent retry logic.
*   **Persistent Sessions**: Implements aggressive Cookie & DOM Storage synchronization. By forcing memory-to-disk "flushing," your login state remains intact even after deep system hibernation or app kills.

### 🎨 Premium Aesthetic
*   **System-Wide Dark Mode**: Automatic theme detection that synchronizes the WebView rendering with your Android system settings.
*   **Ultra-Lightweight Footprint**: Optimized with **R8/ProGuard** and binary resource filtering. Only essential code and English assets are bundled, resulting in a minimal APK size without sacrificing functionality.

---

## 🛠 Tech Stack

- **UI Framework**: Jetpack Compose (Modern Declarative UI)
- **Language**: Kotlin 2.0.21
- **Architecture**: Single-Activity, Life-cycle aware Hybrid Bridge
- **Build System**: Gradle Kotlin DSL
- **Compatibility**: Android 6.0 (API 23) and above

---

## 🚀 Getting Started

### Prerequisites
*   Android Studio Ladybug (or newer)
*   Android SDK 35/36 installed

### Build Instructions
1.  Clone the repository:
    ```bash
    git clone https://github.com/LiferLighdow/Gemini.git
    ```
2.  Open the project in Android Studio.
3.  Ensure your device has the latest **Android System WebView** updated via Play Store for the best CSS support.
4.  Run the `:app:assembleRelease` task to generate a minified APK.

---

## 📜 License

Distributed under the MIT License. See `LICENSE` for more information.

---

## ⚖️ Disclaimer

*This is an unofficial wrapper application. All rights to the Gemini service, branding, and assets belong to Google. This project is intended for educational and personal use only.*

---
<p align="center">
  Developed with ❤️ by Lifer_Lighdow
</p>
