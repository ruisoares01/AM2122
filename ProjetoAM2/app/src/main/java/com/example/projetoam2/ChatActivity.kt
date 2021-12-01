package com.example.projetoam2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.Toast
import com.example.projetoam2.Utils.AppUtils
import com.example.projetoam2.Utils.FirestoreUtil
import com.example.projetoam2.item.UserItem
import com.google.firebase.firestore.ListenerRegistration
import com.xwray.groupie.OnItemClickListener
import com.xwray.groupie.kotlinandroidextensions.Item

class ChatActivity : AppCompatActivity() {

    private lateinit var messagesListenerRegistration : ListenerRegistration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

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
            messagesListenerRegistration =
                FirestoreUtil.addChatMessagesListener(channelId, this, this::onMessagesChanged)
        }

        //action bar title, name of the user
        supportActionBar?.title = otherUserName
    }
    private fun onMessagesChanged(messages: List<Item>){
        Toast.makeText(this, "onMessagesChangedRunning!", Toast.LENGTH_SHORT).show()
    }
}