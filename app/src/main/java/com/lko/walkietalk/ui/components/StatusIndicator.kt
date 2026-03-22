package com.lko.walkietalk.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lko.walkietalk.viewmodel.RoomState

@Composable
fun StatusIndicator(
    roomState: RoomState,
    isTransmitting: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "statusDot")
    
    val dotAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isTransmitting) 300 else 800, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "dotAlpha"
    )

    val (statusColor, statusText) = when {
        isTransmitting -> Color(0xFF00E676) to "TRANSMITTING"
        roomState == RoomState.CONNECTED -> Color(0xFF00E676) to "CONNECTED" // Cyan-green for connected
        roomState == RoomState.WAITING -> Color(0xFFFFC107) to "WAITING" // Yellow for waiting
        else -> Color(0xFFFF5252) to "DISCONNECTED" // Red for disconnected
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(statusColor.copy(alpha = if (roomState != RoomState.DISCONNECTED) dotAlpha else 1f))
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = statusText,
            color = statusColor,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
