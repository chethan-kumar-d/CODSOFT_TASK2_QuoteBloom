# QuoteBloom

**QuoteBloom** is a polished, editorial **Quote of the Day** application built for Android Development Internship Task 2. It provides an offline-first anthology of 65 accurately attributed public-domain and original reflections across five themes (**Motivation**, **Learning**, **Confidence**, **Kindness**, and **Life**), paired with a warm editorial visual identity.

---

## Architecture & Environment Note

This repository contains two synchronized deliverables so you can inspect, test, and package QuoteBloom in both native Android and web/Capacitor environments:

1. **Native Android Application (Primary AI Studio Build Target)**
   - Located in `/app` (`Kotlin` + `Jetpack Compose` + `Material 3`).
   - Powers the live Streaming Android Emulator in Google AI Studio and builds directly into an installable Android APK/AAB via Gradle.
2. **React + TypeScript + Vite + Capacitor Companion Web & Hybrid Reference**
   - Configured via `package.json`, `vite.config.ts`, `capacitor.config.ts`, and `/src` for browser deployment (including GitHub Pages subpath hosting) and Capacitor Android packaging.
   - Note: In this cloud Android container, Gradle and JVM/Robolectric tests are executed directly; no claim is made that a Capacitor-wrapped APK was built separately from the native Gradle build.

---

## Core Features

- **Today (Daily Quote & Rollover)**:
  - Displays the inspiring Quote of the Day, author attribution, contextual note, category badge, and formatted local calendar date.
  - Deterministically selects the daily quote by local calendar date (`YYYY-MM-DD`) so it remains consistent throughout the same day and automatically rolls over when the local date changes—including when the app returns to the foreground (`ON_RESUME` / `visibilitychange`).
- **Discover Another Quote (Non-Destructive)**:
  - Cycles through fresh quotes without immediate repeats and without replacing Today's pinned Quote of the Day. Includes a one-tap **Back to Today's Quote** action.
- **Native Sharing & Clipboard Fallback**:
  - Uses the native Android share sheet (`Intent.createChooser` / `@capacitor/share`) and provides a one-tap copy-to-clipboard action and automatic clipboard fallback.
- **Explore Collection**:
  - Real-time search across quote text and author names, combined with interactive category filter chips (`All`, `Motivation`, `Learning`, `Confidence`, `Kindness`, `Life`).
- **Saved Favourites**:
  - Save or remove favourites from any screen, search within saved favourites, and view a helpful editorial empty state with a direct call-to-action when no quotes are saved yet.
- **Editorial Appearance & Accessibility**:
  - Switch between **Warm Ivory (Light)**, **Deep Plum (Dark)**, and **System Default** themes.
  - Choose from **six curated high-contrast quote-card backgrounds**: *Warm Ivory*, *Deep Plum*, *Botanical Sage*, *Royal Violet*, *Terracotta Dawn*, and *Midnight Botanica*.
  - Includes a **Reduced Motion** toggle, minimum `48dp` touch targets, semantic labels, and adaptive mobile bottom navigation / tablet-desktop navigation rail layouts.
- **Resilient Local-Only Storage**:
  - Persists favourites, theme mode, selected quote-card background, reduced-motion preference, and daily quote state locally (`SharedPreferences` on Android / `localStorage` on Web).
  - Gracefully recovers from malformed JSON or unavailable storage using an in-memory fallback. Requires **no login, no backend, no ads, and no API keys**.

---

## Local Setup & Commands

### 1. Native Android (Gradle / Android Studio)

**Prerequisites:**
- JDK 17+ (or Android Studio Ladybug / Meerkat bundled JBR)
- Android SDK Platform 36

**Commands:**
```bash
# Run unit and Robolectric UI/logic validation tests
gradle :app:testDebugUnitTest

# Assemble the Debug APK
gradle :app:assembleDebug
```
The compiled debug APK is generated at `app/build/outputs/apk/debug/app-debug.apk`.

### 2. Browser Version & GitHub Pages Deployment (React + Vite)

**Prerequisites:**
- Node.js 20+ and npm 10+

**Commands:**
```bash
# Install dependencies
npm install

# Start local development server
npm run dev

# Run TypeScript typecheck and production web build (configured with base: './' for GitHub Pages subpath compatibility)
npm run build

# Preview production build locally
npm run preview
```

**GitHub Pages Subpath Deployment:**
- `vite.config.ts` uses `base: './'` so all compiled JS, CSS, and font assets resolve relatively under any repository subpath (e.g., `https://<username>.github.io/quotebloom/`) without broken asset paths or refresh routing errors.

### 3. Capacitor Android Packaging Steps (Local Toolchain)

If packaging the web build via Capacitor on a local workstation with Android Studio installed:
```bash
npm install
npm run build
npx cap sync android
npx cap open android
```
From Android Studio, let Gradle sync finish and select **Build > Build Bundle(s) / APK(s) > Build APK(s)** or run on a connected device.
