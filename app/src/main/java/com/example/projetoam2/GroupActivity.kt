package com.example.projetoam2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
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
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

class GroupActivity : AppCompatActivity() {

    private val secretKey = "tK5UTui+DPh8lIlBxya5XVsmeDCoUl6vHhdIESMB6sQ="
    private val salt = "QWlGNHNhMTJTQWZ2bGhpV3U="
    private val iv = "bVQzNFNhRkQ1Njc4UUFaWA=="

    private lateinit var messagesListenerRegistration : ListenerRegistration
    private var shouldInitRecyclerView = true
    private lateinit var messagesSection : Section

    private val adapter = GroupAdapter<ViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        //action bar
        supportActionBar?.hide()

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


        val userIds : MutableList<String> = mutableListOf()


        //get the chat channel
        FirestoreUtil.createGroupChannel(userIds) { groupId ->

            messagesListenerRegistration =
                FirestoreUtil.addGroupMessagesListener(groupId, this, this::updateRecyclerView)

            send_msg_button.setOnClickListener {

                val string1: String
                val string: String
                string = edit_text.getText().toString()


                val ivParameterSpec = IvParameterSpec(Base64.decode(iv, Base64.DEFAULT))

                val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
                val spec =  PBEKeySpec(secretKey.toCharArray(), Base64.decode(salt, Base64.DEFAULT), 10000, 256)
                val tmp = factory.generateSecret(spec)
                val secretKey =  SecretKeySpec(tmp.encoded, "AES")

                val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
                cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec)
                string1 = Base64.encodeToString(cipher.doFinal(string.toByteArray(Charsets.UTF_8)), Base64.DEFAULT)

                val messageTosend =
                    TextMessage(string1.toString(), Calendar.getInstance().time,
                        FirebaseAuth.getInstance().currentUser!!.uid, MessageType.TEXT)

                edit_text.setText("")

                FirestoreUtil.sendMessage(messageTosend, groupId)
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