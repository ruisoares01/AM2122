package com.example.projetoam2.Utils

import android.content.Context
import android.util.Log
import com.example.projetoam2.Model.*
import com.example.projetoam2.item.TextMessageItem
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

    private val chatChannelIsCollectionRef = firestoreInstance.collection("Chat")

    private val groupChannelIsCollectionRef = firestoreInstance.collection("grupos")


    fun getOrCreateChatChannel(otherUserId: String, onComplete: (channelId: String) -> Unit) {
        //check which chat the user is in
        currentUserDocRef.collection("Salachat")
        //user we are chating... is the other user
            .document(otherUserId).get().addOnSuccessListener {
                //means we already chating
                if(it.exists()){
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
                    .collection("Salachat")
                    .document(otherUserId)
                    .set(mapOf("channelId" to newChannel.id))

                //get the others users document
                firestoreInstance.collection("usuarios").document(otherUserId)
                    .collection("Salachat")
                    .document(currentUserId)
                    .set(mapOf("channelId" to newChannel.id))

                onComplete(newChannel.id)
            }

    }

    fun createGroupChannel(userIds: MutableList<String>, onComplete: (grupoId: String) -> Unit) {
        //check which chat the user is in

        //if the chat channel doesn't exists we need to create it
        val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid

        val nome = "aspas"

        val imagemUrl = "https://firebasestorage.googleapis.com/v0/b/projetoam2.appspot.com/o/imagens%2F0fd24a87-038a-4e3c-ab01-8b9087c8959c?alt=media&token=518b667b-c0e3-41cd-bae3-98cef40c9669"


        //stores a document ref to the new chat channel even before its created in Firestore
        val newGroupChannel = groupChannelIsCollectionRef.document()
        newGroupChannel.set(GroupChannel(nome, imagemUrl, userIds))

        userIds.forEach {

            firestoreInstance.collection("usuarios")
                .document(it)
                .collection("grupo")
                .document(newGroupChannel.id)

            }

        onComplete(newGroupChannel.id)
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
                    return@forEach
                }
                onListen(items)
            }
    }

    //list for all the messages inside a channel
    fun addGroupMessagesListener(groupId: String, context: Context,
                                onListen: (List<Item>) -> Unit): ListenerRegistration {
        //get the document with name channel Id and get the collection of messages to order the messages by time
        return groupChannelIsCollectionRef.document(groupId).collection("mensagens")
            .orderBy("time")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    Log.e("FIRESTORE", "GroupMessagesListener error", firebaseFirestoreException)
                    return@addSnapshotListener
                }

                val items = mutableListOf<Item>()
                querySnapshot!!.documents.forEach {
                    if (it["type"] == MessageType.TEXT) {
                        items.add(TextMessageItem(it.toObject(TextMessage::class.java)!!, context))
                    } else {
                        //add image message
                    }
                    return@forEach
                }
                onListen(items)
            }
    }

    fun sendMessage(message: Message, channelId: String) {


        chatChannelIsCollectionRef.document(channelId)
            .collection("messages")
            .add(message)
    }

    fun sendGroupMessage(message: Message, groupId: String) {
        groupChannelIsCollectionRef.document(groupId)
            .collection("mensagens")
            .add(message)
    }
}

