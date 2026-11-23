package com.iris.raahi.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.PreviewView
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.iris.raahi.BuildConfig
import com.iris.raahi.RAAHIApplication
import com.presagetech.smartspectra.ProcessingStatus
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(application: RAAHIApplication, onBack: () -> Unit) {
    var settings by remember { mutableStateOf(application.settingsRepository.get()) }
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("Settings", style = MaterialTheme.typography.headlineLarge)

        Text("Speech rate: ${String.format("%.1f", settings.speechRate)}")
        Slider(
            value = settings.speechRate,
            onValueChange = { settings = settings.copy(speechRate = it) },
            valueRange = 0.5f..2f
        )
        Text("Speech volume: ${String.format("%.0f%%", settings.speechVolume * 100)}")
        Slider(
            value = settings.speechVolume,
            onValueChange = { settings = settings.copy(speechVolume = it) },
            valueRange = 0f..1f
        )
        Text("Text size: ${String.format("%.1f", settings.textScale)}")
        Slider(
            value = settings.textScale,
            onValueChange = { settings = settings.copy(textScale = it) },
            valueRange = 0.8f..1.6f
        )

        OutlinedTextField(
            value = settings.language,
            onValueChange = { settings = settings.copy(language = it) },
            label = { Text("Language foundation") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = settings.emergencyContact,
            onValueChange = { settings = settings.copy(emergencyContact = it) },
            label = { Text("Emergency phone or SMS number") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("High contrast", modifier = Modifier.weight(1f))
            Switch(checked = settings.highContrast, onCheckedChange = { settings = settings.copy(highContrast = it) })
        }
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("Vibration feedback", modifier = Modifier.weight(1f))
            Switch(checked = settings.vibration, onCheckedChange = { settings = settings.copy(vibration = it) })
        }

        Text("Privacy", style = MaterialTheme.typography.titleLarge)
        Text("Camera frames are analyzed only while a feature is active. RAAHI does not store frames or location history. External map, SMS, call, and sharing apps are opened only after your action.")

        Button(
            onClick = {
                application.settingsRepository.update(settings)
                onBack()
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Save Settings") }
        FilledTonalButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("Back") }
    }
}

@Composable
fun SmartSpectraScreen(application: RAAHIApplication, onBack: () -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val sdk = application.smartSpectraSdk
    var consent by remember { mutableStateOf(false) }
    var status by remember {
        mutableStateOf(
            application.smartSpectraInitializationError?.let { "Initialization failed" }
                ?: if (sdk == null) "SDK unavailable" else "Idle"
        )
    }
    var sdkStatus by remember {
        mutableStateOf(
            application.smartSpectraInitializationError?.let { "Failed: $it" }
                ?: if (sdk != null) "Initialized (SDK ${sdk.version})" else "Unavailable"
        )
    }
    var authenticationStatus by remember {
        mutableStateOf(
            if (BuildConfig.SMARTSPECTRA_API_KEY.isBlank()) "API key missing"
            else "Configured; authentication occurs when measurement starts"
        )
    }
    var validation by remember { mutableStateOf("Position your face in the preview.") }
    var error by remember { mutableStateOf<String?>(null) }
    var pulse by remember { mutableStateOf<Double?>(null) }
    var breathing by remember { mutableStateOf<Double?>(null) }
    var hrv by remember { mutableStateOf<Double?>(null) }
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
        )
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
        if (!granted) {
            error = "Camera permission is required for measurement."
        } else if (sdk != null) {
            lifecycleOwner.lifecycleScope.launch {
                pulse = null
                breathing = null
                hrv = null
                authenticationStatus = "Authenticating..."
                status = "Starting"
                runCatching { sdk.start() }
                    .onSuccess {
                        // Authentication success is reflected by the SDK processing status.
                    }
                    .onFailure { failure ->
                        authenticationStatus = "Authentication or start failed"
                        error = failure.message ?: "Measurement could not start."
                    }
            }
        }
    }

    DisposableEffect(sdk, lifecycleOwner) {
        if (sdk == null) {
            return@DisposableEffect onDispose {}
        }
        val statusObserver = androidx.lifecycle.Observer<ProcessingStatus> {
            status = it.name.lowercase().replace('_', ' ').replaceFirstChar(Char::uppercase)
            when (it) {
                ProcessingStatus.RUNNING -> authenticationStatus = "Authenticated"
                ProcessingStatus.STARTING -> authenticationStatus = "Authenticating..."
                ProcessingStatus.IDLE -> {
                    if (authenticationStatus == "Authenticating...") {
                        authenticationStatus = "Not started"
                    }
                    pulse = null
                    breathing = null
                    hrv = null
                }
                ProcessingStatus.ERROR -> authenticationStatus = "Authentication or processing failed"
                ProcessingStatus.STOPPING -> Unit
            }
        }
        val validationObserver = androidx.lifecycle.Observer<com.presagetech.smartspectra.ValidationStatus?> {
            validation = it?.hint ?: it?.code?.name?.replace('_', ' ') ?: "Waiting for camera validation."
        }
        val errorObserver = androidx.lifecycle.Observer<com.presagetech.smartspectra.SmartSpectraError?> {
            error = it?.let { sdkError ->
                when (sdkError.code) {
                    com.presagetech.smartspectra.SmartSpectraError.Code.AUTHENTICATION_FAILED ->
                        authenticationStatus = "Authentication failed"
                    com.presagetech.smartspectra.SmartSpectraError.Code.NETWORK_ERROR ->
                        authenticationStatus = "Authentication network unavailable"
                    else -> Unit
                }
                "${sdkError.code}: ${sdkError.message}"
            }
        }
        val metricsObserver = androidx.lifecycle.Observer<com.presagetech.smartspectra.proto.MetricsProto.Metrics?> {
            it?.cardio?.pulseRateList?.lastOrNull()?.value?.toDouble()?.let { value -> pulse = value }
            it?.breathing?.rateList?.lastOrNull()?.value?.toDouble()?.let { value -> breathing = value }
            it?.cardio?.hrvList?.lastOrNull()?.rmssd?.let { value -> hrv = value }
        }
        sdk.processingStatus.observe(lifecycleOwner, statusObserver)
        sdk.validationStatus.observe(lifecycleOwner, validationObserver)
        sdk.error.observe(lifecycleOwner, errorObserver)
        sdk.metrics.observe(lifecycleOwner, metricsObserver)
        onDispose {
            lifecycleOwner.lifecycleScope.launch {
                if (sdk.processingStatus.value == ProcessingStatus.RUNNING ||
                    sdk.processingStatus.value == ProcessingStatus.STARTING
                ) {
                    runCatching { sdk.stop() }
                }
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("SmartSpectra", style = MaterialTheme.typography.headlineLarge)
        Text(
            "This module is separate from navigation and obstacle detection. " +
                "It does not provide navigation sensing or a medical diagnosis."
        )
        Text(
            "Presage SmartSpectra analyzes live front-camera frames on-device. " +
                "The API key is used only to authenticate the SDK subscription."
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            androidx.compose.material3.Checkbox(checked = consent, onCheckedChange = { consent = it })
            Text("I consent to camera-based wellbeing measurement.")
        }
        AndroidView(
            factory = { PreviewView(it) },
            modifier = Modifier.fillMaxWidth().height(260.dp),
            update = { preview ->
                sdk?.config?.previewSurfaceProvider = preview.surfaceProvider
            }
        )
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("SDK: $sdkStatus")
                Text("Authentication: $authenticationStatus")
                Text("Status: $status")
                Text("Validation: $validation")
                Text("Pulse: ${pulse?.let { "%.0f bpm".format(it) } ?: "--"}")
                Text("Breathing: ${breathing?.let { "%.0f bpm".format(it) } ?: "--"}")
                Text("HRV RMSSD: ${hrv?.let { "%.1f ms".format(it) } ?: "--"}")
                error?.let { Text("Error: $it", color = MaterialTheme.colorScheme.error) }
            }
        }
        FilledTonalButton(
            enabled = sdk != null &&
                consent &&
                status != "Starting" &&
                status != "Stopping",
            onClick = {
                error = null
                if (BuildConfig.SMARTSPECTRA_API_KEY.isBlank()) {
                    error = "No SmartSpectra API key configured. See README setup instructions."
                } else if (!hasPermission) {
                    permissionLauncher.launch(Manifest.permission.CAMERA)
                } else if (sdk?.processingStatus?.value == ProcessingStatus.RUNNING) {
                    lifecycleOwner.lifecycleScope.launch { runCatching { sdk.stop() } }
                } else if (sdk != null) {
                    lifecycleOwner.lifecycleScope.launch {
                        pulse = null
                        breathing = null
                        hrv = null
                        authenticationStatus = "Authenticating..."
                        runCatching { sdk.start() }
                            .onFailure { failure ->
                                authenticationStatus = "Authentication or start failed"
                                error = failure.message ?: "Measurement could not start."
                            }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text(if (status == "Running") "Stop measurement" else "Start measurement") }
        Text("Only values emitted by the SDK are shown. No values are estimated or fabricated.")
        FilledTonalButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("Back") }
    }
}
