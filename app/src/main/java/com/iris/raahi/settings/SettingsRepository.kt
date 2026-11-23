package com.iris.raahi.settings

import android.content.Context

data class AppSettings(
    val speechRate: Float = 1f,
    val speechVolume: Float = 1f,
    val textScale: Float = 1f,
    val highContrast: Boolean = false,
    val vibration: Boolean = true,
    val language: String = "English",
    val emergencyContact: String = "",
    val onboardingComplete: Boolean = false
)

class SettingsRepository(context: Context) {
    private val prefs = context.getSharedPreferences("raahi_settings", Context.MODE_PRIVATE)

    fun get(): AppSettings = AppSettings(
        speechRate = prefs.getFloat("speech_rate", 1f),
        speechVolume = prefs.getFloat("speech_volume", 1f),
        textScale = prefs.getFloat("text_scale", 1f),
        highContrast = prefs.getBoolean("high_contrast", false),
        vibration = prefs.getBoolean("vibration", true),
        language = prefs.getString("language", "English") ?: "English",
        emergencyContact = prefs.getString("emergency_contact", "") ?: "",
        onboardingComplete = prefs.getBoolean("onboarding_complete", false)
    )

    fun update(settings: AppSettings) {
        prefs.edit()
            .putFloat("speech_rate", settings.speechRate)
            .putFloat("speech_volume", settings.speechVolume)
            .putFloat("text_scale", settings.textScale)
            .putBoolean("high_contrast", settings.highContrast)
            .putBoolean("vibration", settings.vibration)
            .putString("language", settings.language)
            .putString("emergency_contact", settings.emergencyContact)
            .putBoolean("onboarding_complete", settings.onboardingComplete)
            .apply()
    }
}
