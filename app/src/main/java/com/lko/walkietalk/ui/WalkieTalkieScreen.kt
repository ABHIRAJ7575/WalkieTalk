package com.lko.walkietalk.ui

import android.media.AudioManager
import android.media.ToneGenerator
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lko.walkietalk.ui.components.*
import com.lko.walkietalk.viewmodel.RoomState

@Composable
fun WalkieTalkieScreen(
    roomId: String,
    roomState: RoomState,
    isTransmitting: Boolean,
    onPttPressed: () -> Unit,
    onPttReleased: () -> Unit
) {
    // Zero-delay simple tones using ToneGenerator (runs off main thread naturally or is extremely fast)
    val toneGenerator = remember { ToneGenerator(AudioManager.STREAM_MUSIC, 100) }

    // Play a beep when exactly connected
    LaunchedEffect(roomState) {
        if (roomState == RoomState.CONNECTED) {
            toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP, 100)
        }
    }

    AnimatedBackground(isTransmitting = isTransmitting) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 32.dp)
            ) {
                Text(
                    text = "ROOM $roomId",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 4.sp
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                StatusIndicator(roomState = roomState, isTransmitting = isTransmitting)
            }

            // PTT Button Area
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f)
            ) {
                WaveformView(
                    isTransmitting = isTransmitting,
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                MicButton(
                    isEnabled = roomState == RoomState.CONNECTED,
                    isTransmitting = isTransmitting,
                    onPressed = {
                        toneGenerator.startTone(ToneGenerator.TONE_DTMF_0, 50)
                        onPttPressed()
                    },
                    onReleased = {
                        toneGenerator.startTone(ToneGenerator.TONE_DTMF_0, 50)
                        onPttReleased()
                    }
                )
            }

            // Bottom Status / Signal Strength
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 32.dp)
            ) {
                val listeningText = if (roomState == RoomState.CONNECTED && !isTransmitting) "LISTENING..." else ""
                Text(
                    text = listeningText,
                    color = Color(0xFF00E676),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.height(24.dp),
                    letterSpacing = 2.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                SignalStrength(roomState = roomState)
            }
        }
    }
    
    DisposableEffect(Unit) {
        onDispose { toneGenerator.release() }
    }
}
