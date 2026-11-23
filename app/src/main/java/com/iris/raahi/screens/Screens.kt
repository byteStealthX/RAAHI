package com.iris.raahi.screens

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Accessible
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.DirectionsWalk
import androidx.compose.material.icons.rounded.HealthAndSafety
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Sos
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.iris.raahi.RAAHIApplication
import com.iris.raahi.camera.CameraPreview
import com.iris.raahi.navigation.Routes
import com.iris.raahi.settings.AppSettings
import com.iris.raahi.voice.RAAHIVoiceEngine
import com.iris.raahi.voice.VoiceCommand
import com.iris.raahi.voice.VoiceCommandParser
import com.iris.raahi.theme.RaahiBlue
import com.iris.raahi.theme.RaahiCard
import com.iris.raahi.theme.RaahiCyan
import com.iris.raahi.theme.RaahiGreen
import com.iris.raahi.theme.RaahiMuted
import com.iris.raahi.theme.RaahiNavy
import com.iris.raahi.theme.RaahiRed
import com.iris.raahi.theme.RaahiSecondary
import com.iris.raahi.ui.RaahiAnimatedMicrophone
import com.iris.raahi.ui.RaahiFeatureCard
import com.iris.raahi.ui.RaahiGlassCard
import com.iris.raahi.ui.RaahiPrimaryButton
import com.iris.raahi.ui.RaahiSecondaryButton
import com.iris.raahi.ui.RaahiStatusIndicator
import com.iris.raahi.ui.RaahiTopBar
import com.iris.raahi.ui.RaahiSectionHeader
import com.iris.raahi.ui.RaahiVoiceWaveform

enum class CameraFeature { OBSTACLE, OCR }

@Composable
fun OnboardingScreen(application: RAAHIApplication, onComplete: () -> Unit) {
    val context = LocalContext.current
    var consent by remember { mutableStateOf(false) }
    var explanation by remember { mutableStateOf("RAAHI helps you understand surroundings with spoken alerts. Review access before each feature is enabled.") }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val granted = result.values.count { it }
        explanation = if (granted == 0) "Permissions were denied. You can continue and enable them later in Settings."
        else "Permissions granted: $granted. Camera and location will only be used when you start those features."
    }

    Box(
        modifier = Modifier.fillMaxSize().background(
            Brush.radialGradient(listOf(RaahiBlue.copy(alpha = 0.22f), RaahiNavy, RaahiNavy))
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(54.dp))
            Box(
                Modifier.size(104.dp).background(
                    Brush.linearGradient(listOf(RaahiBlue, RaahiCyan)),
                    CircleShape
                ),
                contentAlignment = Alignment.Center
            ) {
                Text("R", style = MaterialTheme.typography.displayMedium, color = Color.White)
            }
            Text("RAAHI", style = MaterialTheme.typography.headlineLarge)
            Text("See More. Navigate Freely.", style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center)
            RaahiGlassCard(Modifier.fillMaxWidth()) {
                Text(
                    "Your accessible navigation companion from Team IRIS.",
                    modifier = Modifier.padding(20.dp),
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Text(
                "RAAHI can use the camera for obstacle guidance and text reading, the microphone for voice commands, and location for navigation.",
                color = RaahiMuted
            )
            Text(explanation, color = RaahiMuted)
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = consent, onCheckedChange = { consent = it })
                Text("I consent to camera, microphone, and location use.")
            }
            RaahiSecondaryButton(
                text = "Grant permissions",
                enabled = consent,
                onClick = {
                    permissionLauncher.launch(
                        arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.ACCESS_FINE_LOCATION)
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )
            RaahiPrimaryButton(
                text = "Continue",
                onClick = {
                    application.settingsRepository.update(application.settingsRepository.get().copy(onboardingComplete = true))
                    onComplete()
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun DashboardScreen(onNavigate: (String) -> Unit, onEmergency: () -> Unit) {
    val cards = listOf(
        Triple("Navigate", "Walking route guidance", Routes.NAVIGATION),
        Triple("Detect Obstacles", "Camera visual awareness", Routes.OBSTACLE),
        Triple("Read Text", "Scan and speak text", Routes.OCR),
        Triple("SmartSpectra Health", "Wellbeing measurement", Routes.SMARTSPECTRA),
        Triple("Describe Surroundings", "Understand the scene", Routes.OBSTACLE),
        Triple("Emergency Help", "Trusted contact support", Routes.EMERGENCY)
    )
    val icons = listOf(
        Icons.Rounded.DirectionsWalk, Icons.Rounded.Visibility, Icons.Rounded.Description,
        Icons.Rounded.HealthAndSafety, Icons.Rounded.CameraAlt, Icons.Rounded.Sos
    )
    val accents = listOf(RaahiCyan, RaahiGreen, RaahiBlue, RaahiRed, Color(0xFF9B7BFF), RaahiRed)

    Box(Modifier.fillMaxSize().background(RaahiNavy)) {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 36.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            RaahiTopBar(title = "RAAHI") {
                IconButton(onClick = { onNavigate(Routes.SETTINGS) }, modifier = Modifier.size(48.dp)) {
                    Icon(Icons.Rounded.Settings, contentDescription = "Open settings")
                }
            }
            Text("Good to see you", color = RaahiMuted, style = MaterialTheme.typography.titleMedium)

            RaahiGlassCard(Modifier.fillMaxWidth()) {
                Column(
                    Modifier.fillMaxWidth().padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    RaahiAnimatedMicrophone(isListening = false, onClick = { onNavigate(Routes.ASSISTANT) })
                    Text("How can I help you today?", style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center)
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxWidth().height(390.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(count = cards.size) { index ->
                    val (title, description, route) = cards[index]
                    RaahiFeatureCard(
                        title = title,
                        description = description,
                        icon = icons[index],
                        accent = accents[index],
                        onClick = { if (route == Routes.EMERGENCY) onEmergency() else onNavigate(route) },
                        modifier = Modifier.height(116.dp)
                    )
                }
            }

            RaahiSectionHeader(title = "Connections", subtitle = "Feature readiness")
            RaahiGlassCard(Modifier.fillMaxWidth()) {
                Row(
                    Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    RaahiStatusIndicator("GPS", false)
                    RaahiStatusIndicator("Camera", false)
                    RaahiStatusIndicator("Voice", true)
                    RaahiStatusIndicator("Health", false)
                }
            }
        }
    }
}

@Composable
fun CameraFeatureScreen(application: RAAHIApplication, feature: CameraFeature, onBack: () -> Unit) {
    val context = LocalContext.current
    var active by remember { mutableStateOf(false) }
    var recognizedText by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("Camera is stopped.") }
    val settings = application.settingsRepository.get()
    val recognizer = remember { TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS) }
    val voice = remember { RAAHIVoiceEngine(context) }

    DisposableEffect(Unit) { onDispose { recognizer.close(); voice.shutdown() } }

    val analyzer = remember(feature) {
        if (feature == CameraFeature.OCR) {
            ImageAnalysis.Analyzer { proxy ->
                val media = proxy.image
                if (media == null) {
                    proxy.close()
                    return@Analyzer
                }
                val image = InputImage.fromMediaImage(media, proxy.imageInfo.rotationDegrees)
                recognizer.process(image)
                    .addOnSuccessListener { result ->
                        val text = result.text.trim()
                        if (text.isNotBlank() && text != recognizedText) {
                            recognizedText = text
                            status = "Text recognized."
                        }
                    }
                    .addOnFailureListener { status = "OCR could not process this frame." }
                    .addOnCompleteListener { proxy.close() }
            }
        } else null
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(if (feature == CameraFeature.OCR) "Read Text" else "Obstacle Detection", style = MaterialTheme.typography.headlineLarge)
        Card(modifier = Modifier.fillMaxWidth().weight(1f)) {
            CameraPreview(modifier = Modifier.fillMaxSize(), active = active, imageAnalyzer = analyzer)
        }
        if (feature == CameraFeature.OBSTACLE) {
            Text(
                "No trained on-device model is connected. Camera preview only. Do not rely on this screen for obstacle avoidance.",
                color = MaterialTheme.colorScheme.error
            )
        } else {
            Text(if (recognizedText.isBlank()) "No text recognized yet." else recognizedText)
        }
        Text(status)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = {
                active = true
                status = "Camera started."
            }, modifier = Modifier.weight(1f).height(56.dp), enabled = !active) { Text("Start Camera") }
            FilledTonalButton(onClick = {
                active = false
                status = "Camera stopped."
                voice.stop()
            }, modifier = Modifier.weight(1f).height(56.dp), enabled = active) { Text("Stop") }
        }
        if (feature == CameraFeature.OCR) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = {
                    if (recognizedText.isBlank()) voice.speak("No text has been recognized.", settings.speechRate, settings.speechVolume)
                    else voice.speak(recognizedText, settings.speechRate, settings.speechVolume)
                }, modifier = Modifier.weight(1f)) { Text("Read aloud") }
                FilledTonalButton(onClick = { voice.stop() }, modifier = Modifier.weight(1f)) { Text("Stop speech") }
            }
        }
        FilledTonalButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("Back") }
    }
}

@Composable
fun NavigationScreen(application: RAAHIApplication, onBack: () -> Unit) {
    val context = LocalContext.current
    var destination by remember { mutableStateOf("") }
    var locationStatus by remember { mutableStateOf("Location status unavailable.") }
    var currentLocation by remember { mutableStateOf<Pair<Double, Double>?>(null) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
        if (result[Manifest.permission.ACCESS_FINE_LOCATION] == true || result[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
            readLocation(context) { lat, lon, accuracy ->
                currentLocation = lat to lon
                locationStatus = "Current location acquired. Accuracy approximately ${accuracy.toInt()} meters."
            }
        } else locationStatus = "Location permission denied. Navigation cannot determine your position."
    }
    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            readLocation(context) { lat, lon, accuracy ->
                currentLocation = lat to lon
                locationStatus = "Current location acquired. Accuracy approximately ${accuracy.toInt()} meters."
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("Navigation", style = MaterialTheme.typography.headlineLarge)
        Text(locationStatus)
        OutlinedTextField(
            value = destination,
            onValueChange = { destination = it },
            label = { Text("Destination") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Button(
            onClick = {
                if (destination.isBlank()) {
                    locationStatus = "Enter a destination before opening maps."
                } else {
                    val data = if (currentLocation != null) {
                        Uri.parse("https://www.google.com/maps/dir/?api=1&origin=${currentLocation!!.first},${currentLocation!!.second}&destination=${Uri.encode(destination)}&travelmode=walking")
                    } else {
                        Uri.parse("https://www.google.com/maps/dir/?api=1&destination=${Uri.encode(destination)}&travelmode=walking")
                    }
                    val intent = Intent(Intent.ACTION_VIEW, data)
                    try { context.startActivity(intent) } catch (_: Exception) {
                        locationStatus = "A mapping application is unavailable."
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().height(64.dp)
        ) { Text("Open walking route") }
        FilledTonalButton(
            onClick = { launcher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)) },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Update location") }
        Text("RAAHI does not invent route instructions. Spoken directions are provided by the selected map application.")
        FilledTonalButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("Back") }
    }
}

private fun readLocation(context: Context, onResult: (Double, Double, Float) -> Unit) {
    val client = LocationServices.getFusedLocationProviderClient(context)
    try {
        client.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) onResult(location.latitude, location.longitude, location.accuracy)
            }
            .addOnFailureListener { }
    } catch (_: SecurityException) { }
}

@Composable
fun VoiceAssistantScreen(application: RAAHIApplication, onNavigate: (String) -> Unit) {
    val context = LocalContext.current
    val settings = application.settingsRepository.get()
    var status by remember { mutableStateOf("Tap Listen, then say a command.") }
    var listening by remember { mutableStateOf(false) }
    val voice = remember { RAAHIVoiceEngine(context) }
    DisposableEffect(Unit) { onDispose { voice.shutdown() } }
    voice.onListeningChanged = { listening = it }
    voice.onError = { status = it }
    voice.onResult = { command ->
        when (VoiceCommandParser.parse(command)) {
            VoiceCommand.Navigation -> { status = "Opening navigation."; onNavigate(Routes.NAVIGATION) }
            VoiceCommand.OCR -> { status = "Opening text reader."; onNavigate(Routes.OCR) }
            VoiceCommand.Describe -> { status = "Opening surroundings camera."; onNavigate(Routes.OBSTACLE) }
            VoiceCommand.Emergency -> { status = "Opening emergency help."; onNavigate(Routes.EMERGENCY) }
            VoiceCommand.Stop -> { status = "Speech stopped."; voice.stop() }
            VoiceCommand.Repeat -> { status = "Repeated the current instruction."; voice.speak(status, settings.speechRate, settings.speechVolume) }
            is VoiceCommand.Unknown -> {
                status = "I heard: $command. Try navigation, read text, describe surroundings, stop voice, or emergency help."
                voice.speak(status, settings.speechRate, settings.speechVolume)
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {
        RaahiTopBar(title = "Voice Assistant")
        RaahiGlassCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(status, style = MaterialTheme.typography.titleMedium)
                RaahiVoiceWaveform(active = listening)
            }
        }
        RaahiAnimatedMicrophone(
            isListening = listening,
            onClick = {
                if (listening) voice.stop() else voice.listen(context)
            }
        )
        RaahiSecondaryButton(
            text = "Repeat",
            onClick = { voice.speak(status, settings.speechRate, settings.speechVolume) },
            modifier = Modifier.fillMaxWidth()
        )
        RaahiPrimaryButton(
            text = "Back",
            onClick = { onNavigate(Routes.HOME) },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun EmergencyScreen(application: RAAHIApplication, onBack: () -> Unit) {
    val context = LocalContext.current
    var locationText by remember { mutableStateOf("") }
    val settings = application.settingsRepository.get()
    var confirmCall by remember { mutableStateOf(false) }
    var confirmMessage by remember { mutableStateOf(false) }
    var confirmLocation by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("Emergency Help", style = MaterialTheme.typography.headlineLarge)
        Text("RAAHI will never contact emergency services automatically.")
        Text(if (settings.emergencyContact.isBlank()) "No emergency contact configured." else "Configured contact: ${settings.emergencyContact}")
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(confirmCall, { confirmCall = it })
            Text("Confirm opening a call to the configured contact")
        }
        Button(
            enabled = confirmCall && settings.emergencyContact.isNotBlank(),
            onClick = {
                context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:${Uri.encode(settings.emergencyContact)}")))
                confirmCall = false
            },
            modifier = Modifier.fillMaxWidth().height(64.dp)
        ) { Text("Open Call") }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(confirmMessage, { confirmMessage = it })
            Text("Confirm opening an emergency text")
        }
        Button(
            enabled = confirmMessage && settings.emergencyContact.isNotBlank(),
            onClick = {
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("smsto:${Uri.encode(settings.emergencyContact)}")
                    putExtra("sms_body", "I need help.")
                }
                context.startActivity(intent)
                confirmMessage = false
            },
            modifier = Modifier.fillMaxWidth().height(64.dp)
        ) { Text("Open Message") }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(confirmLocation, { confirmLocation = it })
            Text("Confirm sharing my current location")
        }
        Button(
            enabled = confirmLocation,
            onClick = {
                readLocation(context) { lat, lon, _ ->
                    locationText = "https://maps.google.com/?q=$lat,$lon"
                }
                if (locationText.isNotBlank()) {
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, "My current location: $locationText")
                    }
                    context.startActivity(Intent.createChooser(intent, "Share location only after consent"))
                } else Toast.makeText(context, "Current location is not yet available.", Toast.LENGTH_LONG).show()
                confirmLocation = false
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Share Location") }
        FilledTonalButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("Back") }
    }
}
