package com.lko.walkietalk.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Composable
fun AnimatedBackground(
    isTransmitting: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    var isAnimStarted by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { isAnimStarted = true }
    
    val color1: Color by animateColorAsState(
        targetValue = if (isAnimStarted) Color(0xFF203A43) else Color(0xFF0F2027),
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), 
        label = "color1"
    )
    
    val color2: Color by animateColorAsState(
        targetValue = if (isAnimStarted) Color(0xFF2C5364) else Color(0xFF203A43),
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), 
        label = "color2"
    )

    // When transmitting, add a soft pulsing glow effect by shifting the gradient colors
    val transmitGlow: Color by animateColorAsState(
        targetValue = if (isTransmitting) Color(0xFF00B4DB).copy(alpha = 0.3f) else Color.Transparent,
        animationSpec = tween(500), 
        label = "transmitGlow"
    )

    val gradientColors = if (isTransmitting) {
        listOf(color1, transmitGlow, color2)
    } else {
        listOf(color1, color2)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(colors = gradientColors))
    ) {
        content()
    }
}
