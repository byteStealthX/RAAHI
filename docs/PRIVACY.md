# Privacy

RAAHI follows a data-minimization model:

- Camera frames are used in memory only while OCR or camera preview is active.
- No camera frame persistence or automatic frame upload is implemented.
- Location is read only when navigation or an explicitly confirmed location-sharing action requests it.
- RAAHI does not maintain location history.
- Voice command text is handled by the Android recognition service and may be governed by the user's SpeechRecognizer provider.
- OCR runs with on-device ML Kit recognition.
- Settings and emergency contact are stored in local app preferences.
- External calls, SMS, map routes, and sharing use Android intents after explicit confirmation.
- SmartSpectra runs only after separate user consent and camera permission. Its metric computation
  runs on-device; the SDK contacts Presage to authenticate the API key and validate the
  subscription. RAAHI does not store camera frames or fabricate measurements. See the README for
  setup and the signals currently requested.
