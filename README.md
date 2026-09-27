<div align="center">
<img width="1200" height="475" alt="GHBanner" src="https://ai.google.dev/static/site-assets/images/share-ais-513315318.png" />
</div>

# Soundify — AI-Powered Music Player

A Kotlin + Jetpack Compose music streaming app with JioSaavn playback, YouTube fallback, and an AI DJ recommendation engine.

View the original AI Studio project: https://ai.studio/apps/5e897af8-bc20-43d2-92d1-0ff3f75f16c8

## Run Locally

**Prerequisites:** [Android Studio](https://developer.android.com/studio)

1. Open Android Studio
2. Select **Open** and choose the directory containing this project
3. Allow Android Studio to fix any incompatibilities as it imports the project.
4. Create a file named `.env` in the project directory and set `GEMINI_API_KEY` in that file to your Gemini API key (see `.env.example` for an example)
5. Run the app on an emulator or physical device

## Installing CI-built APKs

This repository includes a committed `app/debug.keystore` so that every
CI build (GitHub Actions) signs the APK with the **same** certificate.

> [!IMPORTANT]
> **One-time uninstall required:** If you already have this app installed
> on your device from a **different** build source (e.g. a local Android
> Studio build, or a CI build from before the shared `debug.keystore` was
> added), Android will refuse the install with _"App not installed"_ or
> _"Package conflicts with an existing package"_ because the signing
> certificates don't match.
>
> **Fix:** Uninstall the existing app from your device **once**, then
> install the new APK. After that single uninstall, every future CI-built
> APK will share the same signing certificate and install as a normal
> update — no further uninstalls needed.

## Version Numbering

`versionCode` is auto-incremented on CI builds using `github.run_number`.
Local builds default to `versionCode = 1`.
