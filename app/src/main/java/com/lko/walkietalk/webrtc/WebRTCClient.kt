package com.lko.walkietalk.webrtc

import android.content.Context
import org.webrtc.AudioSource
import org.webrtc.AudioTrack
import org.webrtc.DefaultVideoDecoderFactory
import org.webrtc.DefaultVideoEncoderFactory
import org.webrtc.EglBase
import org.webrtc.IceCandidate
import org.webrtc.MediaConstraints
import org.webrtc.PeerConnection
import org.webrtc.PeerConnectionFactory
import org.webrtc.SessionDescription
import org.webrtc.audio.JavaAudioDeviceModule

class WebRTCClient(
    private val context: Context,
    private val observer: PeerConnection.Observer
) {
    private val rootEglBase: EglBase = EglBase.create()

    private val audioDeviceModule by lazy {
        JavaAudioDeviceModule.builder(context)
            .setUseHardwareAcousticEchoCanceler(true)
            .setUseHardwareNoiseSuppressor(true)
            .createAudioDeviceModule()
    }

    val factory: PeerConnectionFactory by lazy {
        PeerConnectionFactory.initialize(
            PeerConnectionFactory.InitializationOptions.builder(context)
                .setEnableInternalTracer(true)
                .createInitializationOptions()
        )
        PeerConnectionFactory.builder()
            .setVideoDecoderFactory(DefaultVideoDecoderFactory(rootEglBase.eglBaseContext))
            .setVideoEncoderFactory(DefaultVideoEncoderFactory(rootEglBase.eglBaseContext, true, true))
            .setAudioDeviceModule(audioDeviceModule)
            .createPeerConnectionFactory()
    }

    private val iceServer = listOf(
        PeerConnection.IceServer.builder("stun:stun.l.google.com:19302").createIceServer(),
        PeerConnection.IceServer.builder("stun:stun1.l.google.com:19302").createIceServer()
    )

    private val audioSource: AudioSource by lazy {
        factory.createAudioSource(MediaConstraints())
    }

    val localAudioTrack: AudioTrack by lazy {
        factory.createAudioTrack("WalkieTalkAudioTrack", audioSource)
    }

    val peerConnection: PeerConnection? by lazy {
        val configuration = PeerConnection.RTCConfiguration(iceServer)
        configuration.sdpSemantics = PeerConnection.SdpSemantics.UNIFIED_PLAN
        factory.createPeerConnection(configuration, observer)
    }

    init {
        // Create audio track when client is initialized
        localAudioTrack.setEnabled(false) // Initially muted
        peerConnection?.addTrack(localAudioTrack, listOf("WalkieTalkMediaStream"))
    }

    fun call(sdpObserver: org.webrtc.SdpObserver) {
        val constraints = MediaConstraints().apply {
            mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"))
            mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveVideo", "false"))
        }

        peerConnection?.createOffer(sdpObserver, constraints)
    }

    fun answer(sdpObserver: org.webrtc.SdpObserver) {
        val constraints = MediaConstraints().apply {
            mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"))
            mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveVideo", "false"))
        }

        peerConnection?.createAnswer(sdpObserver, constraints)
    }

    fun setRemoteDescription(sessionDescription: SessionDescription) {
        peerConnection?.setRemoteDescription(object : org.webrtc.SdpObserver {
            override fun onCreateSuccess(desc: SessionDescription?) {}
            override fun onSetSuccess() {}
            override fun onCreateFailure(error: String?) {}
            override fun onSetFailure(error: String?) {}
        }, sessionDescription)
    }

    fun setLocalDescription(sessionDescription: SessionDescription) {
        peerConnection?.setLocalDescription(object : org.webrtc.SdpObserver {
            override fun onCreateSuccess(desc: SessionDescription?) {}
            override fun onSetSuccess() {}
            override fun onCreateFailure(error: String?) {}
            override fun onSetFailure(error: String?) {}
        }, sessionDescription)
    }

    fun addIceCandidate(iceCandidate: IceCandidate) {
        peerConnection?.addIceCandidate(iceCandidate)
    }

    fun enableAudio(enable: Boolean) {
        localAudioTrack.setEnabled(enable)
    }

    fun onDestroy() {
        if (localAudioTrack != null) {
            localAudioTrack.dispose()
        }
        if (audioSource != null) {
            audioSource.dispose()
        }
        peerConnection?.close()
        factory.dispose()
        audioDeviceModule.release()
        rootEglBase.release()
    }
}
