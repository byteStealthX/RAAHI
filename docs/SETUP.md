# Setup

1. Install Android SDK Platform 36 and Build Tools 36.0.0.
2. Set `ANDROID_HOME` and `ANDROID_SDK_ROOT` to the SDK directory.
3. Open the repository and run `./gradlew assembleDebug`.
4. Install `app/build/outputs/apk/debug/app-debug.apk` on a device with Android 8.0 or newer.
5. Complete onboarding and review runtime permission prompts.
6. For an emulator, enable location and configure a test location.

No API keys or credentials are required for the current implementation. A network connection may be required for SpeechRecognizer and external map services.
