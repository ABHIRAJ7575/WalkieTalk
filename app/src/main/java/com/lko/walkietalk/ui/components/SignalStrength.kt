package com.lko.walkietalk.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.lko.walkietalk.viewmodel.RoomState

@Composable
fun SignalStrength(
    roomState: RoomState,
    modifier: Modifier = Modifier
) {
    val activeBars = when (roomState) {
        RoomState.WAITING -> 1
        RoomState.CONNECTED -> 4
        RoomState.DISCONNECTED -> 0
    }
    
    val infiniteTransition = rememberInfiniteTransition(label = "signalPulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "alpha"
    )

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        for (i in 0 until 4) {
            val isActive = i < activeBars
            val barHeight = 8.dp + (i * 4).dp
            val barAlpha = if (isActive && roomState == RoomState.CONNECTED) alpha else if (isActive) 1f else 0.3f
            val color = if (roomState == RoomState.DISCONNECTED) Color.Red else Color(0xFF4CAF50)
            
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(barHeight)
                    .clip(RoundedCornerShape(2.dp))
                    .background(color.copy(alpha = if (isActive) barAlpha else 0.3f))
            )
        }
    }
}
