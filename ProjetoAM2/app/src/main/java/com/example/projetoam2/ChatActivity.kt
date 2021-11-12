package com.example.projetoam2

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projetoam2.Adapter.MessageAdapter
import com.example.projetoam2.Model.Message
import com.example.projetoam2.Model.UserId
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

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
            }
        }

        //function to add the message to the database
        fun enviar(){

            var uid = FirebaseAuth.getInstance().uid.toString()
            val message = messageBox.text.toString()
            val messageObject = Message(message, uid.toString(),receiverUid.toString())

            val novochat =  db.collection("chat").document()
            novochat.set(UserId(mutableListOf(uid,receiverUid.toString())))

            messageBox.setText("")
        }
}
