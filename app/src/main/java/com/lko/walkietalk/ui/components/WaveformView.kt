package com.lko.walkietalk.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun WaveformView(
    isTransmitting: Boolean,
    modifier: Modifier = Modifier
) {
    val numBars = 7
    val barHeights = remember { mutableStateListOf<Float>().apply { repeat(numBars) { add(10f) } } }

    LaunchedEffect(isTransmitting) {
        if (isTransmitting) {
            while (true) {
                for (i in 0 until numBars) {
                    barHeights[i] = Random.nextFloat() * 30f + 10f
                }
                delay(150)
            }
        } else {
            for (i in 0 until numBars) {
                barHeights[i] = 10f
            }
        }
    }

    Row(
        modifier = modifier.height(50.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until numBars) {
            val animatedHeight by animateDpAsState(
                targetValue = barHeights[i].dp,
                animationSpec = tween(durationMillis = 150),
                label = "waveformHeight"
            )
            val barColor = if (isTransmitting) Color(0xFF00B4DB) else Color.DarkGray

            Box(
                modifier = Modifier
                    .width(6.dp)
                    .height(animatedHeight)
                    .clip(RoundedCornerShape(3.dp))
                    .background(barColor)
            )
        }
    }
}
