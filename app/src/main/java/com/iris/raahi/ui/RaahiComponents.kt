package com.iris.raahi.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.iris.raahi.theme.RaahiBlue
import com.iris.raahi.theme.RaahiCard
import com.iris.raahi.theme.RaahiCyan
import com.iris.raahi.theme.RaahiGreen
import com.iris.raahi.theme.RaahiMuted
import com.iris.raahi.theme.RaahiRed
import com.iris.raahi.theme.RaahiSecondary

@Composable
fun RaahiPrimaryButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(containerColor = RaahiBlue)
    ) { Text(text) }
}

@Composable
fun RaahiSecondaryButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = RaahiCyan)
    ) { Text(text) }
}

@Composable
fun RaahiGlassCard(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = modifier.border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = RaahiCard.copy(alpha = 0.92f)),
        content = content
    )
}

@Composable
fun RaahiFeatureCard(
    title: String,
    description: String,
    icon: ImageVector,
    accent: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    RaahiGlassCard(modifier = modifier) {
        androidx.compose.material3.Card(
            onClick = onClick,
            modifier = Modifier.fillMaxSize().semantics { contentDescription = "$title. $description" },
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.SpaceBetween) {
                Box(
                    Modifier.size(44.dp).background(accent.copy(alpha = 0.16f), CircleShape),
                    contentAlignment = Alignment.Center
                ) { Icon(icon, contentDescription = null, tint = accent) }
                Column {
                    Text(title, style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(4.dp))
                    Text(description, style = MaterialTheme.typography.bodyMedium, color = RaahiMuted)
                }
            }
        }
    }
}

@Composable
fun RaahiMetricCard(title: String, value: String, unit: String = "", modifier: Modifier = Modifier) {
    RaahiGlassCard(modifier = modifier) {
        Column(Modifier.padding(16.dp)) {
            Text(title, color = RaahiMuted, style = MaterialTheme.typography.labelLarge)
            Text(value, style = MaterialTheme.typography.headlineSmall)
            if (unit.isNotBlank()) Text(unit, color = RaahiCyan, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun RaahiStatusIndicator(label: String, connected: Boolean, modifier: Modifier = Modifier) {
    val color = if (connected) RaahiGreen else RaahiMuted
    Row(
        modifier = modifier.semantics { liveRegion = LiveRegionMode.Polite },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(Modifier.size(8.dp).background(color, CircleShape))
        Text(label, color = color, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
fun RaahiTopBar(title: String, onBack: (() -> Unit)? = null, trailing: @Composable (RowScope.() -> Unit)? = null) {
    Row(
        Modifier.fillMaxWidth().height(56.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(title, style = MaterialTheme.typography.titleLarge)
        if (trailing != null) Row(content = trailing)
    }
}

@Composable
fun RaahiSectionHeader(title: String, subtitle: String? = null) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(title, style = MaterialTheme.typography.titleMedium)
        if (subtitle != null) Text(subtitle, color = RaahiMuted, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun RaahiLoadingIndicator(label: String = "Loading") {
    Row(Modifier.semantics { liveRegion = LiveRegionMode.Polite }, verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        CircularProgressIndicator(Modifier.size(22.dp), color = RaahiCyan, strokeWidth = 2.dp)
        Text(label, color = RaahiMuted)
    }
}

@Composable
fun RaahiAnimatedMicrophone(isListening: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "microphone")
    val scale = if (isListening) transition.animateFloat(1f, 1.12f,
        infiniteRepeatable(tween(900, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "pulse").value else 1f
    androidx.compose.material3.FloatingActionButton(
        onClick = onClick,
        modifier = modifier.size((88 * scale).dp),
        shape = CircleShape,
        containerColor = if (isListening) RaahiRed else RaahiBlue,
        contentColor = Color.White
    ) {
        androidx.compose.material3.Icon(
            imageVector = Icons.Rounded.Mic,
            contentDescription = if (isListening) "Listening. Tap to stop." else "Listen"
        )
    }
}

@Composable
fun RaahiVoiceWaveform(active: Boolean, modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "waveform")
    val phase = transition.animateFloat(0f, 1f,
        infiniteRepeatable(tween(900, easing = LinearEasing), RepeatMode.Restart), label = "phase").value
    Canvas(modifier.height(48.dp).fillMaxWidth()) {
        val widths = floatArrayOf(0.25f, 0.55f, 0.35f, 0.8f, 0.45f, 0.65f, 0.3f)
        val spacing = size.width / (widths.size * 2)
        widths.forEachIndexed { index, ratio ->
            val animated = if (active) 0.35f + 0.65f * kotlin.math.abs(kotlin.math.sin((phase + index * 0.13f) * Math.PI)).toFloat() else ratio
            drawRoundRect(
                color = if (index % 2 == 0) RaahiCyan else RaahiBlue,
                topLeft = androidx.compose.ui.geometry.Offset(spacing * (index * 2 + 1), size.height * (1f - ratio * animated) / 2f),
                size = androidx.compose.ui.geometry.Size(spacing * 0.55f, size.height * ratio * animated),
                cornerRadius = CornerRadius(8f)
            )
        }
    }
}

@Composable
fun RaahiNavigationProgress(progress: Float, modifier: Modifier = Modifier) {
    Box(modifier.height(8.dp).clip(RoundedCornerShape(4.dp)).background(RaahiSecondary)) {
        Box(Modifier.fillMaxHeight().fillMaxWidth(progress.coerceIn(0f, 1f)).background(
            Brush.horizontalGradient(listOf(RaahiBlue, RaahiCyan))
        ))
    }
}

@Composable
fun RaahiWarningCard(title: String, message: String, modifier: Modifier = Modifier) {
    RaahiGlassCard(modifier = modifier.border(1.dp, RaahiRed.copy(alpha = 0.5f), RoundedCornerShape(24.dp)).semantics(mergeDescendants = true) {}) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(title, color = RaahiRed, style = MaterialTheme.typography.titleMedium)
            Text(message, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
