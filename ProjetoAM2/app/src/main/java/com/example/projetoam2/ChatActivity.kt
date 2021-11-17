package com.example.projetoam2

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projetoam2.Adapter.MessageAdapter
import com.example.projetoam2.Model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import io.grpc.InternalChannelz.id

class ChatActivity : AppCompatActivity() {

    //variaveis
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageBox: EditText
    private lateinit var sendButton: ImageView
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: ArrayList<Message>
    var receiverRoom: String? = null
    var senderRoom: String? = null

    lateinit var name: String
    lateinit var receiverUid: String


    private val currentUserDocRef: DocumentReference
        get() = firestoreInstance.document("usuarios/${FirebaseAuth.getInstance().currentUser?.uid
            ?: throw NullPointerException("UID is null")}")

    private val firestoreInstance: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    private val chatChannelCollectionRef = firestoreInstance.collection("chat")

    val db = Firebase.firestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        //id da pessoa que envia a menssagem
        val senderUid = FirebaseAuth.getInstance().currentUser?.uid

        var bundle : Bundle? = intent.extras
         name = bundle?.getString("name").toString()
         receiverUid = bundle?.getString("uid").toString()

        senderRoom = receiverUid + senderUid
        receiverRoom = senderUid + receiverUid

        //show the name on the bar
        supportActionBar?.title = name

        chatRecyclerView = findViewById(R.id.chatRecyclerView)
        messageBox = findViewById(R.id.messageBox)
        sendButton = findViewById(R.id.sendButton)
        messageList = ArrayList()
        messageAdapter = MessageAdapter(this, messageList)

        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatRecyclerView.adapter = messageAdapter

        // adding the message to the database
        sendButton.setOnClickListener {

                enviar()

            messageBox.setText("")
        }
    }

    //function to add the message to the database
    fun enviar(){

        var uid = FirebaseAuth.getInstance().uid.toString()
        val message = messageBox.text.toString()
        val messageObject = Message(message, uid.toString(),receiverUid.toString())

        currentUserDocRef.collection("chatChannels")
            .document(receiverUid.toString()).get().addOnSuccessListener { documents ->
                val idchat = documents.toObject(IdChat::class.java)
                if (idchat != null) {
                    chatChannelCollectionRef.document(idchat.id)
                        .collection("messages")
                        .add(messageObject).addOnSuccessListener {
                            messageBox.text.clear()
                        }
                }
                else{

                    db.collection("chat")
                        .document(receiverUid.toString()).get().addOnSuccessListener {documents ->

                                //get all the documents
                                for (document in documents) {
                                    val message = documents.toObject(Message::class.java)
                                    messageList.add(messageObject)

                                }
                                messageAdapter.notifyDataSetChanged()

                            val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid

                            val newChannel = chatChannelCollectionRef.document()
                            newChannel.set(ChatChannel(mutableListOf(currentUserId, receiverUid)))

                            currentUserDocRef
                                .collection("chatChannels")
                                .document(receiverUid)
                                .set(mapOf("channelId" to newChannel.id))

                            firestoreInstance.collection("usuarios").document(receiverUid)
                                .collection("chatChannels")
                                .document(currentUserId)
                                .set(mapOf("channelId" to newChannel.id))

                            chatChannelCollectionRef.document(idchat.id)
                                .collection("messages")
                                .add(messageObject).addOnSuccessListener {
                                    messageBox.text.clear()
                                }
                        }
                }
            }

    }
}
