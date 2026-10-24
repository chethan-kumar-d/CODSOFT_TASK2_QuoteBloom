# QuoteBloom — Quote of the Day

An Android app created for Task 2 of the CodSoft Android Development Internship. QuoteBloom brings together daily inspiration, a searchable quote collection and saved favourites in an elegant interface.

## Features

- Daily quote selected using the device’s local date
- Automatic daily refresh when opening or returning to the app
- Discover additional quotes
- Search quotes by text or author
- Filter by Motivation, Learning, Confidence, Kindness and Life
- Save, view and remove favourite quotes
- Share quotes through Android’s share sheet
- Copy quotes to the clipboard
- Light, dark and system themes
- Customisable quote-card backgrounds
- Local storage for favourites and preferences

## Design

An editorial interface with layered cards, soft shadows, violet and plum accents, and subtle animations.

## Technologies

- Kotlin
- Jetpack Compose
- Material 3
- SharedPreferences
- Gradle

## Daily Quotes

Quotes are bundled with the app and selected according to the local calendar date. The daily quote stays consistent throughout the day.

The collection is finite, so quotes eventually repeat. Daily quotes do not require AI generation or an external quote service.

## Run the Project

1. Download or clone this repository.
2. Open the project’s root folder in Android Studio.
3. Install the Android SDK and JDK versions required by the project’s Gradle configuration.
4. Allow Gradle sync to complete.
5. Select an Android emulator or connected Android phone.
6. Run the app module.

Internet access may be required to download build tools and dependencies during setup.

## Install an APK

If you have a QuoteBloom APK:

1. Download it onto your Android phone.
2. Open the APK and allow installation from that source if prompted.
3. Tap Install, or Update if a compatible version is already installed.
4. Open QuoteBloom from your app list.

APK updates must use the same application ID and signing certificate as the installed version.

## Project Structure

- app/src/main/java/ — Kotlin application source
- app/src/main/res/ — Fonts, icons, themes and other resources
- app/src/main/AndroidManifest.xml — Android application manifest
- app/src/test/ — Local test source
- gradle/ — Gradle configuration files
- build.gradle.kts — Root build configuration
- settings.gradle.kts — Project settings

## Data Storage

Favourites and preferences are stored locally on the device. They are not synchronised between devices. Clearing app data or uninstalling the app may remove them.

## Author

Chethan Kumar D

[GitHub Profile](https://github.com/chethan-kumar-d)
