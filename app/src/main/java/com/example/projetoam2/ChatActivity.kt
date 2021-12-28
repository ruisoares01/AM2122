package com.example.projetoam2

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projetoam2.Fragments.Users
import com.example.projetoam2.Model.MessageType
import com.example.projetoam2.Model.TextMessage
import com.example.projetoam2.Model.User
import com.example.projetoam2.Utils.AppUtils
import com.example.projetoam2.Utils.FirestoreUtil
import com.example.projetoam2.item.UserItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
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
import java.util.*

class ChatActivity : AppCompatActivity() {

    private lateinit var messagesListenerRegistration : ListenerRegistration
    private var shouldInitRecyclerView = true
    private lateinit var messagesSection : Section

    private val adapter = GroupAdapter<ViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        //action bar
      //  supportActionBar?.setDisplayHomeAsUpEnabled(true)
          supportActionBar?.hide()

        val backButton = findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener {
            finish()
        }

        var otherUserName = ""
        var otherUserId = ""
        var linkfoto = ""
        var otherUserEmail = ""
        var otherUserN = ""
        var otherUserCurso = ""
        var otherUserMorada = ""
        val bundle = intent.extras

        //collect data
        bundle?.let {
            otherUserName = it.getString("name").toString()
            otherUserId = it.getString("uid").toString()
            otherUserEmail = it.getString("email").toString()
            otherUserN = it.getString("nAluno").toString()
            otherUserCurso = it.getString("curso").toString()
            otherUserMorada = it.getString("morada").toString()
            linkfoto = it.getString("linkfoto").toString()

        }

        val imgprofile = findViewById<CircleImageView>(R.id.imgProfile)
        Picasso.get().load(linkfoto).into(imgprofile)

        val nameProfile = findViewById<TextView>(R.id.textViewName)
        nameProfile.text = otherUserName

        nameProfile.setOnClickListener {
            val intent = Intent(this, OtherProfile::class.java)
            intent.putExtra("name", otherUserName)
            intent.putExtra("uid", otherUserId)
            intent.putExtra("email", otherUserEmail)
            intent.putExtra("nAluno", otherUserN)
            intent.putExtra("curso", otherUserCurso)
            intent.putExtra("morada", otherUserMorada)
            intent.putExtra("linkfoto", linkfoto)
            startActivity(intent)
        }

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
            }

            send_image.setOnClickListener {
                //send image
            }
        }

        //action bar title, name of the user
     //   supportActionBar?.title = otherUserName

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
}
