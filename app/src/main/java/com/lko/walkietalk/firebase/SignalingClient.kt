package com.lko.walkietalk.firebase

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.webrtc.IceCandidate
import org.webrtc.SessionDescription

class SignalingClient(
    val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    fun sendOffer(roomId: String, sdp: SessionDescription) {
        val offerMap = hashMapOf(
            "sdp" to sdp.description,
            "type" to sdp.type.name
        )
        db.collection("rooms").document(roomId).set(hashMapOf("offer" to offerMap))
    }

    fun sendAnswer(roomId: String, sdp: SessionDescription) {
        val answerMap = hashMapOf(
            "sdp" to sdp.description,
            "type" to sdp.type.name
        )
        db.collection("rooms").document(roomId).update("answer", answerMap)
    }

    fun sendIceCandidate(roomId: String, candidate: IceCandidate, isLocal: Boolean) {
        val candidateMap = hashMapOf(
            "serverUrl" to candidate.serverUrl,
            "sdpMid" to candidate.sdpMid,
            "sdpMLineIndex" to candidate.sdpMLineIndex,
            "sdpCandidate" to candidate.sdp
        )
        val collection = if (isLocal) "callerCandidates" else "calleeCandidates"
        db.collection("rooms").document(roomId).collection(collection).add(candidateMap)
    }

    fun observeRoomStatus(roomId: String): Flow<Map<String, Any>?> = callbackFlow {
        val listener = db.collection("rooms").document(roomId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    trySend(snapshot.data)
                }
            }
        awaitClose { listener.remove() }
    }
    
    fun observeIceCandidates(roomId: String, isLocal: Boolean): Flow<IceCandidate> = callbackFlow {
        val collection = if (isLocal) "calleeCandidates" else "callerCandidates"
        val listener = db.collection("rooms").document(roomId).collection(collection)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                snapshot?.documentChanges?.forEach { change ->
                    if (change.type == com.google.firebase.firestore.DocumentChange.Type.ADDED) {
                        val data = change.document.data
                        val candidate = IceCandidate(
                            data["sdpMid"] as String,
                            (data["sdpMLineIndex"] as Long).toInt(),
                            data["sdpCandidate"] as String
                        )
                        trySend(candidate)
                    }
                }
            }
        awaitClose { listener.remove() }
    }
}
