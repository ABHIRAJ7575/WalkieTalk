package com.lko.walkietalk.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.lko.walkietalk.audio.AudioController
import com.lko.walkietalk.firebase.SignalingClient
import com.lko.walkietalk.webrtc.WebRTCClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.webrtc.DataChannel
import org.webrtc.IceCandidate
import org.webrtc.MediaStream
import org.webrtc.PeerConnection
import org.webrtc.SdpObserver
import org.webrtc.SessionDescription

enum class RoomState {
    DISCONNECTED, WAITING, CONNECTED
}

class WalkieTalkieViewModel(application: Application) : AndroidViewModel(application) {

    private val signalingClient = SignalingClient()
    private var webRTCClient: WebRTCClient? = null
    private var audioController: AudioController? = null

    private val _roomState = MutableStateFlow(RoomState.DISCONNECTED)
    val roomState = _roomState.asStateFlow()

    private val _currentRoomId = MutableStateFlow("")
    val currentRoomId = _currentRoomId.asStateFlow()

    private val _isTransmitting = MutableStateFlow(false)
    val isTransmitting = _isTransmitting.asStateFlow()

    // Are we the caller (created the room)?
    private var isLocalCaller = false

    fun initWebRTC() {
        if (webRTCClient != null) return

        webRTCClient = WebRTCClient(getApplication(), object : PeerConnection.Observer {
            override fun onSignalingChange(state: PeerConnection.SignalingState?) {}
            override fun onConnectionChange(newState: PeerConnection.PeerConnectionState?) {
                if (newState == PeerConnection.PeerConnectionState.CONNECTED) {
                    _roomState.value = RoomState.CONNECTED
                } else if (newState == PeerConnection.PeerConnectionState.DISCONNECTED || newState == PeerConnection.PeerConnectionState.FAILED) {
                    _roomState.value = RoomState.DISCONNECTED
                }
            }
            override fun onIceConnectionChange(state: PeerConnection.IceConnectionState?) {
                if (state == PeerConnection.IceConnectionState.CONNECTED || state == PeerConnection.IceConnectionState.COMPLETED) {
                    _roomState.value = RoomState.CONNECTED
                } else if (state == PeerConnection.IceConnectionState.FAILED || state == PeerConnection.IceConnectionState.DISCONNECTED) {
                    _roomState.value = RoomState.DISCONNECTED
                }
            }
            override fun onIceConnectionReceivingChange(receiving: Boolean) {}
            override fun onIceGatheringChange(state: PeerConnection.IceGatheringState?) {}
            override fun onIceCandidate(candidate: IceCandidate) {
                // Send ICE candidate to Firebase
                if (currentRoomId.value.isNotEmpty()) {
                    signalingClient.sendIceCandidate(currentRoomId.value, candidate, isLocalCaller)
                }
            }
            override fun onIceCandidatesRemoved(candidates: Array<out IceCandidate>?) {}
            override fun onAddStream(stream: MediaStream?) {}
            override fun onRemoveStream(stream: MediaStream?) {}
            override fun onDataChannel(channel: DataChannel?) {}
            override fun onRenegotiationNeeded() {}
            override fun onAddTrack(receiver: org.webrtc.RtpReceiver?, mediaStreams: Array<out MediaStream>?) {}
        })

        audioController = AudioController(getApplication(), webRTCClient)
        audioController?.setSpeakerphone(true)
    }

    fun joinRoom(roomId: String) {
        initWebRTC()
        _currentRoomId.value = roomId

        viewModelScope.launch {
            // First check if room already has someone (is there an offer?)
            signalingClient.db.collection("rooms").document(roomId).get().addOnSuccessListener { snapshot ->
                val offer = snapshot.get("offer")
                if (offer != null) {
                    // Room exists and has caller. We will answer.
                    isLocalCaller = false
                    _roomState.value = RoomState.WAITING
                    // The snapshot listener handles the offer now
                } else {
                    // Create room as caller
                    isLocalCaller = true
                    _roomState.value = RoomState.WAITING
                    createOffer(roomId)
                }
            }

            var hasHandledOffer = false

            // Observe room updates
            launch {
                signalingClient.observeRoomStatus(roomId).collect { data ->
                    data?.let {
                        if (isLocalCaller && it.containsKey("answer")) {
                            val answerMap = it["answer"] as Map<String, String>
                            val answerSdp = SessionDescription(SessionDescription.Type.ANSWER, answerMap["sdp"])
                            webRTCClient?.setRemoteDescription(answerSdp)
                        } else if (!isLocalCaller && it.containsKey("offer") && !hasHandledOffer) {
                            hasHandledOffer = true
                            android.util.Log.d("WebRTC", "Offer received")
                            
                            val offerMap = it["offer"] as Map<String, String>
                            val offerSdp = SessionDescription(SessionDescription.Type.OFFER, offerMap["sdp"])
                            
                            webRTCClient?.setRemoteDescription(offerSdp)
                            webRTCClient?.answer(object : SdpObserver {
                                override fun onCreateSuccess(desc: SessionDescription?) {
                                    desc?.let { answer ->
                                        android.util.Log.d("WebRTC", "Answer created")
                                        webRTCClient?.setLocalDescription(answer)
                                        signalingClient.sendAnswer(roomId, answer)
                                    }
                                }
                                override fun onSetSuccess() {}
                                override fun onCreateFailure(error: String?) {}
                                override fun onSetFailure(error: String?) {}
                            })
                        }
                    }
                }
            }

            // Observe remote ICE candidates
            launch {
                signalingClient.observeIceCandidates(roomId, isLocalCaller).collect { candidate ->
                    webRTCClient?.addIceCandidate(candidate)
                    android.util.Log.d("WebRTC", "ICE candidate added")
                }
            }
        }
    }

    private fun createOffer(roomId: String) {
        webRTCClient?.call(object : SdpObserver {
            override fun onCreateSuccess(desc: SessionDescription?) {
                desc?.let {
                    webRTCClient?.setLocalDescription(it) // Set local description
                    signalingClient.sendOffer(roomId, it)
                }
            }
            override fun onSetSuccess() {}
            override fun onCreateFailure(error: String?) {}
            override fun onSetFailure(error: String?) {}
        })
    }

    // handleOffer(roomId) is removed as the logic is now in the snapshot listener

    fun setPttActive(active: Boolean) {
        _isTransmitting.value = active
        audioController?.setRecording(active)
    }

    override fun onCleared() {
        super.onCleared()
        webRTCClient?.onDestroy()
    }
}
