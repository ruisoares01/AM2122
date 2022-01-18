package com.example.projetoam2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projetoam2.Model.MessageType
import com.example.projetoam2.Model.TextMessage
import com.example.projetoam2.Model.User
import com.example.projetoam2.Utils.FirestoreUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.ViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import de.hdodenhof.circleimageview.CircleImageView
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

    val db = Firebase.firestore
    val auth = Firebase.auth

    private val adapter = GroupAdapter<ViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group)

        //action bar
        supportActionBar?.hide()

        var groupName = ""
        var groupId = ""
        var linkfoto = ""
        val bundle = intent.extras

        val backButton = findViewById<ImageView>(R.id.backButton1)
        backButton.setOnClickListener {
            finish()
        }

        val groupOptionsButton = findViewById<ImageButton>(R.id.opcoesGruposButton)

        //collect data
        bundle?.let {
            groupName = it.getString("name").toString()
            groupId = it.getString("uid").toString()
            linkfoto = it.getString("linkfoto").toString()
        }

        val groupImg = findViewById<CircleImageView>(R.id.groupImg)
        Picasso.get().load(linkfoto).into(groupImg)

        val nameGroup = findViewById<TextView>(R.id.textViewName1)
        nameGroup.text = groupName

        nameGroup.setOnClickListener {
            val intent = Intent(this, GroupProfile::class.java)
            intent.putExtra("name", groupName)
            intent.putExtra("uid", groupId)
            intent.putExtra("linkfoto", linkfoto)
            startActivity(intent)
        }

        groupImg.setOnClickListener {
            val intent = Intent(this, GroupProfile::class.java)
            intent.putExtra("name", groupName)
            intent.putExtra("uid", groupId)
            intent.putExtra("linkfoto", linkfoto)
            startActivity(intent)
        }

        groupOptionsButton.setOnClickListener {
            val intent = Intent(this, GroupOptionsActivity::class.java)
            intent.putExtra("name", groupName)
            intent.putExtra("uid", groupId)
            intent.putExtra("linkfoto", linkfoto)
            startActivity(intent)
        }

        val userIds : MutableList<String> = mutableListOf()

        //get the chat channel
        /*FirestoreUtil.createGroupChannel(userIds) { groupId ->


        }*/
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

            if(edit_text.text.toString() == "") {

            }else {

                val messageTosend =
                    TextMessage(string1.toString(), Calendar.getInstance().time,
                        FirebaseAuth.getInstance().currentUser!!.uid, MessageType.TEXT)

                edit_text.setText("")

                FirestoreUtil.sendGroupMessage(messageTosend, groupId)
            }


        }

        send_image.setOnClickListener {
            //send image
        }

        //action bar title, name of the user
        //supportActionBar?.title = nameGroup

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

    override fun onPause() {
        super.onPause()
        val uid = FirebaseAuth.getInstance().uid
        val user = User(uid.toString(), dados.nome, dados.email, dados.naluno, dados.curso, dados.morada, dados.linkfoto, false)

        db.collection("usuarios").document(uid.toString()).set(user)
            .addOnSuccessListener {
                println("Online")
            }
    }
    override fun onResume() {
        super.onResume()
        val uid = FirebaseAuth.getInstance().uid
        val user = User(uid.toString(), dados.nome, dados.email, dados.naluno, dados.curso, dados.morada, dados.linkfoto, true)
        db.collection("usuarios").document(uid.toString()).set(user)
            .addOnSuccessListener {
                println("Offline")
            }
    }
}