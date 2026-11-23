package com.iris.raahi

import android.app.Application
import com.iris.raahi.settings.SettingsRepository
import com.presagetech.smartspectra.CameraSelection
import com.presagetech.smartspectra.SmartSpectraConfig
import com.presagetech.smartspectra.SmartSpectraSdk

class RAAHIApplication : Application() {
    lateinit var settingsRepository: SettingsRepository
        private set
    var smartSpectraSdk: SmartSpectraSdk? = null
        private set
    var smartSpectraInitializationError: String? = null
        private set

    override fun onCreate() {
        super.onCreate()
        settingsRepository = SettingsRepository(this)
        smartSpectraInitializationError = null
        runCatching {
            val config = SmartSpectraConfig().apply {
                apiKey = BuildConfig.SMARTSPECTRA_API_KEY.takeIf { it.isNotBlank() }
                imageOutputEnabled = false
                requestedMetrics =
                    SmartSpectraConfig.breathingMetrics + SmartSpectraConfig.cardioMetrics
            }
            SmartSpectraSdk.initialize(this, config).also { sdk ->
                sdk.useCamera(CameraSelection.Front)
                smartSpectraSdk = sdk
            }
        }.onFailure { failure ->
            smartSpectraInitializationError = failure.message ?: "SDK initialization failed"
        }
    }
}
