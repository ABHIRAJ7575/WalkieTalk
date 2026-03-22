package com.lko.walkietalk.audio

import android.content.Context
import android.media.AudioManager
import com.lko.walkietalk.webrtc.WebRTCClient

class AudioController(
    private val context: Context,
    private val webRTCClient: WebRTCClient?
) {
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    
    fun setSpeakerphone(enable: Boolean) {
        audioManager.mode = AudioManager.MODE_IN_COMMUNICATION
        audioManager.isSpeakerphoneOn = enable
    }

    fun setRecording(enable: Boolean) {
        webRTCClient?.enableAudio(enable)
    }
}
