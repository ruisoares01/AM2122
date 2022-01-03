package com.example.projetoam2

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projetoam2.Model.MessageType
import com.example.projetoam2.Model.TextMessage
import com.example.projetoam2.Notifications.FirebaseService
import com.example.projetoam2.Notifications.NotificationData
import com.example.projetoam2.Notifications.PushNotification
import com.example.projetoam2.Notifications.RetrofitInstance
import com.example.projetoam2.Utils.AppUtils
import com.example.projetoam2.Utils.FirestoreUtil
import com.example.projetoam2.item.UserItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.OnItemClickListener
import com.xwray.groupie.Section
import com.xwray.groupie.ViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.item_text_message.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

const val TOPIC = "/topics/myTopic2"

class ChatActivity : AppCompatActivity() {

    private lateinit var messagesListenerRegistration : ListenerRegistration
    private var shouldInitRecyclerView = true
    private lateinit var messagesSection : Section

    private val adapter = GroupAdapter<ViewHolder>()

    val TAG = "ChatActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        FirebaseService.sharedPref = getSharedPreferences("sharedPref", MODE_PRIVATE)
        /*FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
            FirebaseService.token = it.token
            etToken.setText(it.token)
        }*/

        //action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        var otherUserName = ""
        var otherUserId = ""
        var linkfoto = ""
        val bundle = intent.extras
        //collect data
        bundle?.let {
            otherUserName = it.getString("name").toString()
            otherUserId = it.getString("uid").toString()
            linkfoto = it.getString("linkfoto").toString()

        }

        val imgprofile = findViewById<CircleImageView>(R.id.imgProfile)
        Picasso.get().load(linkfoto).into(imgprofile)

        val nameProfile = findViewById<TextView>(R.id.textViewName)
        nameProfile.text = otherUserName

        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)

        //get the chat channel
        FirestoreUtil.getOrCreateChatChannel(otherUserId) { channelId ->

            messagesListenerRegistration =
                FirestoreUtil.addChatMessagesListener(channelId, this, this::updateRecyclerView)

            send_msg_button.setOnClickListener {
                val messageTosend =
                    TextMessage(edit_text.text.toString(), Calendar.getInstance().time,
                    FirebaseAuth.getInstance().currentUser!!.uid, MessageType.TEXT)

                edit_text.setText("")

                FirestoreUtil.sendMessage(messageTosend, channelId)

                val title = edit_text.text.toString()
                val message = textview_message_text.text.toString()
                val recipientToken = text_token.text.toString()
                if(title.isNotEmpty() && message.isNotEmpty() && recipientToken.isNotEmpty()) {
                    PushNotification(
                        NotificationData(title, message),
                        recipientToken
                    ).also {
                        sendNotification(it)
                    }
                }
            }

            send_image.setOnClickListener {
                //send image
            }
        }

        //action bar title, name of the user
       // supportActionBar?.title = otherUserName

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

    private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitInstance.api.postNotification(notification)
            if(response.isSuccessful) {
                Log.d(TAG, "Response: ${Gson().toJson(response)}")
            } else {
                Log.e(TAG, response.errorBody().toString())
            }
        } catch(e: Exception) {
            Log.e(TAG, e.toString())
        }
    }
}
