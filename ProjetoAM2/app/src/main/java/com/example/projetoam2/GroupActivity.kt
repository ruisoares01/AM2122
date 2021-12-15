package com.example.projetoam2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projetoam2.Model.MessageType
import com.example.projetoam2.Model.TextMessage
import com.example.projetoam2.Utils.FirestoreUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.ViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.activity_chat.*
import java.util.*

class GroupActivity : AppCompatActivity() {

    private lateinit var messagesListenerRegistration : ListenerRegistration
    private var shouldInitRecyclerView = true
    private lateinit var messagesSection : Section

    private val adapter = GroupAdapter<ViewHolder>()

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
        FirestoreUtil.getOrCreateGroupChannel(otherUserId) { groupId ->

            messagesListenerRegistration =
                FirestoreUtil.addGroupMessagesListener(groupId, this, this::updateRecyclerView)

            send_msg_button.setOnClickListener {
                val messageTosend =
                    TextMessage(edit_text.text.toString(), Calendar.getInstance().time,
                        FirebaseAuth.getInstance().currentUser!!.uid, MessageType.TEXT)

                edit_text.setText("")

                FirestoreUtil.sendGroupMessage(messageTosend, groupId)
            }

            send_image.setOnClickListener {
                //send image
            }
        }

        //action bar title, name of the user
        supportActionBar?.title = otherUserName

    }
    private fun updateRecyclerView(mensagens: List<Item>) {
        fun init() {
            recyclerview_messages.apply {
                layoutManager = LinearLayoutManager(this@GroupActivity)
                adapter = GroupAdapter<ViewHolder>().apply {
                    messagesSection = Section(mensagens)
                    this.add(messagesSection)
                }
            }
            shouldInitRecyclerView = false
        }

        fun updateItems() = messagesSection.update(mensagens)

        if (shouldInitRecyclerView) {
            init()
        } else {
            updateItems()
        }
    }
}