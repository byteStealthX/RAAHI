# RAAHI screenshots

Real screenshot capture could not be completed in this environment because no
Android device or emulator was available initially, and the available Linux host
has no KVM hardware acceleration (`/dev/kvm` is unavailable). No placeholder or
rendered mock screenshot has been substituted.

## Route inventory

| Requested screen | Implemented route / composable | Status |
|---|---|---|
| Splash | `onboarding` / `OnboardingScreen` | Implemented in app; not captured |
| Dashboard | `home` / `DashboardScreen` | Implemented; not captured |
| Navigation | `navigation` / `NavigationScreen` | Implemented; not captured |
| Obstacle Detection | `obstacle` / `CameraFeatureScreen(CameraFeature.OBSTACLE)` | Implemented; not captured |
| SmartSpectra | `smartspectra` / `SmartSpectraScreen` | Implemented; real SDK state only; not captured |
| OCR Reader | `ocr` / `CameraFeatureScreen(CameraFeature.OCR)` | Implemented; not captured |
| Voice Assistant | `assistant` / `VoiceAssistantScreen` | Implemented; not captured |
| Surroundings | No dedicated route or composable | Not implemented separately; the dashboard currently opens the obstacle route |
| Emergency SOS | `emergency` / `EmergencyScreen` | Implemented; not captured |
| Settings | `settings` / `SettingsScreen` | Implemented; not captured |

## APK

Build command:

```bash
ANDROID_HOME=/home/codespace/android-sdk ANDROID_SDK_ROOT=/home/codespace/android-sdk ./gradlew assembleDebug
```

APK: `app/build/outputs/apk/debug/app-debug.apk`

## Capture on an Android phone

1. Enable **Developer options** and **USB debugging** on the phone.
2. Connect the phone to this computer and accept the USB debugging prompt.
3. Verify it is visible:

   ```bash
   export ANDROID_HOME=/home/codespace/android-sdk
   export ANDROID_SDK_ROOT=/home/codespace/android-sdk
   $ANDROID_HOME/platform-tools/adb devices -l
   ```

4. Install the debug APK:

   ```bash
   $ANDROID_HOME/platform-tools/adb install -r app/build/outputs/apk/debug/app-debug.apk
   ```

5. Grant requested camera, microphone, and location permissions as desired.
6. Launch RAAHI and navigate through each route:

   - Splash: clear app data first, then launch the app.
   - Dashboard: `adb shell am start -n com.iris.raahi/.MainActivity`
   - Navigation: open **Navigate** from Dashboard.
   - Obstacle Detection: open **Detect Obstacles**.
   - SmartSpectra: open **SmartSpectra Health**.
   - OCR Reader: open **Read Text**.
   - Voice Assistant: tap the microphone or open **Voice Assistant**.
   - Emergency SOS: open **Emergency Help**.
   - Settings: use the Settings button in the Dashboard top bar.

7. Capture each display:

   ```bash
   mkdir -p screenshots
   $ANDROID_HOME/platform-tools/adb exec-out screencap -p > screenshots/dashboard.png
   ```

   Repeat for each screen using descriptive names such as
   `splash.png`, `navigation.png`, `obstacle-detection.png`,
   `smartspectra.png`, `ocr-reader.png`, `voice-assistant.png`,
   `emergency-sos.png`, and `settings.png`.

Do not start SmartSpectra measurement just to create a value: the app must show
only real SDK callback readings.
