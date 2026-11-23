# RAAHI

RAAHI is an accessible Android navigation assistant by Team IRIS. It combines camera preview, optional OCR, voice commands, location foundations, emergency assistance, settings, and a clearly isolated SmartSpectra wellbeing integration boundary.

## Features

- Accessible onboarding with explicit camera, microphone, and location consent
- High-contrast Material 3 dashboard with large touch targets and TalkBack labels
- Rear-camera preview for obstacle-awareness screens
- ML Kit on-device text recognition with speech playback
- GPS location status and Google Maps walking-route handoff
- Android SpeechRecognizer and TextToSpeech voice controls
- Consent-confirmed emergency call, SMS, and location sharing
- Speech, text-size, contrast, vibration, language, privacy, and emergency settings
- Optional Presage SmartSpectra live pulse, breathing, and HRV measurement

## Technology

- Kotlin
- Jetpack Compose and Material 3
- MVVM-oriented ViewModel and repository boundaries
- Android CameraX
- ML Kit Text Recognition
- Android SpeechRecognizer and TextToSpeech
- Google Play Services Fused Location Provider
- AndroidX Navigation
- Gradle Kotlin DSL

## Build requirements

- Android SDK Platform 36
- Android Build Tools 36.0.0
- Java 17 or newer; the project is verified with Java 25
- Android device or emulator with Play Services for location and ML Kit features

## Build

```bash
export ANDROID_HOME=/home/codespace/android-sdk
export ANDROID_SDK_ROOT=/home/codespace/android-sdk
./gradlew assembleDebug
./gradlew test
```

The debug APK is written to `app/build/outputs/apk/debug/app-debug.apk`.

## Permissions

| Permission | Purpose |
|---|---|
| Camera | CameraX preview, OCR, and future obstacle detection |
| Microphone | SpeechRecognizer voice commands |
| Fine/coarse location | Current-position status and map-route handoff |
| Call phone | UI does not call automatically; Android dialer is opened after confirmation |
| Vibrate | Optional accessible feedback |

## AI model integration

No trained obstacle model is bundled. The app does not fabricate detections. `ObstacleDetector` in `com.iris.raahi.detection` is the integration boundary, and `PlaceholderObstacleDetector` documents that detections are unavailable.

To integrate a verified model:

1. Add the licensed TFLite or LiteRT model under `app/src/main/assets/`.
2. Add a pinned interpreter dependency only after checking compatibility.
3. Implement `ObstacleDetector` with live `ImageProxy` processing.
4. Add calibrated and tested distance estimation only if hardware and validation support it.
5. Close every `ImageProxy` on every execution path.
6. Avoid UI-thread inference and throttle frame frequency.

## SmartSpectra / Presage

The wellbeing module is isolated from navigation, OCR, and obstacle detection. It uses the official
Presage SmartSpectra Android SDK (`com.presagetech:smartspectra:3.5.0`) with SDK-owned front-camera
capture. The screen requests explicit consent and Android camera permission before starting.

### Setup

SmartSpectra requires Android `minSdk 28`, compile SDK `36.1`, AGP 9.x, and Kotlin 2.2.x.
Register at the [Presage developer portal](https://physiology.presagetech.com/auth/register), then
provide the key only at build time:

```bash
# Add SMARTSPECTRA_API_KEY=your-key to the root local.properties file.
./gradlew assembleDebug
```

Gradle reads `SMARTSPECTRA_API_KEY` from the root `local.properties` file and embeds it only in
that build's `BuildConfig`; `local.properties` is ignored by Git and must never be committed.
An API key embedded in an APK can be extracted, so this is for development and internal testing
only. Do not treat it as production-secret storage; use the documented Presage OAuth flow for
production.

Open **SmartSpectra Wellbeing**, accept the consent text, grant camera access, and start a
measurement while facing the front camera in good lighting. The UI displays SDK status,
validation guidance, and only SDK-emitted values. A network connection is required for SDK key
and subscription authentication, while metric computation runs on-device.

Currently requested live signals are pulse rate, breathing rate, arterial-pressure waveform, and
HRV RMSSD. This repository has not demonstrated a real device measurement in this environment:
those signals require a valid Presage API key/subscription and a physical Android camera device.
Until both are supplied, the app intentionally shows an authentication/setup error rather than
inventing readings. SmartSpectra metrics are for wellness/informational use, not diagnosis.

## Known limitations

- Obstacle detection requires a trained model that is not currently provided.
- OCR accuracy depends on camera quality, lighting, and ML Kit recognition quality.
- Route instructions are not invented; RAAHI hands route data to Google Maps or another installed map application.
- Location accuracy depends on device, permissions, GPS, network, and Play Services.
- Voice recognition depends on device support and service availability.
- Camera permission is required at runtime on Android 28+.

## Privacy

RAAHI does not store camera frames or location history. Location sharing and emergency actions require explicit in-app consent and hand off to the Android system or a selected app. See `docs/PRIVACY.md`.
