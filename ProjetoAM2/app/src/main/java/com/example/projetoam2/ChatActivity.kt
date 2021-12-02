package com.example.projetoam2

<<<<<<< HEAD
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
=======
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projetoam2.Model.MessageType
import com.example.projetoam2.Model.TextMessage
import com.example.projetoam2.Utils.AppUtils
import com.example.projetoam2.Utils.FirestoreUtil
import com.example.projetoam2.item.UserItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.OnItemClickListener
import com.xwray.groupie.Section
import com.xwray.groupie.ViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.activity_login.*
import java.util.*

class ChatActivity : AppCompatActivity() {

    private lateinit var messagesListenerRegistration : ListenerRegistration
    private var shouldInitRecyclerView = true
    private lateinit var messagesSection : Section

    private val adapter = GroupAdapter<ViewHolder>()
>>>>>>> Rui

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

<<<<<<< HEAD
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
=======
        //action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        var otherUserName = ""
        var otherUserId = ""
        val bundle = intent.extras

        //collect data
        bundle?.let {
            otherUserName = it.getString("name").toString()
            otherUserId = it.getString("uid").toString()

        }

        //get the chat channel
        FirestoreUtil.getOrCreateChatChannel(otherUserId) { channelId ->

            println(otherUserId)

            messagesListenerRegistration =
                FirestoreUtil.addChatMessagesListener(channelId, this, this::updateRecyclerView)

            send_msg_button.setOnClickListener {
                val messageTosend =
                    TextMessage(edit_text.text.toString(), Calendar.getInstance().time,
                    FirebaseAuth.getInstance().currentUser!!.uid, MessageType.TEXT)

                edit_text.setText("")

                FirestoreUtil.sendMessage(messageTosend, channelId)

                println(channelId)
                println("AAAAAAAA")
            }
            send_image.setOnClickListener {
                //send image
            }
        }

        //action bar title, name of the user
        supportActionBar?.title = otherUserName

    }
    private fun updateRecyclerView(messages: List<Item>) {
        fun init() {
            recyclerview_messages.apply {
                layoutManager = LinearLayoutManager(this@ChatActivity)
                adapter = GroupAdapter<ViewHolder>().apply {
                    messagesSection = Section(messages)
                    this.add(messagesSection)
                }
            }
            shouldInitRecyclerView = false
        }

        fun updateItems() = messagesSection.update(messages)

        if (shouldInitRecyclerView) {
            init()
        } else {
            updateItems()
        }
    }
>>>>>>> Rui
}
