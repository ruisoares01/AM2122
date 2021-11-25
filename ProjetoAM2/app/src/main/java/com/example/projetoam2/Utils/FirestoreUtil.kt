package com.example.projetoam2.Utils

import android.content.ClipData
import android.content.Context
import android.util.Log
import com.example.projetoam2.Model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.xwray.groupie.kotlinandroidextensions.Item
import java.lang.NullPointerException

object FirestoreUtil {

    private val firestoreInstance: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    private val currentUserDocRef: DocumentReference
        get() = firestoreInstance.document("usuarios/${FirebaseAuth.getInstance().currentUser?.uid
            ?: throw  NullPointerException("UID is null")}")

    private val chatChannelIsCollectionRef = firestoreInstance.collection("ChatChannels")


    fun getOrCreateChatChannel(otherUserId: String, onComplete: (channelId: String) -> Unit) {

        //check which chat the user is in
        currentUserDocRef.collection("engagedChatChannels")
        //user we are chating... is the other user
            .document(otherUserId).get().addOnSuccessListener {
                //means we already chating
                if(it.exists())
                    onComplete(it["channelId"] as String)
                return@addOnSuccessListener
            }
        //if the chat channel doesn't exists we need to create it
        val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid

        //stores a document ref to the new chat channel even before its created in Firestore
        val newChannel = chatChannelIsCollectionRef.document()
        newChannel.set(ChatChannel(mutableListOf(currentUserId, otherUserId)))

        //save the channel id in users who will chat together
        currentUserDocRef
            .collection("engagedChatChannels")
            .document(otherUserId)
            .set(mapOf("channelId" to newChannel.id))

        //get the others users document
        firestoreInstance.collection("usuarios").document(otherUserId)
            .collection("engagedChatChannels")
            .document(currentUserId)
            .set(mapOf("channelId" to newChannel.id))

        onComplete(newChannel.id)
    }

    //list for all the messages inside a channel
    fun addChatMessagesListener(channelId: String, context: Context,
                                onListen: (List<Item>) -> Unit): ListenerRegistration {
        //get the document with name channel Id and get the collection of messages to order the messages by time
        return chatChannelIsCollectionRef.document(channelId).collection("messages")
            .orderBy("time")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    Log.e("FIRESTORE", "ChatMessagesListener error", firebaseFirestoreException)
                    return@addSnapshotListener
                }

                val items = mutableListOf<Item>()
                querySnapshot!!.documents.forEach {
                    if (it["type"] == MessageType.TEXT) {
                        items.add(TextMessageItem(it.toObject(TextMessage::class.java)!!, context))
                    } else {
                        //add image message
                    }
                }
                onListen(items)
            }
    }
}