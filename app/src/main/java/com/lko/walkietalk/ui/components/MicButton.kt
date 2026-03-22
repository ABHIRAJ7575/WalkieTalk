package com.lko.walkietalk.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MicButton(
    isEnabled: Boolean,
    isTransmitting: Boolean,
    onPressed: () -> Unit,
    onReleased: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current

    // Spring animation for scale
    val scale by animateFloatAsState(
        targetValue = if (isTransmitting) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ), label = "buttonScale"
    )

    // Inner pulse animation when transmitting
    val infiniteTransition = rememberInfiniteTransition(label = "micPulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "pulseScale"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(240.dp)
    ) {
        // Outer pulsing glow
        if (isTransmitting) {
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .scale(pulseScale)
                    .clip(CircleShape)
                    .background(Color(0xFF00B4DB).copy(alpha = 0.2f))
            )
        }

        val buttonGradient = if (isTransmitting) {
            Brush.radialGradient(listOf(Color(0xFF00B4DB), Color(0xFF0083B0)))
        } else {
            Brush.radialGradient(listOf(Color(0xFF37474F), Color(0xFF263238)))
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(200.dp)
                .scale(scale)
                .shadow(if (isTransmitting) 4.dp else 12.dp, CircleShape)
                .clip(CircleShape)
                .background(buttonGradient)
                .border(
                    width = if (isTransmitting) 3.dp else 1.dp,
                    color = if (isTransmitting) Color(0xFF00E676) else Color(0xFF455A64),
                    shape = CircleShape
                )
                .pointerInput(isEnabled) {
                    if (!isEnabled) return@pointerInput
                    detectTapGestures(
                        onPress = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onPressed()
                            tryAwaitRelease()
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onReleased()
                        }
                    )
                }
        ) {
            Text(
                text = if (isTransmitting) "TRANSMITTING" else "PUSH TO TALK",
                color = if (isTransmitting) Color.White else Color.LightGray,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                letterSpacing = 1.sp
            )
        }
    }
}
