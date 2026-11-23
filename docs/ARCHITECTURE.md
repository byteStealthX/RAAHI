# RAAHI Architecture

## Layers

- `navigation`: Compose destinations and route-building helpers
- `screens`: user-facing Compose UI
- `camera`: CameraX preview lifecycle
- `detection`: obstacle-detector interface and placeholder implementation
- `ocr`: ML Kit integration through the OCR camera screen
- `location`: Play Services location access
- `voice`: TTS and SpeechRecognizer platform integration
- `emergency`: explicit Android call, SMS, and sharing intents
- `settings`: SharedPreferences-backed repository
- `smartspectra`: isolated UI and future SDK boundary
- `theme`: accessible navy and blue Material 3 design
- `permissions`: permission result interpretation

## State and lifecycle

Compose screens own short-lived screen state. `SettingsRepository` is the persistent preference boundary. Camera and voice resources are disposed with Compose lifecycle effects. Camera frames are backpressured and OCR closes every `ImageProxy` via `addOnCompleteListener`.

## Safety boundaries

No module presents a detected obstacle or route instruction unless it comes from a real trained model or legitimate map service. Emergency actions open Android surfaces and do not initiate calls or messages without confirmation.
